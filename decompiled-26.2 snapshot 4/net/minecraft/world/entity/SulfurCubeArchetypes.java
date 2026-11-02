/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.entity;

import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

public class SulfurCubeArchetypes {
    public static final ResourceKey<SulfurCubeArchetype> REGULAR = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("regular"));
    public static final ResourceKey<SulfurCubeArchetype> BOUNCY = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("bouncy"));
    public static final ResourceKey<SulfurCubeArchetype> SLOW_FLAT = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("slow_flat"));
    public static final ResourceKey<SulfurCubeArchetype> FAST_FLAT = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("fast_flat"));
    public static final ResourceKey<SulfurCubeArchetype> LIGHT = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("light"));
    public static final ResourceKey<SulfurCubeArchetype> FAST_SLIDING = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("fast_sliding"));
    public static final ResourceKey<SulfurCubeArchetype> SLOW_SLIDING = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("slow_sliding"));
    public static final ResourceKey<SulfurCubeArchetype> HIGH_RESISTANCE = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("high_resistance"));
    public static final ResourceKey<SulfurCubeArchetype> STICKY = SulfurCubeArchetypes.createKey(Identifier.withDefaultNamespace("sticky"));

    public static void bootstrap(BootstrapContext<SulfurCubeArchetype> context) {
        SulfurCubeArchetypes.register(context, REGULAR, ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR, SulfurCubeArchetypes.archetype(1.0f, 0.5f, 0.3f, 0.1f), true);
        SulfurCubeArchetypes.register(context, BOUNCY, ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY, SulfurCubeArchetypes.archetype(2.0f, 0.9f, 0.3f, 0.01f), true);
        SulfurCubeArchetypes.register(context, SLOW_FLAT, ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT, SulfurCubeArchetypes.archetype(-0.7f, 0.2f, 0.3f, 0.1f), false);
        SulfurCubeArchetypes.register(context, FAST_FLAT, ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT, SulfurCubeArchetypes.archetype(2.0f, 0.2f, 0.1f, 0.01f), false);
        SulfurCubeArchetypes.register(context, LIGHT, ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT, SulfurCubeArchetypes.archetype(1.0f, 1.0f, 0.3f, 1.8f), true);
        SulfurCubeArchetypes.register(context, FAST_SLIDING, ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING, SulfurCubeArchetypes.archetype(-0.5f, 0.1f, 0.05f, 0.01f), false);
        SulfurCubeArchetypes.register(context, SLOW_SLIDING, ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING, SulfurCubeArchetypes.archetype(-0.8f, 0.1f, 0.05f, 0.01f), false);
        SulfurCubeArchetypes.register(context, STICKY, ItemTags.SULFUR_CUBE_ARCHETYPE_STICKY, SulfurCubeArchetypes.archetype(2.0f, 0.0f, 2.0f, 0.01f), false);
        SulfurCubeArchetypes.register(context, HIGH_RESISTANCE, ItemTags.SULFUR_CUBE_ARCHETYPE_HIGH_RESISTANCE, SulfurCubeArchetypes.archetype(-0.7f, 0.2f, 1.0f, 0.01f), false);
    }

    private static ResourceKey<SulfurCubeArchetype> createKey(Identifier id) {
        return ResourceKey.create(Registries.SULFUR_CUBE_ARCHETYPE, id);
    }

    private static Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry> add(Holder<Attribute> attribute, double amount) {
        return key -> SulfurCubeArchetype.AttributeEntry.add(attribute, amount, key);
    }

    private static Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry> multiply(Holder<Attribute> attribute, double amount) {
        return key -> SulfurCubeArchetype.AttributeEntry.multiply(attribute, amount, key);
    }

    private static List<Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry>> archetype(float speed, float bounce, float friction, float drag) {
        return List.of(SulfurCubeArchetypes.add(Attributes.KNOCKBACK_RESISTANCE, -speed), SulfurCubeArchetypes.add(Attributes.BOUNCINESS, bounce), SulfurCubeArchetypes.multiply(Attributes.FRICTION_MODIFIER, friction), SulfurCubeArchetypes.multiply(Attributes.AIR_DRAG_MODIFIER, drag));
    }

    private static void register(BootstrapContext<SulfurCubeArchetype> context, ResourceKey<SulfurCubeArchetype> name, TagKey<Item> blocks, List<Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry>> modifiers, boolean floats) {
        context.register(name, new SulfurCubeArchetype(context.lookup(Registries.ITEM).getOrThrow(blocks), modifiers.stream().map(f -> (SulfurCubeArchetype.AttributeEntry)f.apply(name)).toList(), floats));
    }
}

