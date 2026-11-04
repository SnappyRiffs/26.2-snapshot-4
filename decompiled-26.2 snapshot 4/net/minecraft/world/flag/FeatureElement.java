/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.flag;

import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public interface FeatureElement {
  public static final Set<ResourceKey<? extends Registry<? extends FeatureElement>>>
      FILTERED_REGISTRIES =
          Set.of(
              Registries.ITEM,
              Registries.BLOCK,
              Registries.ENTITY_TYPE,
              Registries.GAME_RULE,
              Registries.MENU,
              Registries.POTION,
              Registries.MOB_EFFECT);

  public FeatureFlagSet requiredFeatures();

  public default boolean isEnabled(FeatureFlagSet enabledFeatures) {
    return this.requiredFeatures().isSubsetOf(enabledFeatures);
  }
}
