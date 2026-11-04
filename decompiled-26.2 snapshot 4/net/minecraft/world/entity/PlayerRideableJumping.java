/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.entity;

public interface PlayerRideableJumping extends PlayerRideable {
  public void onPlayerJump(int var1);

  public boolean canJump();

  public void handleStartJump(int var1);

  public void handleStopJump();

  public default int getJumpCooldown() {
    return 0;
  }

  public default float getPlayerJumpPendingScale(int jumpAmount) {
    return jumpAmount >= 90 ? 1.0f : 0.4f + 0.4f * (float) jumpAmount / 90.0f;
  }
}
