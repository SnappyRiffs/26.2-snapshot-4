/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.audio;

public interface DeviceTracker {
  public DeviceList currentDevices();

  public void tick();

  public void forceRefresh();
}
