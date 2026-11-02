/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.gamerules;

public interface GameRuleTypeVisitor {
  public default <T> void visit(GameRule<T> gameRule) {}

  public default void visitBoolean(GameRule<Boolean> gameRule) {}

  public default void visitInteger(GameRule<Integer> gameRule) {}
}
