/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface Nameable {
  public Component getName();

  public default String getPlainTextName() {
    return this.getName().getString();
  }

  public default boolean hasCustomName() {
    return this.getCustomName() != null;
  }

  public default Component getDisplayName() {
    return this.getName();
  }

  public default @Nullable Component getCustomName() {
    return null;
  }
}
