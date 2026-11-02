/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class BlastingRecipe extends AbstractCookingRecipe {
  public static final MapCodec<BlastingRecipe> MAP_CODEC =
      BlastingRecipe.cookingMapCodec(BlastingRecipe::new, 100);
  public static final StreamCodec<RegistryFriendlyByteBuf, BlastingRecipe> STREAM_CODEC =
      BlastingRecipe.cookingStreamCodec(BlastingRecipe::new);
  public static final RecipeSerializer<BlastingRecipe> SERIALIZER =
      new RecipeSerializer<BlastingRecipe>(MAP_CODEC, STREAM_CODEC);

  public BlastingRecipe(
      Recipe.CommonInfo commonInfo,
      AbstractCookingRecipe.CookingBookInfo bookInfo,
      Ingredient ingredient,
      ItemStackTemplate result,
      float experience,
      int cookingTime) {
    super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
  }

  @Override
  protected Item furnaceIcon() {
    return Items.BLAST_FURNACE;
  }

  @Override
  public RecipeSerializer<BlastingRecipe> getSerializer() {
    return SERIALIZER;
  }

  @Override
  public RecipeType<BlastingRecipe> getType() {
    return RecipeType.BLASTING;
  }

  @Override
  public RecipeBookCategory recipeBookCategory() {
    return switch (this.category()) {
      default -> throw new MatchException(null, null);
      case CookingBookCategory.BLOCKS -> RecipeBookCategories.BLAST_FURNACE_BLOCKS;
      case CookingBookCategory.FOOD, CookingBookCategory.MISC ->
          RecipeBookCategories.BLAST_FURNACE_MISC;
    };
  }
}
