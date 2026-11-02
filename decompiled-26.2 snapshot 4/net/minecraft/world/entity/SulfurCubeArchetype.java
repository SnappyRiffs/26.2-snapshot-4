/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public record SulfurCubeArchetype(
    HolderSet<Item> items, List<AttributeEntry> attributeModifiers, boolean buoyant) {
  public static final Codec<SulfurCubeArchetype> DIRECT_CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      (App)
                          RegistryCodecs.homogeneousList(Registries.ITEM)
                              .fieldOf("items")
                              .forGetter(SulfurCubeArchetype::items),
                      (App)
                          AttributeEntry.CODEC
                              .listOf()
                              .fieldOf("attribute_modifiers")
                              .forGetter(SulfurCubeArchetype::attributeModifiers),
                      (App)
                          Codec.BOOL
                              .optionalFieldOf("buoyant", (Object) false)
                              .forGetter(SulfurCubeArchetype::buoyant))
                  .apply((Applicative) i, SulfurCubeArchetype::new));

  public record AttributeEntry(Holder<Attribute> attribute, AttributeModifier modifier) {
    public static final Codec<AttributeEntry> CODEC =
        RecordCodecBuilder.create(
            i ->
                i.group(
                        (App)
                            Attribute.CODEC
                                .fieldOf("attribute")
                                .forGetter(AttributeEntry::attribute),
                        (App) AttributeModifier.MAP_CODEC.forGetter(AttributeEntry::modifier))
                    .apply((Applicative) i, AttributeEntry::new));

    public static AttributeEntry add(
        Holder<Attribute> attribute, double amount, ResourceKey<SulfurCubeArchetype> archetype) {
      return new AttributeEntry(
          attribute,
          new AttributeModifier(
              Identifier.withDefaultNamespace(
                  archetype.identifier().getPath()
                      + "_add_"
                      + attribute.unwrapKey().get().identifier().getPath()),
              amount,
              AttributeModifier.Operation.ADD_VALUE));
    }

    public static AttributeEntry multiply(
        Holder<Attribute> attribute, double amount, ResourceKey<SulfurCubeArchetype> archetype) {
      return new AttributeEntry(
          attribute,
          new AttributeModifier(
              Identifier.withDefaultNamespace(
                  archetype.identifier().getPath()
                      + "_mul_"
                      + attribute.unwrapKey().get().identifier().getPath()),
              amount - 1.0,
              AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
  }
}
