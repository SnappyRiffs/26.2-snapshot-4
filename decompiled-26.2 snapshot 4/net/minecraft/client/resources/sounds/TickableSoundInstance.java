/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.sounds;

public interface TickableSoundInstance extends SoundInstance {
  public boolean isStopped();

  public void tick();
}
