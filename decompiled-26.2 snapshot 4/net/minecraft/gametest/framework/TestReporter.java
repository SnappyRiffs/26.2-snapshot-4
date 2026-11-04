/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.gametest.framework;

public interface TestReporter {
  public void onTestFailed(GameTestInfo var1);

  public void onTestSuccess(GameTestInfo var1);

  public default void finish() {}
}
