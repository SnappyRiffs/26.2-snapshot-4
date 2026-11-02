/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.joml.Matrix4fStack
 */
package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.client.renderer.StagedVertexBuffer;
import org.joml.Matrix4fStack;

public class RenderType {
  private static final int MEGABYTE = 0x100000;
  public static final int BIG_BUFFER_SIZE = 0x400000;
  public static final int SMALL_BUFFER_SIZE = 786432;
  public static final int TRANSIENT_BUFFER_SIZE = 1536;
  private final RenderSetup state;
  private final Optional<RenderType> outline;
  protected final String name;

  private RenderType(String name, RenderSetup state) {
    this.name = name;
    this.state = state;
    this.outline =
        state.outlineProperty == RenderSetup.OutlineProperty.AFFECTS_OUTLINE
            ? state.textures.values().stream()
                .findFirst()
                .map(
                    texture ->
                        RenderTypes.OUTLINE.apply(texture.location(), state.pipeline.isCull()))
            : Optional.empty();
  }

  static RenderType create(String name, RenderSetup state) {
    return new RenderType(name, state);
  }

  public String toString() {
    return "RenderType[" + this.name + ":" + String.valueOf(this.state) + "]";
  }

  public boolean hasBlending() {
    return this.state.pipeline.getColorTargetState().blendFunction().isPresent();
  }

  public OutputTarget outputTarget() {
    return this.state.outputTarget;
  }

  public void drawFromBuffer(StagedVertexBuffer.ExecuteInfo info) {
    this.drawFromBuffer(
        info.vertexBuffer(),
        info.indexBuffer(),
        info.indexType(),
        info.baseVertex(),
        info.firstIndex(),
        info.indexCount());
  }

  public void drawFromBuffer(
      GpuBuffer vertexBuffer,
      GpuBuffer indexBuffer,
      VertexFormat.IndexType indexType,
      int baseVertex,
      int firstIndex,
      int indexCount) {
    GpuTextureView colorTexture;
    Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
    Consumer<Matrix4fStack> modelViewModifier = this.state.layeringTransform.getModifier();
    if (modelViewModifier != null) {
      modelViewStack.pushMatrix();
      modelViewModifier.accept(modelViewStack);
    }
    GpuBufferSlice dynamicTransforms =
        RenderSystem.getDynamicUniforms()
            .writeTransform(
                RenderSystem.getModelViewMatrixCopy(), this.state.textureTransform.createMatrix());
    Map<String, RenderSetup.TextureAndSampler> textures = this.state.getTextures();
    RenderTarget renderTarget = this.state.outputTarget.getRenderTarget();
    GpuTextureView gpuTextureView =
        colorTexture =
            RenderSystem.outputColorTextureOverride != null
                ? RenderSystem.outputColorTextureOverride
                : renderTarget.getColorTextureView();
    GpuTextureView depthTexture =
        renderTarget.useDepth
            ? (RenderSystem.outputDepthTextureOverride != null
                ? RenderSystem.outputDepthTextureOverride
                : renderTarget.getDepthTextureView())
            : null;
    try (RenderPass renderPass =
        RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
                () -> "Immediate draw for " + this.name,
                colorTexture,
                OptionalInt.empty(),
                depthTexture,
                OptionalDouble.empty()); ) {
      renderPass.setPipeline(this.state.pipeline);
      ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
      if (scissorState.enabled()) {
        renderPass.enableScissor(
            scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
      }
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.setUniform("DynamicTransforms", dynamicTransforms);
      renderPass.setVertexBuffer(0, vertexBuffer);
      for (Map.Entry<String, RenderSetup.TextureAndSampler> entry : textures.entrySet()) {
        renderPass.bindTexture(
            entry.getKey(), entry.getValue().textureView(), entry.getValue().sampler());
      }
      renderPass.setIndexBuffer(indexBuffer, indexType);
      renderPass.drawIndexed(baseVertex, firstIndex, indexCount, 1);
    }
    if (modelViewModifier != null) {
      modelViewStack.popMatrix();
    }
  }

  public VertexFormat format() {
    return this.state.pipeline.getVertexFormat();
  }

  public VertexFormat.Mode mode() {
    return this.state.pipeline.getVertexFormatMode();
  }

  public Optional<RenderType> outline() {
    return this.outline;
  }

  public boolean isOutline() {
    return this.state.outlineProperty == RenderSetup.OutlineProperty.IS_OUTLINE;
  }

  public RenderPipeline pipeline() {
    return this.state.pipeline;
  }

  public boolean affectsCrumbling() {
    return this.state.affectsCrumbling;
  }

  public boolean canConsolidateConsecutiveGeometry() {
    return !this.mode().connectedPrimitives;
  }

  public boolean sortOnUpload() {
    return this.state.sortOnUpload;
  }
}
