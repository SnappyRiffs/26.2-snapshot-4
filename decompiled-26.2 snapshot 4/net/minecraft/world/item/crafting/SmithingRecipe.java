/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.world.level.Level;

public interface SmithingRecipe extends Recipe<SmithingRecipeInput> {
  @Override
  public default RecipeType<SmithingRecipe> getType() {
    return RecipeType.SMITHING;
  }

  @Override
  public RecipeSerializer<? extends SmithingRecipe> getSerializer();

  @Override
  public default boolean matches(SmithingRecipeInput input, Level level) {
    return Ingredient.testOptionalIngredient(this.templateIngredient(), input.template())
        && this.baseIngredient().test(input.base())
        && Ingredient.testOptionalIngredient(this.additionIngredient(), input.addition());
  }

  public Optional<Ingredient> templateIngredient();

  public Ingredient baseIngredient();

  public Optional<Ingredient> additionIngredient();

  @Override
  public default RecipeBookCategory recipeBookCategory() {
    return RecipeBookCategories.SMITHING;
  }
}
