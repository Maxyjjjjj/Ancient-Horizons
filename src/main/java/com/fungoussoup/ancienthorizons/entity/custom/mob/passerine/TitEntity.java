package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TitEntity extends AbstractPasserineEntity {
    public TitEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level, PathNavigation groundNavigation, PathNavigation flyingNavigation) {
        super(entityType, level, groundNavigation, flyingNavigation);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.TIT.get().create(serverLevel);
    }
}
