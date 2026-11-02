/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.PotentSulfurEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public class PotentSulfurBlock
extends BaseEntityBlock {
    public static final int ALLOWED_WATER_BLOCKS_ABOVE = 4;
    public static final MapCodec<PotentSulfurBlock> CODEC = PotentSulfurBlock.simpleCodec(PotentSulfurBlock::new);

    public MapCodec<PotentSulfurBlock> codec() {
        return CODEC;
    }

    public PotentSulfurBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new PotentSulfurEntity(worldPosition, blockState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
            return;
        }
        PotentSulfurBlock.spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
        PotentSulfurBlock.spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
        if (random.nextInt(10) == 0) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOXIOUS_GAS, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    private static void spawnBubbleParticlesAt(Level level, RandomSource random, double x, double y, double z) {
        level.addAlwaysVisibleParticle(ParticleTypes.SULFUR_BUBBLES, x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0, 0.0, 0.0);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return PotentSulfurBlock.createTickerHelper(type, BlockEntityTypes.POTENT_SULFUR, level.isClientSide() ? PotentSulfurEntity::clientTick : PotentSulfurEntity::serverTick);
    }
}

