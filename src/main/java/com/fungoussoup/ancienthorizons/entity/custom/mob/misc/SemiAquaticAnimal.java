package com.fungoussoup.ancienthorizons.entity.custom.mob.misc;

import com.fungoussoup.ancienthorizons.entity.interfaces.ISemiAquaticAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public abstract class SemiAquaticAnimal extends Animal implements ISemiAquaticAnimal {
    protected SemiAquaticAnimal(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected static class SemiAquaticMoveToWaterGoal extends MoveToBlockGoal {
        private final SemiAquaticAnimal animal;

        public SemiAquaticMoveToWaterGoal(SemiAquaticAnimal animal, double speedModifier) {
            super(animal, speedModifier, 24);
            this.animal = animal;
        }

        @Override
        public boolean canUse() {
            return this.animal.shouldEnterWater() && super.canUse();
        }

        @Override
        protected boolean isValidTarget(net.minecraft.world.level.LevelReader level, BlockPos pos) {
            return level.getFluidState(pos).is(FluidTags.WATER);
        }
    }

    // Goal for leaving water
    protected static class SemiAquaticLeaveWaterGoal extends Goal {
        private final SemiAquaticAnimal animal;
        private final double speedModifier;
        private BlockPos targetPos;

        public SemiAquaticLeaveWaterGoal(SemiAquaticAnimal animal, double speedModifier) {
            this.animal = animal;
            this.speedModifier = speedModifier;
        }

        @Override
        public boolean canUse() {
            if (!this.animal.shouldLeaveWater()) {
                return false;
            }

            this.targetPos = this.findNearbyLand();
            return this.targetPos != null;
        }

        @Override
        public void start() {
            this.animal.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speedModifier);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.animal.getNavigation().isDone() && this.animal.shouldLeaveWater();
        }

        private BlockPos findNearbyLand() {
            BlockPos crocPos = this.animal.blockPosition();
            for (int x = -8; x <= 8; x++) {
                for (int z = -8; z <= 8; z++) {
                    for (int y = -2; y <= 2; y++) {
                        BlockPos checkPos = crocPos.offset(x, y, z);
                        if (!this.animal.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                            return checkPos;
                        }
                    }
                }
            }
            return null;
        }
    }
}
