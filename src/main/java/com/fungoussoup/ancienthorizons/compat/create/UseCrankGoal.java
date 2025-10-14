package com.fungoussoup.ancienthorizons.compat.create;

import com.fungoussoup.ancienthorizons.entity.interfaces.CuriousAndIntelligentAnimal;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class UseCrankGoal extends Goal {
    private final CuriousAndIntelligentAnimal curiousAnimal;
    private BlockPos crankPos;
    private int cooldownTicks = 0;

    public UseCrankGoal(CuriousAndIntelligentAnimal curiousAnimal) {
        this.curiousAnimal = curiousAnimal;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }

        crankPos = findNearbyCrank();
        return crankPos != null;
    }

    @Override
    public void start() {
        if (crankPos != null) {
            curiousAnimal.getNavigation().moveTo(crankPos.getX() + 0.5, crankPos.getY(), crankPos.getZ() + 0.5, 1.0D);
        }
    }

    @Override
    public void tick() {
        if (crankPos == null) return;

        double distanceSq = curiousAnimal.distanceToSqr(crankPos.getX() + 0.5, crankPos.getY(), crankPos.getZ() + 0.5);
        if (distanceSq < 3.0D) {
            BlockState state = curiousAnimal.level().getBlockState(crankPos);
            if (state.getBlock() instanceof HandCrankBlock crank) {
                Rotation rotation = curiousAnimal.getRandom().nextBoolean() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
                // Use the Level directly for the rotate method
                crank.rotate(state, (Level) curiousAnimal.level(), crankPos, rotation);
                cooldownTicks = 200 + curiousAnimal.getRandom().nextInt(200);
            }
            crankPos = null; // reset
        }
    }

    @Override
    public boolean canContinueToUse() {
        return crankPos != null && curiousAnimal.distanceToSqr(crankPos.getX() + 0.5, crankPos.getY(), crankPos.getZ() + 0.5) >= 3.0D;
    }

    private BlockPos findNearbyCrank() {
        BlockPos origin = curiousAnimal.blockPosition();
        int range = 5;

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-range, -1, -range), origin.offset(range, 1, range))) {
            if (curiousAnimal.level().getBlockState(pos).getBlock() instanceof HandCrankBlock) {
                return pos.immutable();
            }
        }
        return null;
    }
}