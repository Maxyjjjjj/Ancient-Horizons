package com.fungoussoup.ancienthorizons.entity.ai;

import javax.annotation.Nullable;

import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

public class SemiAquaticSwimGoal extends RandomStrollGoal {

    public SemiAquaticSwimGoal(Animal creature, double speed, int chance) {
        super(creature, speed, chance, false);
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle() || ((SemiAquaticAnimal) this.mob).shouldStopMoving() || this.mob.getTarget() != null || !this.mob.isInWater() && !this.mob.isInLava() && !((SemiAquaticAnimal) this.mob).shouldEnterWater()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.mob.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
            }
            final Vec3 vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.wantedX = vector3d.x;
                this.wantedY = vector3d.y;
                this.wantedZ = vector3d.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if(this.mob.hasRestriction() && this.mob.distanceToSqr(Vec3.atCenterOf(this.mob.getRestrictCenter())) > this.mob.getRestrictRadius() * this.mob.getRestrictRadius()){
            return DefaultRandomPos.getPosTowards(this.mob, 7, 3, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()), 1);
        }
        if(this.mob.getRandom().nextFloat() < 0.3F){
            Vec3 vector3d = findSurfaceTarget(this.mob, 15, 7);
            if(vector3d != null){
                return vector3d;
            }
        }
        Vec3 vector3d = DefaultRandomPos.getPos(this.mob, 7, 3);

        return vector3d;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        final BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
        return this.mob.level().getFluidState(blockpos).is(FluidTags.LAVA) || this.mob.level().getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level().getBlockState(blockpos).blocksMotion();
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.mob.level().getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir() && this.mob.level().getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
    }

    protected Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
        BlockPos upPos = creature.blockPosition();
        // Safety: avoid unbounded ascent if something went wrong in the world (bubble columns, top-of-world)
        int safety = 0;
        while ((creature.level().getFluidState(upPos).is(FluidTags.WATER) || creature.level().getFluidState(upPos).is(FluidTags.LAVA)) && safety++ < 128) {
            upPos = upPos.above();
        }
        if (safety >= 128) return null;
        if (isAirAbove(upPos.below(), 0, 0, 0) && canJumpTo(upPos.below(), 0, 0, 0)) {
            return new Vec3(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
        }
        return null;
    }
}