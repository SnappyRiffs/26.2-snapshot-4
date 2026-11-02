/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.components;

public interface SelectableEntry {
  public default boolean mouseOverIcon(int relX, int relY, int size) {
    return relX >= 0 && relX < size && relY >= 0 && relY < size;
  }

  public default boolean mouseOverLeftHalf(int relX, int relY, int size) {
    return relX >= 0 && relX < size / 2 && relY >= 0 && relY < size;
  }

  public default boolean mouseOverRightHalf(int relX, int relY, int size) {
    return relX >= size / 2 && relX < size && relY >= 0 && relY < size;
  }

  public default boolean mouseOverTopRightQuarter(int relX, int relY, int size) {
    return relX >= size / 2 && relX < size && relY >= 0 && relY < size / 2;
  }

  public default boolean mouseOverBottomRightQuarter(int relX, int relY, int size) {
    return relX >= size / 2 && relX < size && relY >= size / 2 && relY < size;
  }

  public default boolean mouseOverTopLeftQuarter(int relX, int relY, int size) {
    return relX >= 0 && relX < size / 2 && relY >= 0 && relY < size / 2;
  }

  public default boolean mouseOverBottomLeftQuarter(int relX, int relY, int size) {
    return relX >= 0 && relX < size / 2 && relY >= size / 2 && relY < size;
  }
}
