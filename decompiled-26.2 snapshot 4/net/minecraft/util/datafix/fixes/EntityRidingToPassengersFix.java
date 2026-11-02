/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;

public class EntityRidingToPassengersFix
extends DataFix {
    public EntityRidingToPassengersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        Schema inputSchema = this.getInputSchema();
        Schema outputSchema = this.getOutputSchema();
        Type oldEntityTreeType = inputSchema.getTypeRaw(References.ENTITY_TREE);
        Type newEntityTreeType = outputSchema.getTypeRaw(References.ENTITY_TREE);
        Type entityType = inputSchema.getTypeRaw(References.ENTITY);
        return this.cap(inputSchema, outputSchema, oldEntityTreeType, newEntityTreeType, entityType);
    }

    private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(Schema inputSchema, Schema outputType, Type<OldEntityTree> oldEntityTreeType, Type<NewEntityTree> newEntityTreeType, Type<Entity> entityType) {
        Type oldType = DSL.named((String)References.ENTITY_TREE.typeName(), (Type)DSL.and((Type)DSL.optional((Type)DSL.field((String)"Riding", oldEntityTreeType)), entityType));
        Type newType = DSL.named((String)References.ENTITY_TREE.typeName(), (Type)DSL.and((Type)DSL.optional((Type)DSL.field((String)"Passengers", (Type)DSL.list(newEntityTreeType))), entityType));
        Type oldEntityType = inputSchema.getType(References.ENTITY_TREE);
        Type newEntityType = outputType.getType(References.ENTITY_TREE);
        if (!Objects.equals(oldEntityType, oldType)) {
            throw new IllegalStateException("Old entity type is not what was expected.");
        }
        if (!newEntityType.equals((Object)newType, true, true)) {
            throw new IllegalStateException("New entity type is not what was expected.");
        }
        OpticFinder entityTreeFinder = DSL.typeFinder((Type)oldType);
        OpticFinder newEntityTreeValueFinder = DSL.typeFinder((Type)newType);
        OpticFinder newEntityTreeFinder = DSL.typeFinder(newEntityTreeType);
        Type oldPlayerType = inputSchema.getType(References.PLAYER);
        Type newPlayerType = outputType.getType(References.PLAYER);
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere("EntityRidingToPassengerFix", oldType, newType, ops -> input -> {
            Optional<Object> passenger = Optional.empty();
            Pair updating = input;
            while (true) {
                Either passengersValue = (Either)DataFixUtils.orElse(passenger.map(p -> {
                    Typed newEntity = (Typed)newEntityTreeType.pointTyped(ops).orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
                    Object newEntityTree = newEntity.set(newEntityTreeValueFinder, p).getOptional(newEntityTreeFinder).orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                    return Either.left((Object)ImmutableList.of(newEntityTree));
                }), (Object)Either.right((Object)DSL.unit()));
                passenger = Optional.of(Pair.of((Object)References.ENTITY_TREE.typeName(), (Object)Pair.of((Object)passengersValue, (Object)((Pair)updating.getSecond()).getSecond())));
                Optional riding = ((Either)((Pair)updating.getSecond()).getFirst()).left();
                if (riding.isEmpty()) break;
                updating = (Pair)new Typed(oldEntityTreeType, ops, riding.get()).getOptional(entityTreeFinder).orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
            }
            return (Pair)passenger.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
        }), (TypeRewriteRule)this.writeAndRead("player RootVehicle injecter", oldPlayerType, newPlayerType));
    }
}

