/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public interface ServerConfigurationPacketListener extends ServerCommonPacketListener {
  @Override
  public default ConnectionProtocol protocol() {
    return ConnectionProtocol.CONFIGURATION;
  }

  public void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1);

  public void handleSelectKnownPacks(ServerboundSelectKnownPacks var1);

  public void handleAcceptCodeOfConduct(ServerboundAcceptCodeOfConductPacket var1);
}
