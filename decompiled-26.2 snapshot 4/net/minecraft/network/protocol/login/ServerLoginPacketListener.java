/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol.login;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.cookie.ServerCookiePacketListener;

public interface ServerLoginPacketListener extends ServerCookiePacketListener {
  @Override
  public default ConnectionProtocol protocol() {
    return ConnectionProtocol.LOGIN;
  }

  public void handleHello(ServerboundHelloPacket var1);

  public void handleKey(ServerboundKeyPacket var1);

  public void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket var1);

  public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket var1);
}
