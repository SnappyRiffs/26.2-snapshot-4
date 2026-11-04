/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;

public interface HeadedModel {
  public ModelPart getHead();

  public default void translateToHead(PoseStack poseStack) {
    this.getHead().translateAndRotate(poseStack);
  }
}
