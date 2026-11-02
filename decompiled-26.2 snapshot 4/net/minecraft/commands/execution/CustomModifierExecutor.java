/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.brigadier.RedirectModifier
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.commands.execution;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;

public interface CustomModifierExecutor<T> {
  public void apply(
      T var1, List<T> var2, ContextChain<T> var3, ChainModifiers var4, ExecutionControl<T> var5);

  public static interface ModifierAdapter<T>
      extends CustomModifierExecutor<T>, RedirectModifier<T> {
    public default Collection<T> apply(CommandContext<T> context) throws CommandSyntaxException {
      throw new UnsupportedOperationException("This function should not run");
    }
  }
}
