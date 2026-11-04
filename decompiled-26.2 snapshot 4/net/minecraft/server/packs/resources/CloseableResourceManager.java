/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.packs.resources;

public interface CloseableResourceManager extends ResourceManager, AutoCloseable {
  @Override
  public void close();
}
