/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.storage.loot;

import java.util.Set;
import net.minecraft.util.context.ContextKey;

public interface LootContextUser extends Validatable {
  public default Set<ContextKey<?>> getReferencedContextParams() {
    return Set.of();
  }

  @Override
  public default void validate(ValidationContext context) {
    context.validateContextUsage(this);
  }
}
