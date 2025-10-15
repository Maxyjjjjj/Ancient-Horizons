package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CanaryEntity extends AbstractPasserineEntity {
    public CanaryEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AlertOwnerGoal(this));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    private class AlertOwnerGoal extends Goal {
        private final CanaryEntity canary;
        private LivingEntity threat;

        public AlertOwnerGoal(CanaryEntity canaryEntity) {
            this.canary = canaryEntity;
        }

        @Override
        public boolean canUse() {
            if (!canary.isTame() || canary.getOwner() == null) {
                return false;
            }

            // Look for nearby hostile mobs
            Level level = canary.level();
            double radius = 12.0D;
            List<LivingEntity> hostiles = level.getEntitiesOfClass(
                    LivingEntity.class,
                    canary.getBoundingBox().inflate(radius),
                    entity -> !entity.getType().getCategory().isFriendly()
            );

            if (!hostiles.isEmpty()) {
                this.threat = hostiles.get(0);
                return true;
            }

            return false;
        }

        @Override
        public void start() {
            Player owner = (Player) canary.getOwner();
            if (owner != null && threat != null) {
                // Optional: play alert sound
                canary.playSound(SoundEvents.PARROT_AMBIENT, 1.0F, 1.2F);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false; // Only alert once per threat detection
        }
    }

}
