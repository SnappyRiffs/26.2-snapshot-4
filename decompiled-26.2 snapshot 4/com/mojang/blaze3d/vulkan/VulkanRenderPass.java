/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.vulkan.KHRPushDescriptor
 *  org.lwjgl.vulkan.KHRSynchronization2
 *  org.lwjgl.vulkan.VK12
 *  org.lwjgl.vulkan.VkBufferViewCreateInfo
 *  org.lwjgl.vulkan.VkCommandBuffer
 *  org.lwjgl.vulkan.VkDescriptorBufferInfo
 *  org.lwjgl.vulkan.VkDescriptorBufferInfo$Buffer
 *  org.lwjgl.vulkan.VkDescriptorImageInfo
 *  org.lwjgl.vulkan.VkDescriptorImageInfo$Buffer
 *  org.lwjgl.vulkan.VkDevice
 *  org.lwjgl.vulkan.VkRect2D
 *  org.lwjgl.vulkan.VkRect2D$Buffer
 *  org.lwjgl.vulkan.VkViewport
 *  org.lwjgl.vulkan.VkViewport$Buffer
 *  org.lwjgl.vulkan.VkWriteDescriptorSet
 *  org.lwjgl.vulkan.VkWriteDescriptorSet$Buffer
 */
package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vulkan.Destroyable;
import com.mojang.blaze3d.vulkan.VulkanBindGroupLayout;
import com.mojang.blaze3d.vulkan.VulkanConst;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuBuffer;
import com.mojang.blaze3d.vulkan.VulkanGpuSampler;
import com.mojang.blaze3d.vulkan.VulkanGpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanQueryPool;
import com.mojang.blaze3d.vulkan.VulkanRenderPipeline;
import com.mojang.blaze3d.vulkan.VulkanUtils;
import java.nio.LongBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRPushDescriptor;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferViewCreateInfo;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

