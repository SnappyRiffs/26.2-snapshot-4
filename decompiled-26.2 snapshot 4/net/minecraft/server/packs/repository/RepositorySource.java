/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.packs.repository;

import java.util.function.Consumer;

@FunctionalInterface
public interface RepositorySource {
  public void loadPacks(Consumer<Pack> var1);
}
