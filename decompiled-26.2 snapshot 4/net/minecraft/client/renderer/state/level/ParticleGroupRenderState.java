/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.state.level;

import net.minecraft.client.renderer.SubmitNodeCollector;

public interface ParticleGroupRenderState {
  public void submit(SubmitNodeCollector var1, CameraRenderState var2);

  public default void clear() {}
}
