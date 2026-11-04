/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import net.minecraft.network.protocol.PacketFlow;

public interface ClientboundPacketListener extends PacketListener {
  @Override
  public default PacketFlow flow() {
    return PacketFlow.CLIENTBOUND;
  }
}
