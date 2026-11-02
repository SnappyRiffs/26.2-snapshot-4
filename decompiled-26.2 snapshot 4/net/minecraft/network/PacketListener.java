/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;

public interface PacketListener {
  public PacketFlow flow();

  public ConnectionProtocol protocol();

  public void onDisconnect(DisconnectionDetails var1);

  public default void onPacketError(Packet packet, Exception cause) throws ReportedException {
    throw PacketUtils.makeReportedException(cause, packet, this);
  }

  public default DisconnectionDetails createDisconnectionInfo(Component reason, Throwable cause) {
    return new DisconnectionDetails(reason);
  }

  public boolean isAcceptingMessages();

  public default boolean shouldHandleMessage(Packet<?> packet) {
    return this.isAcceptingMessages();
  }

  public default void fillCrashReport(CrashReport crashReport) {
    CrashReportCategory connection = crashReport.addCategory("Connection");
    connection.setDetail("Protocol", () -> this.protocol().id());
    connection.setDetail("Flow", () -> this.flow().toString());
    this.fillListenerSpecificCrashDetails(crashReport, connection);
  }

  public default void fillListenerSpecificCrashDetails(
      CrashReport report, CrashReportCategory connectionDetails) {}
}
