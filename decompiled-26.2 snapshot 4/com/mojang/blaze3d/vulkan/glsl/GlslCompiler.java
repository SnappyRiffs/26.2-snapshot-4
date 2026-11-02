/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.shaderc.Shaderc
 */
package com.mojang.blaze3d.vulkan.glsl;

import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.vulkan.VulkanBindGroupLayout;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.glsl.IntermediaryShaderModule;
import com.mojang.blaze3d.vulkan.glsl.ShaderCompileException;
import com.mojang.blaze3d.vulkan.glsl.SpvSampler;
import com.mojang.blaze3d.vulkan.glsl.SpvUniformBuffer;
import com.mojang.blaze3d.vulkan.glsl.SpvVariable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.ShaderDefines;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.Shaderc;

public class GlslCompiler
implements AutoCloseable {
    private final long shaderCompiler = Shaderc.shaderc_compiler_initialize();
    private final long shaderOptions = Shaderc.shaderc_compile_options_initialize();
    private final ShaderDefines globalDefines;

    public GlslCompiler() {
        Shaderc.shaderc_compile_options_set_target_env((long)this.shaderOptions, (int)0, (int)0x402000);
        Shaderc.shaderc_compile_options_set_auto_bind_uniforms((long)this.shaderOptions, (boolean)true);
        Shaderc.shaderc_compile_options_set_auto_map_locations((long)this.shaderOptions, (boolean)true);
        Shaderc.shaderc_compile_options_set_generate_debug_info((long)this.shaderOptions);
        Shaderc.shaderc_compile_options_set_optimization_level((long)this.shaderOptions, (int)0);
        this.globalDefines = ShaderDefines.builder().define("gl_VertexID", "gl_VertexIndex").define("gl_InstanceID", "gl_InstanceIndex").build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IntermediaryShaderModule createIntermediary(String filename, String source, ShaderType type) throws ShaderCompileException {
        source = GlslPreprocessor.injectDefines(source, this.globalDefines);
        int shaderType = type == ShaderType.FRAGMENT ? 1 : 0;
        long result = Shaderc.shaderc_compile_into_spv((long)this.shaderCompiler, (CharSequence)source, (int)shaderType, (CharSequence)filename, (CharSequence)"main", (long)this.shaderOptions);
        try {
            int status = Shaderc.shaderc_result_get_compilation_status((long)result);
            if (status != 0) {
                throw new ShaderCompileException("Couldn't parse GLSL: " + Shaderc.shaderc_result_get_error_message((long)result));
            }
            ByteBuffer spirv = Shaderc.shaderc_result_get_bytes((long)result);
            ByteBuffer copy = MemoryUtil.memCalloc((int)spirv.remaining());
            MemoryUtil.memCopy((ByteBuffer)spirv, (ByteBuffer)copy);
            IntermediaryShaderModule intermediaryShaderModule = IntermediaryShaderModule.createFromSpirv(filename, copy);
            return intermediaryShaderModule;
        }
        finally {
            Shaderc.shaderc_result_release((long)result);
        }
    }

    public CompiledModules compile(VulkanDevice device, RenderPipeline pipeline, IntermediaryShaderModule vertex, IntermediaryShaderModule fragment, List<String> vertexInputs) throws ShaderCompileException {
        String pipelineName = pipeline.getLocation().toString();
        ArrayList<VulkanBindGroupLayout.Entry> entries = new ArrayList<VulkanBindGroupLayout.Entry>();
        GlslCompiler.addToBindGroup(entries, vertex, pipeline);
        GlslCompiler.addToBindGroup(entries, fragment, pipeline);
        ArrayList<String> vertexOutputNames = new ArrayList<String>();
        for (SpvVariable output : vertex.outputs()) {
            vertexOutputNames.add(output.name());
        }
        vertex.rebind(vertexInputs, entries);
        fragment.rebind(vertexOutputNames, entries);
        long vertexId = vertex.createVulkanShaderModule(device);
        long fragmentId = fragment.createVulkanShaderModule(device);
        VulkanBindGroupLayout layout = VulkanBindGroupLayout.create(device, entries, pipelineName);
        return new CompiledModules(vertexId, fragmentId, layout);
    }

    @Override
    public void close() {
        Shaderc.shaderc_compile_options_release((long)this.shaderOptions);
        Shaderc.shaderc_compiler_release((long)this.shaderCompiler);
    }

    private static void addToBindGroup(List<VulkanBindGroupLayout.Entry> entries, IntermediaryShaderModule shader, RenderPipeline pipeline) throws ShaderCompileException {
        Optional<BindGroupLayout.UniformDescription> uniformDescription;
        String name;
        for (SpvUniformBuffer buffer : shader.uniformBuffers()) {
            name = buffer.name();
            uniformDescription = BindGroupLayout.flattenUniforms(pipeline.getBindGroupLayouts()).stream().filter(d -> d.name().equals(name)).findFirst();
            if (uniformDescription.isEmpty()) {
                throw new ShaderCompileException("Unable to find shader defined uniform (" + name + ")");
            }
            if (!entries.stream().noneMatch(e -> e.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.UNIFORM_BUFFER && e.name().equals(name))) continue;
            entries.add(new VulkanBindGroupLayout.Entry(VulkanBindGroupLayout.VulkanBindGroupEntryType.UNIFORM_BUFFER, name, null));
        }
        for (SpvSampler sampler : shader.samplers()) {
            name = sampler.name();
            uniformDescription = BindGroupLayout.flattenUniforms(pipeline.getBindGroupLayouts()).stream().filter(d -> d.name().equals(name)).findFirst();
            if (uniformDescription.isPresent()) {
                if (sampler.dimensions() != 5) {
                    throw new ShaderCompileException("UTB (" + name + ") must have type of SpvDimBuffer");
                }
                if (!entries.stream().noneMatch(e -> e.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.TEXEL_BUFFER && e.name().equals(name))) continue;
                entries.add(new VulkanBindGroupLayout.Entry(VulkanBindGroupLayout.VulkanBindGroupEntryType.TEXEL_BUFFER, name, uniformDescription.get().gpuFormat()));
                continue;
            }
            if (BindGroupLayout.flattenSamplers(pipeline.getBindGroupLayouts()).stream().noneMatch(name::equals)) {
                throw new ShaderCompileException("Unable to find shader defined uniform (" + name + ")");
            }
            if (sampler.dimensions() != 1 && sampler.dimensions() != 3) {
                throw new ShaderCompileException("Sampled texture (" + name + ") must have type of SpvDim2D or SpvDimCube");
            }
            if (!entries.stream().noneMatch(e -> e.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.SAMPLED_IMAGE && e.name().equals(name))) continue;
            entries.add(new VulkanBindGroupLayout.Entry(VulkanBindGroupLayout.VulkanBindGroupEntryType.SAMPLED_IMAGE, name, null));
        }
    }

    public record CompiledModules(long vertex, long fragment, VulkanBindGroupLayout layout) {
    }
}

