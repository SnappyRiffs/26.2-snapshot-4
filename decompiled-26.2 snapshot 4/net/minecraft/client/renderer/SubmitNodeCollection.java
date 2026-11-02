/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.BlockModelFeatureRenderer;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.feature.GizmoFeatureRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.LeashFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.MovingBlockFeatureRenderer;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.ShadowFeatureRenderer;
import net.minecraft.client.renderer.feature.ShapeOutlineFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class SubmitNodeCollection
implements OrderedSubmitNodeCollector {
    private final List<ShadowFeatureRenderer.Submit> shadowSubmits = new ArrayList<ShadowFeatureRenderer.Submit>();
    private final List<FlameFeatureRenderer.Submit> flameSubmits = new ArrayList<FlameFeatureRenderer.Submit>();
    private final NameTagFeatureRenderer.Storage nameTagSubmits = new NameTagFeatureRenderer.Storage();
    private final List<TextFeatureRenderer.Submit> textSubmits = new ArrayList<TextFeatureRenderer.Submit>();
    private final List<LeashFeatureRenderer.Submit> leashSubmits = new ArrayList<LeashFeatureRenderer.Submit>();
    private final List<MovingBlockFeatureRenderer.Submit> movingBlockSubmits = new ArrayList<MovingBlockFeatureRenderer.Submit>();
    private final List<BlockModelFeatureRenderer.Submit> blockModelSubmits = new ArrayList<BlockModelFeatureRenderer.Submit>();
    private final List<ShapeOutlineFeatureRenderer.Submit> shapeOutlineSubmits = new ArrayList<ShapeOutlineFeatureRenderer.Submit>();
    private final List<ItemFeatureRenderer.Submit> itemSubmits = new ArrayList<ItemFeatureRenderer.Submit>();
    private final List<QuadParticleRenderState> quadParticleGroups = new ArrayList<QuadParticleRenderState>();
    private final ModelFeatureRenderer.Storage modelSubmits = new ModelFeatureRenderer.Storage();
    private final CustomFeatureRenderer.Storage customGeometrySubmits = new CustomFeatureRenderer.Storage();
    private final List<GizmoFeatureRenderer.Submit> gizmoSubmits = new ArrayList<GizmoFeatureRenderer.Submit>();
    private final SubmitNodeStorage submitNodeStorage;
    private boolean wasUsed = false;

    public SubmitNodeCollection(SubmitNodeStorage submitNodeStorage) {
        this.submitNodeStorage = submitNodeStorage;
    }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) {
        this.wasUsed = true;
        PoseStack.Pose pose = poseStack.last();
        this.shadowSubmits.add(new ShadowFeatureRenderer.Submit((Matrix4fc)new Matrix4f((Matrix4fc)pose.pose()), radius, pieces));
    }

    @Override
    public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera) {
        this.wasUsed = true;
        this.nameTagSubmits.add(poseStack, nameTagAttachment, offset, name, seeThrough, lightCoords, camera);
    }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) {
        this.wasUsed = true;
        this.textSubmits.add(new TextFeatureRenderer.Submit((Matrix4fc)new Matrix4f((Matrix4fc)poseStack.last().pose()), x, y, string, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor));
    }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) {
        this.wasUsed = true;
        this.flameSubmits.add(new FlameFeatureRenderer.Submit(poseStack.last().copy(), renderState, rotation));
    }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
        this.wasUsed = true;
        this.leashSubmits.add(new LeashFeatureRenderer.Submit(new Matrix4f((Matrix4fc)poseStack.last().pose()), leashState));
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor,  @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        this.wasUsed = true;
        ModelFeatureRenderer.Submit<? super S> submit = new ModelFeatureRenderer.Submit<S>(renderType, poseStack.last().copy(), model, state, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
        this.modelSubmits.add(renderType, submit);
    }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState) {
        this.wasUsed = true;
        this.movingBlockSubmits.add(new MovingBlockFeatureRenderer.Submit((Matrix4fc)new Matrix4f((Matrix4fc)poseStack.last().pose()), movingBlockRenderState));
    }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> modelParts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) {
        this.wasUsed = true;
        this.blockModelSubmits.add(new BlockModelFeatureRenderer.Submit(poseStack.last().copy(), renderType, modelParts, tintLayers, lightCoords, overlayCoords, outlineColor, null));
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress) {
        this.wasUsed = true;
        PoseStack.Pose pose = poseStack.last().copy();
        this.blockModelSubmits.add(new BlockModelFeatureRenderer.Submit(pose, ModelBakery.DESTROY_TYPES.get(progress), List.copyOf(parts), BlockModelRenderState.EMPTY_TINTS, 0xF000F0, OverlayTexture.NO_OVERLAY, 0, pose));
    }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
        this.wasUsed = true;
        this.shapeOutlineSubmits.add(new ShapeOutlineFeatureRenderer.Submit(poseStack.last().copy(), shape, renderType, color, width, afterTerrain));
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType) {
        this.wasUsed = true;
        this.itemSubmits.add(new ItemFeatureRenderer.Submit(poseStack.last().copy(), displayContext, lightCoords, overlayCoords, outlineColor, tintLayers, quads, foilType));
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, int outlineColor, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
        this.wasUsed = true;
        this.customGeometrySubmits.add(poseStack, renderType, outlineColor, customGeometryRenderer);
    }

    @Override
    public void submitQuadParticleGroup(QuadParticleRenderState particles) {
        this.wasUsed = true;
        this.quadParticleGroups.add(particles);
    }

    @Override
    public void submitGizmoPrimitives(DrawableGizmoPrimitives.Group group, CameraRenderState camera, boolean onTop) {
        this.wasUsed = true;
        this.gizmoSubmits.add(new GizmoFeatureRenderer.Submit(group, camera, onTop));
    }

    public List<ShadowFeatureRenderer.Submit> getShadowSubmits() {
        return this.shadowSubmits;
    }

    public List<FlameFeatureRenderer.Submit> getFlameSubmits() {
        return this.flameSubmits;
    }

    public NameTagFeatureRenderer.Storage getNameTagSubmits() {
        return this.nameTagSubmits;
    }

    public List<TextFeatureRenderer.Submit> getTextSubmits() {
        return this.textSubmits;
    }

    public List<LeashFeatureRenderer.Submit> getLeashSubmits() {
        return this.leashSubmits;
    }

    public List<MovingBlockFeatureRenderer.Submit> getMovingBlockSubmits() {
        return this.movingBlockSubmits;
    }

    public List<BlockModelFeatureRenderer.Submit> getBlockModelSubmits() {
        return this.blockModelSubmits;
    }

    public List<ItemFeatureRenderer.Submit> getItemSubmits() {
        return this.itemSubmits;
    }

    public List<QuadParticleRenderState> getQuadParticleGroups() {
        return this.quadParticleGroups;
    }

    public ModelFeatureRenderer.Storage getModelSubmits() {
        return this.modelSubmits;
    }

    public CustomFeatureRenderer.Storage getCustomGeometrySubmits() {
        return this.customGeometrySubmits;
    }

    public List<ShapeOutlineFeatureRenderer.Submit> getShapeOutlineSubmits() {
        return this.shapeOutlineSubmits;
    }

    public List<GizmoFeatureRenderer.Submit> getGizmoSubmits() {
        return this.gizmoSubmits;
    }

    public boolean wasUsed() {
        return this.wasUsed;
    }

    public void clear() {
        this.shadowSubmits.clear();
        this.flameSubmits.clear();
        this.nameTagSubmits.clear();
        this.textSubmits.clear();
        this.leashSubmits.clear();
        this.movingBlockSubmits.clear();
        this.blockModelSubmits.clear();
        this.itemSubmits.clear();
        this.quadParticleGroups.clear();
        this.modelSubmits.clear();
        this.customGeometrySubmits.clear();
        this.shapeOutlineSubmits.clear();
        this.gizmoSubmits.clear();
    }

    public void endFrame() {
        this.modelSubmits.endFrame();
        this.customGeometrySubmits.endFrame();
        this.wasUsed = false;
    }
}

