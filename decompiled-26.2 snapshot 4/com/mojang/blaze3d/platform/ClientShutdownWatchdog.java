/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.platform;

import java.time.Duration;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.dedicated.ServerWatchdog;
import org.jspecify.annotations.Nullable;

public class ClientShutdownWatchdog {
  private static final Duration CRASH_REPORT_PRELOAD_LOAD = Duration.ofSeconds(15L);

  public static void startShutdownWatchdog(
      String callsite, @Nullable Minecraft minecraft, GameConfig gameConfig, long mainThreadId) {
    Thread thread =
        new Thread(
            () -> {
              try {
                Thread.sleep(CRASH_REPORT_PRELOAD_LOAD);
              } catch (InterruptedException e) {
                return;
              }
              CrashReport report =
                  ServerWatchdog.createWatchdogCrashReport(
                      "Client shutdown from " + callsite, mainThreadId);
              if (minecraft != null) {
                minecraft.fillReport(report);
              } else {
                Minecraft.fillReport(null, null, gameConfig.game.launchVersion, null, report);
              }
              Minecraft.saveReport(gameConfig.location.gameDirectory, report);
            },
            "Client shutdown watchdog");
    thread.setDaemon(true);
    thread.start();
  }
}
