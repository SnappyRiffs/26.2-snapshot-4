/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

public class ItemFeatureRenderer {
  public static final Identifier ENCHANTED_GLINT_ARMOR =
      Identifier.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
  public static final Identifier ENCHANTED_GLINT_ITEM =
      Identifier.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
  private static final float SPECIAL_FOIL_UI_SCALE = 0.5f;
  private static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75f;
  private static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125f;
  public static final int NO_TINT = -1;
  private final QuadInstance quadInstance = new QuadInstance();

  public void renderSolid(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
    for (Submit submit : nodeCollection.getItemSubmits()) {
      if (ItemFeatureRenderer.hasTranslucency(submit)) continue;
      this.renderItem(context.bufferSource(), context.outlineBufferSource(), submit);
    }
  }

  public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
    for (Submit submit : nodeCollection.getItemSubmits()) {
      if (!ItemFeatureRenderer.hasTranslucency(submit)) continue;
      this.renderItem(context.bufferSource(), context.outlineBufferSource(), submit);
    }
  }

  private static boolean hasTranslucency(Submit submit) {
    for (BakedQuad quad : submit.quads()) {
      if (!quad.materialInfo().itemRenderType().hasBlending()) continue;
      return true;
    }
    return false;
  }

  private void renderItem(
      MultiBufferSource bufferSource, OutlineBufferSource outlineBufferSource, Submit submit) {
    PoseStack.Pose pose = submit.pose();
    ItemStackRenderState.FoilType foilType = submit.foilType();
    PoseStack.Pose foilDecalPose =
        foilType == ItemStackRenderState.FoilType.SPECIAL
            ? ItemFeatureRenderer.computeFoilDecalPose(submit.displayContext(), pose)
            : null;
    this.quadInstance.setLightCoords(submit.lightCoords());
    this.quadInstance.setOverlayCoords(submit.overlayCoords());
    if (submit.outlineColor() != 0) {
      outlineBufferSource.setColor(submit.outlineColor());
    }
    for (BakedQuad quad : submit.quads()) {
      BakedQuad.MaterialInfo material = quad.materialInfo();
      RenderType renderType = material.itemRenderType();
      this.quadInstance.setColor(
          ItemFeatureRenderer.getLayerColorSafe(submit.tintLayers(), material));
      if (foilType != ItemStackRenderState.FoilType.NONE) {
        VertexConsumer foilBuffer =
            ItemFeatureRenderer.getFoilBuffer(bufferSource, renderType, foilDecalPose);
        foilBuffer.putBakedQuad(pose, quad, this.quadInstance);
      }
      if (submit.outlineColor() != 0) {
        outlineBufferSource.getBuffer(renderType).putBakedQuad(pose, quad, this.quadInstance);
      }
      bufferSource.getBuffer(renderType).putBakedQuad(pose, quad, this.quadInstance);
    }
  }

  private static VertexConsumer getFoilBuffer(
      MultiBufferSource bufferSource,
      RenderType renderType,
      @Nullable PoseStack.Pose foilDecalPose) {
    RenderType foilRenderType =
        ItemFeatureRenderer.useTransparentGlint(renderType)
            ? RenderTypes.glintTranslucent()
            : RenderTypes.glint();
    VertexConsumer foilBuffer = bufferSource.getBuffer(foilRenderType);
    if (foilDecalPose != null) {
      foilBuffer = new SheetedDecalTextureGenerator(foilBuffer, foilDecalPose, 0.0078125f);
    }
    return foilBuffer;
  }

  private static PoseStack.Pose computeFoilDecalPose(ItemDisplayContext type, PoseStack.Pose pose) {
    PoseStack.Pose foilDecalPose = pose.copy();
    if (type == ItemDisplayContext.GUI) {
      MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.5f);
    } else if (type.firstPerson()) {
      MatrixUtil.mulComponentWise(foilDecalPose.pose(), 0.75f);
    }
    return foilDecalPose;
  }

  private static boolean useTransparentGlint(RenderType renderType) {
    return Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency()
        && renderType.outputTarget() == OutputTarget.ITEM_ENTITY_TARGET;
  }

  private static int getLayerColorSafe(int[] layers, int layer) {
    if (layer < 0 || layer >= layers.length) {
      return -1;
    }
    return layers[layer];
  }

  private static int getLayerColorSafe(int[] tintLayers, BakedQuad.MaterialInfo material) {
    if (material.isTinted()) {
      return ItemFeatureRenderer.getLayerColorSafe(tintLayers, material.tintIndex());
    }
    return -1;
  }

  public record Submit(
      PoseStack.Pose pose,
      ItemDisplayContext displayContext,
      int lightCoords,
      int overlayCoords,
      int outlineColor,
      int[] tintLayers,
      List<BakedQuad> quads,
      ItemStackRenderState.FoilType foilType) {}
}
