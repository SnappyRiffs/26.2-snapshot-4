/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SulfurCubeRenderer
    extends AbstractCubeMobRenderer<SulfurCube, SulfurCubeRenderState, SulfurCubeModel> {
  private static final Identifier SULFUR_CUBE_LOCATION =
      Identifier.withDefaultNamespace("textures/entity/sulfur_cube/sulfur_cube_outer.png");
  public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
  private final BlockModelResolver blockModelResolver;

  public SulfurCubeRenderer(EntityRendererProvider.Context context) {
    super(context, new SulfurCubeModel(context.bakeLayer(ModelLayers.SULFUR_CUBE)));
    this.blockModelResolver = context.getBlockModelResolver();
    this.addLayer(new SulfurCubeInnerLayer(this, context.getModelSet()));
  }

  @Override
  protected void scale(SulfurCubeRenderState state, PoseStack poseStack) {
    this.downscaleSlightly(poseStack);
    super.scale(state, poseStack);
    poseStack.scale(0.5f, 0.5f, 0.5f);
    poseStack.translate(-0.0f, 0.98f, -0.0f);
  }

  @Override
  public Identifier getTextureLocation(SulfurCubeRenderState state) {
    return SULFUR_CUBE_LOCATION;
  }

  @Override
  public SulfurCubeRenderState createRenderState() {
    return new SulfurCubeRenderState();
  }

  @Override
  public void extractRenderState(
      SulfurCube entity, SulfurCubeRenderState state, float partialTicks) {
    super.extractRenderState(entity, state, partialTicks);
    ItemStack containedBlock = entity.getBodyArmorItem();
    if (!containedBlock.isEmpty()) {
      BlockItemStateProperties blockItemState =
          containedBlock.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
      BlockState blockState =
          blockItemState.apply(Block.byItem(containedBlock.getItem()).defaultBlockState());
      this.blockModelResolver.update(state.containedBlock, blockState, BLOCK_DISPLAY_CONTEXT);
    }
  }
}
