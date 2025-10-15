package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class GoldfinchEntity extends AbstractPasserineEntity {
    public GoldfinchEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.GOLDFINCH.get().create(serverLevel);
    }
}
