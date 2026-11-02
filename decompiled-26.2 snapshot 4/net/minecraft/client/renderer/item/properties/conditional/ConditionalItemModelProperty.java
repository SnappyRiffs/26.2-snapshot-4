/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;

public interface ConditionalItemModelProperty extends ItemModelPropertyTest {
  public MapCodec<? extends ConditionalItemModelProperty> type();
}
