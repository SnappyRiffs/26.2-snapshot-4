/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.commands.execution;

@FunctionalInterface
public interface EntryAction<T> {
  public void execute(ExecutionContext<T> var1, Frame var2);
}
