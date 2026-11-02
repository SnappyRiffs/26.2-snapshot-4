/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;

public interface GpuBackend {
  public String getName();

  public void setWindowHints();

  public void handleWindowCreationErrors(GLFWErrorCapture.Error var1)
      throws BackendCreationException;

  public GpuDevice createDevice(long var1, ShaderSource var3, GpuDebugOptions var4)
      throws BackendCreationException;
}
