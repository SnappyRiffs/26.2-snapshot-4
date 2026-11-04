/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.commands.execution;

@FunctionalInterface
public interface UnboundEntryAction<T> {
  public void execute(T var1, ExecutionContext<T> var2, Frame var3);

  public default EntryAction<T> bind(T sender) {
    return (context, frame) -> this.execute(sender, context, frame);
  }
}
