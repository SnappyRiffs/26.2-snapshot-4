/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class SulfurCubeBlockDispenseItemBehavior extends DefaultDispenseItemBehavior {
  public static final SulfurCubeBlockDispenseItemBehavior INSTANCE =
      new SulfurCubeBlockDispenseItemBehavior();

  @Override
  protected ItemStack execute(BlockSource source, ItemStack dispensed) {
    return SulfurCubeBlockDispenseItemBehavior.dispenseBlock(source, dispensed)
        ? dispensed
        : super.execute(source, dispensed);
  }

  public static boolean dispenseBlock(BlockSource source, ItemStack dispensed) {
    BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
    for (SulfurCube entity : source.level().getEntitiesOfClass(SulfurCube.class, new AABB(pos))) {
      if (!entity.equipItem(dispensed)) continue;
      dispensed.shrink(1);
      return true;
    }
    return false;
  }
}
