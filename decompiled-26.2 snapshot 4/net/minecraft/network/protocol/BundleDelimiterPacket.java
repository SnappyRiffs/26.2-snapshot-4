/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;

public abstract class BundleDelimiterPacket<T extends PacketListener> implements Packet<T> {
  @Override
  public final void handle(T listener) {
    throw new AssertionError((Object) "This packet should be handled by pipeline");
  }

  @Override
  public abstract PacketType<? extends BundleDelimiterPacket<T>> type();
}
