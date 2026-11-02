/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;

public interface TimerCallback<T> {
  public void handle(T var1, TimerQueue<T> var2, long var3);

  public MapCodec<? extends TimerCallback<T>> codec();
}
