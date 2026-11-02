/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.network.protocol.ping.ServerPingPacketListener;

public interface ServerStatusPacketListener extends ServerPacketListener, ServerPingPacketListener {
  @Override
  public default ConnectionProtocol protocol() {
    return ConnectionProtocol.STATUS;
  }

  public void handleStatusRequest(ServerboundStatusRequestPacket var1);
}
