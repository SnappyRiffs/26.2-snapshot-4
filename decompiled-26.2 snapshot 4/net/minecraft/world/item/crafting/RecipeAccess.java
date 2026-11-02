/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceKey;

public interface RecipeAccess {
  public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> var1);

  public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes();
}
