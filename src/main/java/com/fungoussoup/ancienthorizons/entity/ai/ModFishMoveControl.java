package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.FishAnimal;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class ModFishMoveControl extends MoveControl {
    private final FishAnimal animal;

    public ModFishMoveControl(FishAnimal animal) {
        super(animal);
        this.animal = animal;
    }

    public void tick() {
        if (this.animal.isEyeInFluid(FluidTags.WATER)) {
            this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0F, 0.005, 0.0F));
        }

        if (this.operation == Operation.MOVE_TO && !this.animal.getNavigation().isDone()) {
            float f = (float)(this.speedModifier * this.animal.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.animal.setSpeed(Mth.lerp(0.125F, this.animal.getSpeed(), f));
            double d0 = this.wantedX - this.animal.getX();
            double d1 = this.wantedY - this.animal.getY();
            double d2 = this.wantedZ - this.animal.getZ();
            if (d1 != (double)0.0F) {
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                this.animal.setDeltaMovement(this.animal.getDeltaMovement().add(0.0F, (double)this.animal.getSpeed() * (d1 / d3) * 0.1, 0.0F));
            }

            if (d0 != (double)0.0F || d2 != (double)0.0F) {
                float f1 = (float)(Mth.atan2(d2, d0) * (double)180.0F / (double)(float)Math.PI) - 90.0F;
                this.animal.setYRot(this.rotlerp(this.animal.getYRot(), f1, 90.0F));
                this.animal.yBodyRot = this.animal.getYRot();
            }
        } else {
            this.animal.setSpeed(0.0F);
        }
    }
}