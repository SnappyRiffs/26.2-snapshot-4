/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.tutorial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface TutorialStepInstance {
  public default void clear() {}

  public default void tick() {}

  public default void onInput(ClientInput input) {}

  public default void onMouse(double xd, double yd) {}

  public default void onLookAt(ClientLevel level, HitResult hit) {}

  public default void onDestroyBlock(
      ClientLevel level, BlockPos pos, BlockState state, float percent) {}

  public default void onOpenInventory() {}

  public default void onGetItem(ItemStack itemStack) {}
}
