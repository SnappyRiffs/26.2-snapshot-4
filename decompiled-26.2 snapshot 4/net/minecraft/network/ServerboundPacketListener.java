/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import net.minecraft.network.protocol.PacketFlow;

public interface ServerboundPacketListener extends PacketListener {
  @Override
  public default PacketFlow flow() {
    return PacketFlow.SERVERBOUND;
  }
}
