package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EarthwormEntity;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class ChaffinchEntity extends AbstractPasserineEntity {
    public ChaffinchEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.CHAFFINCH.get().create(serverLevel);
    }
}
