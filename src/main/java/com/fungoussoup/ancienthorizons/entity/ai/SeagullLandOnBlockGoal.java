package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.SeagullEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SeagullLandOnBlockGoal extends Goal {
    private final SeagullEntity seagull;
    private BlockPos targetPos;
    private int landingTimer = 0;
    private static final int LANDING_TIME = 20; // 1 second

    public SeagullLandOnBlockGoal(SeagullEntity seagull) {
        this.seagull = seagull;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.seagull.isSitting() || this.seagull.getTarget() != null) {
            return false;
        }

        if (this.seagull.getRandom().nextInt(200) != 0) {
            return false;
        }

        return findLandingSpot() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPos != null && !this.seagull.isSitting() && this.landingTimer < LANDING_TIME * 3;
    }

    @Override
    public void start() {
        this.targetPos = findLandingSpot();
        this.landingTimer = 0;
        this.seagull.setFlying(true);
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.landingTimer = 0;
    }

    @Override
    public void tick() {
        if (this.targetPos != null) {
            Vec3 targetVec = Vec3.atCenterOf(this.targetPos);
            this.seagull.getMoveControl().setWantedPosition(targetVec.x, targetVec.y, targetVec.z, 0.8);

            double distance = this.seagull.distanceToSqr(targetVec);

            if (distance < 2.0) {
                this.landingTimer++;
                if (this.landingTimer >= LANDING_TIME) {
                    this.seagull.setFlying(false);
                    this.seagull.setSitting(true);
                    this.seagull.setPos(targetVec.x, targetVec.y, targetVec.z);
                    this.seagull.setDeltaMovement(Vec3.ZERO);
                }
            }
        }
    }

    private BlockPos findLandingSpot() {
        BlockPos currentPos = this.seagull.blockPosition();

        for (int i = 0; i < 20; i++) {
            BlockPos testPos = currentPos.offset(
                    this.seagull.getRandom().nextInt(21) - 10,
                    -this.seagull.getRandom().nextInt(8) - 1,
                    this.seagull.getRandom().nextInt(21) - 10
            );

            if (isValidLandingSpot(testPos)) {
                return testPos.above();
            }
        }

        return null;
    }

    private boolean isValidLandingSpot(BlockPos pos) {
        BlockState groundState = this.seagull.level().getBlockState(pos);
        BlockState aboveState = this.seagull.level().getBlockState(pos.above());

        // Check if it's a solid block that seagulls can land on
        return aboveState.isAir() &&
                (groundState.is(BlockTags.LOGS) ||
                        groundState.is(BlockTags.LEAVES) ||
                        groundState.is(BlockTags.FENCES) ||
                        groundState.is(BlockTags.WALLS) ||
                        groundState.is(BlockTags.SAND));
    }
}
