/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ShapeOutlineFeatureRenderer {
    public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context, boolean afterTerrain) {
        Vector3f normal = new Vector3f();
        for (Submit submit : nodeCollection.getShapeOutlineSubmits()) {
            if (submit.afterTerrain() != afterTerrain) continue;
            PoseStack.Pose pose = submit.pose();
            int color = submit.color();
            float width = submit.width();
            VertexConsumer builder = context.bufferSource().getBuffer(submit.renderType());
            submit.shape().forAllEdges((x1, y1, z1, x2, y2, z2) -> {
                normal.set((float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1)).normalize();
                builder.addVertex(pose, (float)x1, (float)y1, (float)z1).setColor(color).setNormal(pose, (Vector3fc)normal).setLineWidth(width);
                builder.addVertex(pose, (float)x2, (float)y2, (float)z2).setColor(color).setNormal(pose, (Vector3fc)normal).setLineWidth(width);
            });
        }
    }

    public record Submit(PoseStack.Pose pose, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
    }
}

