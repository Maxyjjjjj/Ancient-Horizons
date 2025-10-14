package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.BrownBearEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BearBreakBeeNestGoal extends Goal {
        private final BrownBearEntity bear;
        private BlockPos targetPos;
        private int breakTicks;

        public BearBreakBeeNestGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bear.getCurrentGene() == BrownBearEntity.Gene.LAZY) {
                return false; // Too lazy
            }

            // Look for nearby beehive/nest with honey
            BlockPos bearPos = bear.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(bearPos.offset(-6, -2, -6), bearPos.offset(6, 2, 6))) {
                BlockState state = bear.level().getBlockState(pos);
                if (isHoneyFilledBeeNest(state)) {
                    this.targetPos = pos.immutable();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return targetPos != null && bear.distanceToSqr(Vec3.atCenterOf(targetPos)) < 9.0D;
        }

        @Override
        public void start() {
            bear.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0D);
            breakTicks = 0;
        }

        @Override
        public void tick() {
            if (targetPos != null && bear.distanceToSqr(Vec3.atCenterOf(targetPos)) < 2.0D) {
                bear.getLookControl().setLookAt(Vec3.atCenterOf(targetPos));

                breakTicks++;
                if (breakTicks > 40) { // 2 seconds
                    BlockState state = bear.level().getBlockState(targetPos);
                    if (isHoneyFilledBeeNest(state)) {
                        bear.level().destroyBlock(targetPos, true); // Drop contents
                        double d0 = 15;
                        for (Bee bee : bear.level().getEntitiesOfClass(Bee.class, new AABB((double) targetPos.getX() - d0, (double) targetPos.getY() - d0, (double) targetPos.getZ() - d0, (double) targetPos.getX() + d0, (double) targetPos.getY() + d0, (double) targetPos.getZ() + d0))) {
                            bee.setRemainingPersistentAngerTime(100);
                            bee.setTarget(bear);
                            bee.setStayOutOfHiveCountdown(400);
                        }
                        bear.heal(6.0F);
                        bear.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));

                        bear.playSound(SoundEvents.HONEY_BLOCK_PLACE, 1.0F, 1.0F);
                    }
                    targetPos = null;
                }
            }
        }

        @Override
        public void stop() {
            targetPos = null;
            breakTicks = 0;
        }

        private boolean isHoneyFilledBeeNest(BlockState state) {
            Block block = state.getBlock();
            return (block == Blocks.BEE_NEST || block == Blocks.BEEHIVE)
                    && state.hasProperty(BeehiveBlock.HONEY_LEVEL)
                    && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5;
        }
    }
