/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class MovingBlockFeatureRenderer {
    public void renderSolid(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        this.render(nodeCollection, context, false);
    }

    public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        this.render(nodeCollection, context, true);
    }

    private void render(SubmitNodeCollection nodeCollection, FeatureFrameContext context, boolean translucent) {
        PoseStack poseStack = new PoseStack();
        MultiBufferSource bufferSource = context.bufferSource();
        BlockQuadOutput output = (x, y, z, quad, instance) -> MovingBlockFeatureRenderer.putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, quad.materialInfo().layer());
        BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> MovingBlockFeatureRenderer.putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
        Minecraft minecraft = Minecraft.getInstance();
        boolean ambientOcclusion = context.options().ambientOcclusion;
        boolean cutoutLeaves = context.options().cutoutLeaves;
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, minecraft.getBlockColors());
        for (Submit submit : nodeCollection.getMovingBlockSubmits()) {
            MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
            BlockState blockState = movingBlockRenderState.blockState;
            BlockStateModel model = context.blockStateModelSet().get(blockState);
            if (model.hasMaterialFlag(1) != translucent) continue;
            poseStack.setIdentity();
            poseStack.mulPose(submit.pose());
            BlockQuadOutput blockOutput = ModelBlockRenderer.forceOpaque(cutoutLeaves, blockState) ? solidOutput : output;
            long blockSeed = blockState.getSeed(movingBlockRenderState.randomSeedPos);
            blockRenderer.tesselateBlock(blockOutput, 0.0f, 0.0f, 0.0f, movingBlockRenderState, movingBlockRenderState.blockPos, blockState, model, blockSeed);
        }
    }

    private static void putBakedQuad(PoseStack poseStack, MultiBufferSource bufferSource, float x, float y, float z, BakedQuad quad, QuadInstance instance, ChunkSectionLayer layer) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        VertexConsumer buffer = bufferSource.getBuffer(switch (layer) {
            default -> throw new MatchException(null, null);
            case ChunkSectionLayer.SOLID -> RenderTypes.solidMovingBlock();
            case ChunkSectionLayer.CUTOUT -> RenderTypes.cutoutMovingBlock();
            case ChunkSectionLayer.TRANSLUCENT -> RenderTypes.translucentMovingBlock();
        });
        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }

    public record Submit(Matrix4fc pose, MovingBlockRenderState movingBlockRenderState) {
    }
}

