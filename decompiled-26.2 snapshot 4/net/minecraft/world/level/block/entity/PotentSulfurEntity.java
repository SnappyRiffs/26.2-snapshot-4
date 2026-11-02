/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

public class PotentSulfurEntity
extends BlockEntity {
    private static final int EFFECT_APPLICATION_FREQUENCY_TICKS = 10;
    private static final float EFFECT_DURATION_IN_SECONDS = 4.0f;
    private static final int EFFECT_DURATION_IN_TICKS = 80;
    public static final float EFFECT_RANGE = 3.0f;
    private static final Predicate<Entity> EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE);
    public static final int PARTICLE_FREQUENCY_TICKS = 20;

    public PotentSulfurEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntityTypes.POTENT_SULFUR, worldPosition, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotentSulfurEntity potentSulfur) {
        if (level.getGameTime() % 10L != 0L || !PotentSulfurEntity.isUnderwater(level, pos)) {
            return;
        }
        BlockPos sourceBlock = PotentSulfurEntity.findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock == null) {
            return;
        }
        for (LivingEntity entity : PotentSulfurEntity.getNearbyLivingEntities(level, sourceBlock)) {
            if (!PotentSulfurEntity.canBeReachedByNoxiousGas(level, sourceBlock, entity.getEyePosition())) continue;
            PotentSulfurEntity.applyNauseaEffect(entity);
        }
    }

    private static void applyNauseaEffect(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 80, 0, true, true));
    }

    private static List<LivingEntity> getNearbyLivingEntities(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos).inflate(2.5, 0.0, 2.5);
        return level.getEntitiesOfClass(LivingEntity.class, aabb, EFFECT_PREDICATE);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PotentSulfurEntity entity) {
        if (level.getGameTime() % 20L != 0L || !PotentSulfurEntity.isUnderwater(level, pos)) {
            return;
        }
        BlockPos sourceBlock = PotentSulfurEntity.findNoxiousGasSourceBlock(level, pos);
        if (sourceBlock != null) {
            PotentSulfurEntity.spawnNoxiousGasCloudParticle(level, sourceBlock.getCenter());
        }
    }

    private static void spawnNoxiousGasCloudParticle(Level level, Vec3 pos) {
        level.addParticle(ParticleTypes.NOXIOUS_GAS_CLOUD, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
    }

    private static @Nullable BlockPos findNoxiousGasSourceBlock(Level level, BlockPos origin) {
        int maxY = origin.getY() + 4 + 1;
        BlockPos.MutableBlockPos pos = origin.above(2).mutable();
        while (pos.getY() <= maxY) {
            if (!level.getFluidState(pos).isSourceOfType(Fluids.WATER)) {
                if (!level.getBlockState(pos).isAir()) break;
                return pos.immutable();
            }
            pos.move(Direction.UP);
        }
        return null;
    }

    public static boolean canBeReachedByNoxiousGas(Level level, BlockPos sourceBlock, Vec3 pos) {
        if (!PotentSulfurEntity.isAir(level, pos)) {
            return false;
        }
        if (pos.distanceToSqr(sourceBlock.getCenter()) > 9.0) {
            return false;
        }
        Vec3 belowSource = sourceBlock.below().getCenter();
        Vec3 belowPos = pos.with(Direction.Axis.Y, pos.y - 1.0);
        return PotentSulfurEntity.isWater(level, belowPos) && PotentSulfurEntity.haveLineOfSight(level, belowSource, belowPos);
    }

    private static boolean haveLineOfSight(Level level, Vec3 a, Vec3 b) {
        BlockHitResult hitResult = level.clip(new ClipContext(a, b, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return ((HitResult)hitResult).getType() != HitResult.Type.BLOCK;
    }

    private static boolean isUnderwater(Level level, BlockPos pos) {
        return level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER);
    }

    private static boolean isWater(Level level, Vec3 pos) {
        return level.getFluidState(BlockPos.containing(pos)).isSourceOfType(Fluids.WATER);
    }

    private static boolean isAir(Level level, Vec3 pos) {
        return level.getBlockState(BlockPos.containing(pos)).isAir();
    }
}

