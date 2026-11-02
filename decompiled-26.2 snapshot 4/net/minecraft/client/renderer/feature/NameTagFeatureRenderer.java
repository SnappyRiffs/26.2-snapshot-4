/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class NameTagFeatureRenderer {
    public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        Font.PreparedText preparedText;
        GlyphRenderer glyphRenderer = new GlyphRenderer(context.bufferSource());
        Storage storage = nodeCollection.getNameTagSubmits();
        storage.nameTagSubmitsSeethrough.sort(Comparator.comparing(Submit::distanceToCameraSq).reversed());
        for (Submit nameTag : storage.nameTagSubmitsSeethrough) {
            preparedText = NameTagFeatureRenderer.prepareText(context.font(), nameTag);
            glyphRenderer.prepare(nameTag, Font.DisplayMode.SEE_THROUGH);
            preparedText.visit(glyphRenderer);
        }
        for (Submit nameTag : storage.nameTagSubmitsNormal) {
            preparedText = NameTagFeatureRenderer.prepareText(context.font(), nameTag);
            glyphRenderer.prepare(nameTag, Font.DisplayMode.NORMAL);
            preparedText.visit(glyphRenderer);
        }
    }

    private static Font.PreparedText prepareText(Font font, Submit nameTag) {
        return font.prepareText(nameTag.text().getVisualOrderText(), nameTag.x(), nameTag.y(), nameTag.color(), false, false, nameTag.backgroundColor());
    }

    private static class GlyphRenderer
    implements Font.GlyphVisitor {
        private final MultiBufferSource bufferSource;
        private final Matrix4f pose = new Matrix4f();
        private int lightCoords = 0xF000F0;
        private Font.DisplayMode displayMode = Font.DisplayMode.NORMAL;

        private GlyphRenderer(MultiBufferSource bufferSource) {
            this.bufferSource = bufferSource;
        }

        public void prepare(Submit submit, Font.DisplayMode displayMode) {
            this.pose.set(submit.pose());
            this.lightCoords = submit.lightCoords();
            this.displayMode = displayMode;
        }

        @Override
        public void acceptRenderable(TextRenderable renderable) {
            VertexConsumer buffer = this.bufferSource.getBuffer(renderable.renderType(this.displayMode));
            renderable.render((Matrix4fc)this.pose, buffer, this.lightCoords, false);
        }
    }

    public static class Storage {
        private final List<Submit> nameTagSubmitsSeethrough = new ArrayList<Submit>();
        private final List<Submit> nameTagSubmitsNormal = new ArrayList<Submit>();

        public void add(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera) {
            if (nameTagAttachment == null) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            poseStack.pushPose();
            poseStack.translate(nameTagAttachment.x, nameTagAttachment.y + 0.5, nameTagAttachment.z);
            poseStack.mulPose((Quaternionfc)camera.orientation);
            poseStack.scale(0.025f, -0.025f, 0.025f);
            Matrix4f pose = new Matrix4f((Matrix4fc)poseStack.last().pose());
            float x = (float)(-minecraft.font.width(name)) / 2.0f;
            int backgroundColor = (int)(minecraft.gameRenderer.gameRenderState().optionsRenderState.getBackgroundOpacity(0.25f) * 255.0f) << 24;
            if (seeThrough) {
                this.nameTagSubmitsNormal.add(new Submit((Matrix4fc)pose, x, offset, name, LightCoordsUtil.lightCoordsWithEmission(lightCoords, 2), -1, 0));
                this.nameTagSubmitsSeethrough.add(new Submit((Matrix4fc)pose, x, offset, name, lightCoords, -2130706433, backgroundColor));
            } else {
                this.nameTagSubmitsNormal.add(new Submit((Matrix4fc)pose, x, offset, name, lightCoords, -2130706433, backgroundColor));
            }
            poseStack.popPose();
        }

        public void clear() {
            this.nameTagSubmitsNormal.clear();
            this.nameTagSubmitsSeethrough.clear();
        }
    }

    public record Submit(Matrix4fc pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor) implements TranslucentSubmit
    {
        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
        }
    }
}

