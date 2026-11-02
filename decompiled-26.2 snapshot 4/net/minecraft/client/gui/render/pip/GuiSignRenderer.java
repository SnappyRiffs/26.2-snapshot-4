/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.GuiSignRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;

public class GuiSignRenderer extends PictureInPictureRenderer<GuiSignRenderState> {
  private final SpriteGetter sprites;

  public GuiSignRenderer(SpriteGetter sprites) {
    this.sprites = sprites;
  }

  @Override
  public Class<GuiSignRenderState> getRenderStateClass() {
    return GuiSignRenderState.class;
  }

  @Override
  protected void renderToTexture(
      GuiSignRenderState renderState,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector) {
    Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
    poseStack.translate(0.0f, -0.75f, 0.0f);
    SpriteId sprite = Sheets.getSignSprite(renderState.woodType());
    submitNodeCollector.submitModel(
        renderState.signModel(),
        Unit.INSTANCE,
        poseStack,
        0xF000F0,
        OverlayTexture.NO_OVERLAY,
        -1,
        sprite,
        this.sprites,
        0,
        null);
  }

  @Override
  protected String getTextureLabel() {
    return "sign";
  }
}
