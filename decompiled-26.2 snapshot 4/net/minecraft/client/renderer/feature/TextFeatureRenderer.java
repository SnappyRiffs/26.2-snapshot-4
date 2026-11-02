/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class TextFeatureRenderer {
  public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
    Font font = context.font();
    GlyphRenderer glyphRenderer = new GlyphRenderer(context.bufferSource());
    for (Submit submit : nodeCollection.getTextSubmits()) {
      glyphRenderer.pose.set(submit.pose());
      glyphRenderer.lightCoords = submit.lightCoords();
      glyphRenderer.displayMode = submit.displayMode();
      if (submit.outlineColor() == 0) {
        Font.PreparedText text =
            font.prepareText(
                submit.string(),
                submit.x(),
                submit.y(),
                submit.color(),
                submit.dropShadow(),
                false,
                submit.backgroundColor());
        text.visit(glyphRenderer);
        continue;
      }
      Font.PreparedText outline =
          font.prepare8xTextOutline(submit.string(), submit.x(), submit.y(), submit.outlineColor());
      Font.PreparedText text =
          font.prepareText(
              submit.string(), submit.x(), submit.y(), submit.color(), false, false, 0);
      glyphRenderer.displayMode = Font.DisplayMode.NORMAL;
      outline.visit(glyphRenderer);
      glyphRenderer.displayMode = Font.DisplayMode.POLYGON_OFFSET;
      text.visit(glyphRenderer);
    }
  }

  private static class GlyphRenderer implements Font.GlyphVisitor {
    private final MultiBufferSource bufferSource;
    private final Matrix4f pose = new Matrix4f();
    private int lightCoords = 0xF000F0;
    private Font.DisplayMode displayMode = Font.DisplayMode.NORMAL;

    private GlyphRenderer(MultiBufferSource bufferSource) {
      this.bufferSource = bufferSource;
    }

    @Override
    public void acceptRenderable(TextRenderable renderable) {
      VertexConsumer buffer = this.bufferSource.getBuffer(renderable.renderType(this.displayMode));
      renderable.render((Matrix4fc) this.pose, buffer, this.lightCoords, false);
    }
  }

  public record Submit(
      Matrix4fc pose,
      float x,
      float y,
      FormattedCharSequence string,
      boolean dropShadow,
      Font.DisplayMode displayMode,
      int lightCoords,
      int color,
      int backgroundColor,
      int outlineColor) {}
}
