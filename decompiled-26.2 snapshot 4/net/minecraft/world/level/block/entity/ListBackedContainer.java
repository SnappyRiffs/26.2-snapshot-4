/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.block.entity;

import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public interface ListBackedContainer extends Container {
  public NonNullList<ItemStack> getItems();

  public default int count() {
    return (int) this.getItems().stream().filter(Predicate.not(ItemStack::isEmpty)).count();
  }

  @Override
  public default int getContainerSize() {
    return this.getItems().size();
  }

  @Override
  public default void clearContent() {
    this.getItems().clear();
  }

  @Override
  public default boolean isEmpty() {
    return this.getItems().stream().allMatch(ItemStack::isEmpty);
  }

  @Override
  public default ItemStack getItem(int slot) {
    return this.getItems().get(slot);
  }

  @Override
  public default ItemStack removeItem(int slot, int count) {
    ItemStack result = ContainerHelper.removeItem(this.getItems(), slot, count);
    if (!result.isEmpty()) {
      this.setChanged();
    }
    return result;
  }

  @Override
  public default ItemStack removeItemNoUpdate(int slot) {
    return ContainerHelper.removeItem(this.getItems(), slot, this.getMaxStackSize());
  }

  @Override
  public default boolean canPlaceItem(int slot, ItemStack itemStack) {
    return this.acceptsItemType(itemStack)
        && (this.getItem(slot).isEmpty()
            || this.getItem(slot).getCount() < this.getMaxStackSize(itemStack));
  }

  public default boolean acceptsItemType(ItemStack itemStack) {
    return true;
  }

  @Override
  public default void setItem(int slot, ItemStack itemStack) {
    this.setItemNoUpdate(slot, itemStack);
    this.setChanged();
  }

  public default void setItemNoUpdate(int slot, ItemStack itemStack) {
    this.getItems().set(slot, itemStack);
    itemStack.limitSize(this.getMaxStackSize(itemStack));
  }
}
