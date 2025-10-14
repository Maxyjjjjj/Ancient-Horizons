package com.fungoussoup.ancienthorizons.entity.custom.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class AirBreathingWaterAnimal extends TrulyWaterAnimal {

    protected AirBreathingWaterAnimal(EntityType<? extends AirBreathingWaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean needsAirFromSurface() {
        return true;
    }

    @Override
    protected void handleAirSupply(int previousAirSupply) {
        if (this.isAlive()) {
            // Regain air when at surface or above water
            if (!this.isInWaterOrBubble() || this.isAtSurface()) {
                this.setAirSupply(this.getMaxAirSupply());
            } else {
                // Lose air while underwater
                this.setAirSupply(previousAirSupply - 1);
                if (this.getAirSupply() == -20) {
                    this.setAirSupply(0);
                    this.hurt(this.damageSources().drown(), 2.0F);
                }
            }
        }
    }

    /**
     * Checks if the entity is at the water surface where it can breathe.
     */
    protected boolean isAtSurface() {
        BlockPos blockPos = this.blockPosition();
        FluidState fluidState = this.level().getFluidState(blockPos);

        // Check if we're in water and the block above is air or water surface
        return fluidState.is(FluidTags.WATER)
                && fluidState.getAmount() >= 0.8F
                && this.level().getBlockState(blockPos.above()).isAir();
    }

    /**
     * Returns the maximum air supply in ticks.
     * Override to customize breath duration.
     */
    @Override
    public int getMaxAirSupply() {
        return 4800; // 4 minutes underwater
    }

    /**
     * Returns the air supply threshold at which the entity will seek the surface.
     * Override to make animals surface sooner or later.
     */
    protected int getAirSupplyThreshold() {
        return 1200; // Start seeking air with 1 minute left
    }

    /**
     * Whether the entity needs to surface for air urgently.
     */
    protected boolean needsToSurface() {
        return this.getAirSupply() < this.getAirSupplyThreshold() && this.isInWater();
    }

    /**
     * Goal for surfacing to breathe air.
     * Add this goal to your entity's goal selector.
     */
    public static class SurfaceForAirGoal extends Goal {
        private final AirBreathingWaterAnimal mob;
        private boolean hasReachedSurface;

        public SurfaceForAirGoal(AirBreathingWaterAnimal mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.needsToSurface() && !this.mob.isAtSurface();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.hasReachedSurface;
        }

        @Override
        public void start() {
            this.hasReachedSurface = false;
        }

        @Override
        public void stop() {
            this.hasReachedSurface = false;
        }

        @Override
        public void tick() {
            // Swim upward toward surface
            Vec3 currentMotion = this.mob.getDeltaMovement();
            double upwardForce = 0.04;

            // Apply stronger upward force when air is critical
            if (this.mob.getAirSupply() < 200) {
                upwardForce = 0.08;
            }

            this.mob.setDeltaMovement(
                    currentMotion.x * 0.95,
                    Math.min(currentMotion.y + upwardForce, 0.5),
                    currentMotion.z * 0.95
            );

            // Look upward
            this.mob.setXRot(Mth.clamp(this.mob.getXRot() - 5.0F, -90.0F, 0.0F));

            // Check if we've reached the surface
            if (this.mob.isAtSurface()) {
                this.hasReachedSurface = true;
            }
        }
    }

    /**
     * Standard spawn rule for air-breathing aquatic animals.
     * Similar to surface water animals but allows for deeper spawns.
     */
    public static boolean checkAirBreathingWaterAnimalSpawnRules(
            EntityType<? extends AirBreathingWaterAnimal> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random) {
        int seaLevel = level.getSeaLevel();
        int minDepth = seaLevel - 32; // Allow deeper spawns than surface fish
        return pos.getY() >= minDepth
                && pos.getY() <= seaLevel
                && level.getFluidState(pos).is(FluidTags.WATER)
                && level.getBlockState(pos.above()).is(Blocks.WATER);
    }
}
