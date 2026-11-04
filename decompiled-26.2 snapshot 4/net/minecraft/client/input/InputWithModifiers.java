/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface InputWithModifiers {
  public static final int NOT_DIGIT = -1;

  @InputConstants.Value
  public int input();

  @Modifiers
  public int modifiers();

  public default boolean isSelection() {
    return this.input() == 257 || this.input() == 32 || this.input() == 335;
  }

  public default boolean isConfirmation() {
    return this.input() == 257 || this.input() == 335;
  }

  public default boolean isEscape() {
    return this.input() == 256;
  }

  public default boolean isLeft() {
    return this.input() == 263;
  }

  public default boolean isRight() {
    return this.input() == 262;
  }

  public default boolean isUp() {
    return this.input() == 265;
  }

  public default boolean isDown() {
    return this.input() == 264;
  }

  public default boolean isCycleFocus() {
    return this.input() == 258;
  }

  public default int getDigit() {
    int value = this.input() - 48;
    if (value >= 0 && value <= 9) {
      return value;
    }
    return -1;
  }

  public default boolean hasAltDown() {
    return (this.modifiers() & 4) != 0;
  }

  public default boolean hasShiftDown() {
    return (this.modifiers() & 1) != 0;
  }

  public default boolean hasControlDown() {
    return (this.modifiers() & 2) != 0;
  }

  public default boolean hasControlDownWithQuirk() {
    return (this.modifiers() & InputQuirks.EDIT_SHORTCUT_KEY_MODIFIER) != 0;
  }

  public default boolean isSelectAll() {
    return this.input() == 65
        && this.hasControlDownWithQuirk()
        && !this.hasShiftDown()
        && !this.hasAltDown();
  }

  public default boolean isCopy() {
    return this.input() == 67
        && this.hasControlDownWithQuirk()
        && !this.hasShiftDown()
        && !this.hasAltDown();
  }

  public default boolean isPaste() {
    return this.input() == 86
        && this.hasControlDownWithQuirk()
        && !this.hasShiftDown()
        && !this.hasAltDown();
  }

  public default boolean isCut() {
    return this.input() == 88
        && this.hasControlDownWithQuirk()
        && !this.hasShiftDown()
        && !this.hasAltDown();
  }

  @Retention(value = RetentionPolicy.CLASS)
  @Target(
      value = {
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.METHOD,
        ElementType.TYPE_USE
      })
  public static @interface Modifiers {}
}