public class VulkanRenderPass
implements RenderPassBackend {
    public static final boolean VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
    private final VulkanDevice device;
    private final Consumer<Destroyable> garbageQueue;
    private final VkCommandBuffer primaryCommandBuffer;
    final Supplier<VkCommandBuffer> secondaryCommandBufferSupplier;
    private final RenderPass.RenderArea renderArea;
    private final int outputWidth;
    private final int outputHeight;
    private final boolean hasDepth;
    protected int pushedDebugGroups = 0;
    private @Nullable VkCommandBuffer currentSecondaryCommandBuffer;
    protected @Nullable VulkanRenderPipeline pipeline;
    private boolean anyDescriptorDirty = false;
    protected final HashMap<String, GpuBufferSlice> uniforms = new HashMap();
    protected final HashMap<String, TextureViewAndSampler> textures = new HashMap();

    public VulkanRenderPass(VulkanDevice device, Consumer<Destroyable> garbageQueue, VkCommandBuffer primaryCommandBuffer, Supplier<VkCommandBuffer> secondaryCommandBufferSupplier, RenderPass.RenderArea renderArea, int outputWidth, int outputHeight, boolean hasDepth) {
        this.device = device;
        this.garbageQueue = garbageQueue;
        this.primaryCommandBuffer = primaryCommandBuffer;
        this.secondaryCommandBufferSupplier = secondaryCommandBufferSupplier;
        this.renderArea = renderArea;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.hasDepth = hasDepth;
    }

    public void end() {
        this.endSecondaryCommandBuffer();
    }

    public VkCommandBuffer allocateTransientRenderpassCommandBuffer() {
        return this.secondaryCommandBufferSupplier.get();
    }

    private VkCommandBuffer secondaryCommandBuffer() {
        if (this.currentSecondaryCommandBuffer == null) {
            this.currentSecondaryCommandBuffer = this.allocateTransientRenderpassCommandBuffer();
            try (MemoryStack stack = MemoryStack.stackPush();){
                VkViewport.Buffer viewport = VkViewport.calloc((int)1, (MemoryStack)stack);
                viewport.x(0.0f);
                viewport.y(0.0f);
                viewport.width((float)this.outputWidth);
                viewport.height((float)this.outputHeight);
                viewport.minDepth(0.0f);
                viewport.maxDepth(1.0f);
                VK12.vkCmdSetViewport((VkCommandBuffer)this.currentSecondaryCommandBuffer, (int)0, (VkViewport.Buffer)viewport);
                VulkanRenderPass.setScissor(stack, this.currentSecondaryCommandBuffer, this.renderArea.x(), this.renderArea.y(), this.renderArea.width(), this.renderArea.height());
            }
        }
        return this.currentSecondaryCommandBuffer;
    }

    private void endSecondaryCommandBuffer() {
        if (this.currentSecondaryCommandBuffer == null) {
            return;
        }
        VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer((VkCommandBuffer)this.currentSecondaryCommandBuffer), "Failed to end VkCommandBuffer");
        VK12.vkCmdExecuteCommands((VkCommandBuffer)this.primaryCommandBuffer, (VkCommandBuffer)this.currentSecondaryCommandBuffer);
        this.currentSecondaryCommandBuffer = null;
    }

    public void executeCommandBuffer(VkCommandBuffer commandBuffer) {
        this.endSecondaryCommandBuffer();
        VK12.vkCmdExecuteCommands((VkCommandBuffer)this.primaryCommandBuffer, (VkCommandBuffer)commandBuffer);
    }

    @Override
    public void pushDebugGroup(Supplier<String> label) {
        ++this.pushedDebugGroups;
        this.device.instance().debug().beginDebugGroup(this.secondaryCommandBuffer(), label);
    }

    @Override
    public void popDebugGroup() {
        if (this.pushedDebugGroups == 0) {
            throw new IllegalStateException("Can't pop more debug groups than was pushed!");
        }
        --this.pushedDebugGroups;
        this.device.instance().debug().endDebugGroup(this.secondaryCommandBuffer());
    }

    @Override
    public void setPipeline(RenderPipeline pipeline) {
        this.pipeline = this.device.getOrCompilePipeline(pipeline);
        if (!this.pipeline.isValid()) {
            throw new IllegalStateException("Pipeline is not valid (may contain invalid shaders?)");
        }
        this.anyDescriptorDirty = true;
        VK12.vkCmdBindPipeline((VkCommandBuffer)this.secondaryCommandBuffer(), (int)0, (long)(this.hasDepth ? this.pipeline.withDepthPipeline() : this.pipeline.withoutDepthPipeline()));
    }

    @Override
    public void bindTexture(String name, @Nullable GpuTextureView textureView, @Nullable GpuSampler sampler) {
        if (textureView == null || sampler == null) {
            if (textureView != null || sampler != null) {
                throw new IllegalArgumentException();
            }
            this.textures.remove(name);
            return;
        }
        this.textures.put(name, new TextureViewAndSampler((VulkanGpuTextureView)textureView, (VulkanGpuSampler)sampler));
        this.anyDescriptorDirty = true;
    }

    @Override
    public void setUniform(String name, GpuBuffer value) {
        this.uniforms.put(name, value.slice());
        this.anyDescriptorDirty = true;
    }

    @Override
    public void setUniform(String name, GpuBufferSlice value) {
        this.uniforms.put(name, value);
        this.anyDescriptorDirty = true;
    }

    @Override
    public void enableScissor(int x, int y, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            VulkanRenderPass.setScissor(stack, this.secondaryCommandBuffer(), x, y, width, height);
        }
    }

    private static void setScissor(MemoryStack stack, VkCommandBuffer commandBuffer, int x, int y, int width, int height) {
        VkRect2D.Buffer scissor = VkRect2D.calloc((int)1, (MemoryStack)stack);
        scissor.offset().set(x, y);
        scissor.extent().set(width, height);
        VK12.vkCmdSetScissor((VkCommandBuffer)commandBuffer, (int)0, (VkRect2D.Buffer)scissor);
    }

    @Override
    public void disableScissor() {
        this.enableScissor(this.renderArea.x(), this.renderArea.y(), this.renderArea.width(), this.renderArea.height());
    }

    @Override
    public void setVertexBuffer(int slot, GpuBuffer vertexBuffer) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            VK12.vkCmdBindVertexBuffers((VkCommandBuffer)this.secondaryCommandBuffer(), (int)slot, (LongBuffer)stack.longs(((VulkanGpuBuffer)vertexBuffer).vkBuffer()), (LongBuffer)stack.longs(0L));
        }
    }

    @Override
    public void setIndexBuffer(GpuBuffer indexBuffer, VertexFormat.IndexType indexType) {
        int type = switch (indexType) {
            default -> throw new MatchException(null, null);
            case VertexFormat.IndexType.SHORT -> 0;
            case VertexFormat.IndexType.INT -> 1;
        };
        VK12.vkCmdBindIndexBuffer((VkCommandBuffer)this.secondaryCommandBuffer(), (long)((VulkanGpuBuffer)indexBuffer).vkBuffer(), (long)0L, (int)type);
    }

    @Override
    public void drawIndexed(int baseVertex, int firstIndex, int indexCount, int instanceCount) {
        if (this.pipeline == null || !this.pipeline.isValid()) {
            throw new IllegalStateException("Pipeline is missing or not valid");
        }
        this.pushDescriptors();
        VK12.vkCmdDrawIndexed((VkCommandBuffer)this.secondaryCommandBuffer(), (int)indexCount, (int)instanceCount, (int)firstIndex, (int)baseVertex, (int)0);
    }

    @Override
    public <T> void drawMultipleIndexed(Collection<RenderPass.Draw<T>> draws, @Nullable GpuBuffer defaultIndexBuffer, @Nullable VertexFormat.IndexType defaultIndexType, Collection<String> dynamicUniforms, T uniformArgument) {
        for (RenderPass.Draw<T> draw : draws) {
            BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer = draw.uniformUploaderConsumer();
            if (uniformUploaderConsumer != null) {
                uniformUploaderConsumer.accept(uniformArgument, this::setUniform);
            }
            assert (draw.indexBuffer() != null || defaultIndexBuffer != null);
            assert (draw.indexType() != null || defaultIndexType != null);
            this.setIndexBuffer(draw.indexBuffer() == null ? defaultIndexBuffer : draw.indexBuffer(), draw.indexType() == null ? defaultIndexType : draw.indexType());
            this.setVertexBuffer(0, draw.vertexBuffer());
            this.drawIndexed(draw.baseVertex(), draw.firstIndex(), draw.indexCount(), 1);
        }
    }

    @Override
    public void draw(int firstVertex, int vertexCount) {
        if (this.pipeline == null || !this.pipeline.isValid()) {
            return;
        }
        this.pushDescriptors();
        VK12.vkCmdDraw((VkCommandBuffer)this.secondaryCommandBuffer(), (int)vertexCount, (int)1, (int)firstVertex, (int)vertexCount);
    }

    private void pushDescriptors() {
        if (!this.anyDescriptorDirty) {
            return;
        }
        if (VALIDATION) {
            for (BindGroupLayout.UniformDescription uniform : BindGroupLayout.flattenUniforms(this.pipeline.info().getBindGroupLayouts())) {
                GpuBufferSlice value = this.uniforms.get(uniform.name());
                if (value == null) {
                    throw new IllegalStateException("Missing uniform " + uniform.name() + " (should be " + String.valueOf((Object)uniform.type()) + ")");
                }
                if (uniform.type() == UniformType.UNIFORM_BUFFER) {
                    if (value.buffer().isClosed()) {
                        throw new IllegalStateException("Uniform buffer " + uniform.name() + " is already closed");
                    }
                    if ((value.buffer().usage() & 0x80) == 0) {
                        throw new IllegalStateException("Uniform buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM");
                    }
                }
                if (uniform.type() != UniformType.TEXEL_BUFFER) continue;
                if (value.offset() != 0L || value.length() != value.buffer().size()) {
                    throw new IllegalStateException("Uniform texel buffers do not support a slice of a buffer, must be entire buffer");
                }
                if ((value.buffer().usage() & 0x100) == 0) {
                    throw new IllegalStateException("Uniform texel buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER");
                }
                if (uniform.gpuFormat() != null) continue;
                throw new IllegalStateException("Invalid uniform texel buffer " + uniform.name() + " (missing a texture format)");
            }
        }
        assert (this.pipeline != null);
        VulkanBindGroupLayout layout = this.pipeline.layout();
        try (MemoryStack stack = MemoryStack.stackPush();){
            VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc((int)layout.entries().size(), (MemoryStack)stack);
            for (int i = 0; i < layout.entries().size(); ++i) {
                Record value;
                VulkanBindGroupLayout.Entry entry = layout.entries().get(i);
                VkWriteDescriptorSet set = ((VkWriteDescriptorSet)writes.get()).sType$Default();
                set.dstBinding(i);
                set.dstArrayElement(0);
                set.descriptorCount(1);
                if (entry.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.UNIFORM_BUFFER) {
                    GpuBufferSlice buffer = this.uniforms.get(entry.name());
                    if (buffer == null) {
                        throw new IllegalStateException("Missing uniform " + entry.name() + " (should be " + String.valueOf((Object)entry.type()) + ")");
                    }
                    VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc((int)1, (MemoryStack)stack);
                    bufferInfo.buffer(((VulkanGpuBuffer)buffer.buffer()).vkBuffer());
                    bufferInfo.offset(buffer.offset());
                    bufferInfo.range(buffer.length());
                    set.descriptorType(6);
                    set.pBufferInfo(bufferInfo);
                    continue;
                }
                if (entry.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.SAMPLED_IMAGE) {
                    value = this.textures.get(entry.name());
                    if (value == null) {
                        throw new IllegalStateException("Missing sampler " + entry.name());
                    }
                    VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc((int)1, (MemoryStack)stack);
                    imageInfo.sampler(((TextureViewAndSampler)value).sampler.vkSampler());
                    imageInfo.imageView(((TextureViewAndSampler)value).view.vkImageView());
                    imageInfo.imageLayout(1);
                    set.descriptorType(1);
                    set.pImageInfo(imageInfo);
                    continue;
                }
                if (entry.type() != VulkanBindGroupLayout.VulkanBindGroupEntryType.TEXEL_BUFFER) continue;
                value = this.uniforms.get(entry.name());
                if (value == null) {
                    throw new IllegalStateException("Missing uniform " + entry.name() + " (should be " + String.valueOf((Object)entry.type()) + ")");
                }
                LongBuffer bufferViewPtr = stack.callocLong(1);
                try (MemoryStack memoryStack = stack.push();){
                    assert (entry.texelBufferFormat() != null);
                    VkBufferViewCreateInfo viewCreateInfo = VkBufferViewCreateInfo.calloc((MemoryStack)stack).sType$Default();
                    viewCreateInfo.buffer(((VulkanGpuBuffer)((GpuBufferSlice)value).buffer()).vkBuffer());
                    viewCreateInfo.offset(((GpuBufferSlice)value).offset());
                    viewCreateInfo.range(((GpuBufferSlice)value).length());
                    viewCreateInfo.format(VulkanConst.toVk(entry.texelBufferFormat()));
                    VulkanUtils.crashIfFailure(VK12.vkCreateBufferView((VkDevice)this.device.vkDevice(), (VkBufferViewCreateInfo)viewCreateInfo, null, (LongBuffer)bufferViewPtr), "Couldn't create buffer view for texel buffer");
                    long bufferViewHandle = bufferViewPtr.get(0);
                    this.garbageQueue.accept(() -> VK12.vkDestroyBufferView((VkDevice)this.device.vkDevice(), (long)bufferViewHandle, null));
                }
                set.descriptorType(4);
                set.pTexelBufferView(bufferViewPtr);
            }
            KHRPushDescriptor.vkCmdPushDescriptorSetKHR((VkCommandBuffer)this.secondaryCommandBuffer(), (int)0, (long)this.pipeline.pipelineLayout(), (int)0, (VkWriteDescriptorSet.Buffer)((VkWriteDescriptorSet.Buffer)writes.flip()));
        }
        this.anyDescriptorDirty = false;
    }

    @Override
    public void writeTimestamp(GpuQueryPool pool, int index) {
        long queryPool = ((VulkanQueryPool)pool).vkQueryPool();
        VK12.vkResetQueryPool((VkDevice)this.device.vkDevice(), (long)queryPool, (int)index, (int)1);
        KHRSynchronization2.vkCmdWriteTimestamp2KHR((VkCommandBuffer)this.secondaryCommandBuffer(), (long)65536L, (long)queryPool, (int)index);
    }

    protected record TextureViewAndSampler(VulkanGpuTextureView view, VulkanGpuSampler sampler) {
    }
}

