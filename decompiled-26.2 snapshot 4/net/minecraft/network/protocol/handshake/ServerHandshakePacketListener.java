/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerHandshakePacketListener extends ServerPacketListener {
  @Override
  public default ConnectionProtocol protocol() {
    return ConnectionProtocol.HANDSHAKING;
  }

  public void handleIntention(ClientIntentionPacket var1);
}
