/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
  public ItemStack getTheItem();

  public default ItemStack splitTheItem(int count) {
    return this.getTheItem().split(count);
  }

  public void setTheItem(ItemStack var1);

  public default ItemStack removeTheItem() {
    return this.splitTheItem(this.getMaxStackSize());
  }

  @Override
  public default int getContainerSize() {
    return 1;
  }

  @Override
  public default boolean isEmpty() {
    return this.getTheItem().isEmpty();
  }

  @Override
  public default void clearContent() {
    this.removeTheItem();
  }

  @Override
  public default ItemStack removeItemNoUpdate(int slot) {
    return this.removeItem(slot, this.getMaxStackSize());
  }

  @Override
  public default ItemStack getItem(int slot) {
    return slot == 0 ? this.getTheItem() : ItemStack.EMPTY;
  }

  @Override
  public default ItemStack removeItem(int slot, int count) {
    if (slot != 0) {
      return ItemStack.EMPTY;
    }
    return this.splitTheItem(count);
  }

  @Override
  public default void setItem(int slot, ItemStack itemStack) {
    if (slot == 0) {
      this.setTheItem(itemStack);
    }
  }

  public static interface BlockContainerSingleItem extends ContainerSingleItem {
    public BlockEntity getContainerBlockEntity();

    @Override
    public default boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this.getContainerBlockEntity(), player);
    }
  }
}
