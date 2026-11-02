/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.renderer.feature.submit;

import net.minecraft.util.Mth;
import org.joml.Matrix4fc;

public interface TranslucentSubmit extends SubmitNode {
  public float distanceToCameraSq();

  public static float computeDistanceToCameraSq(Matrix4fc pose) {
    return Mth.lengthSquared(pose.m30(), pose.m31(), pose.m32());
  }
}
