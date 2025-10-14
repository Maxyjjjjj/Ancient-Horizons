package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EarthwormEntity;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BullfinchEntity extends AbstractPasserineEntity {

    public BullfinchEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.BULLFINCH.get().create(serverLevel);
    }
}
