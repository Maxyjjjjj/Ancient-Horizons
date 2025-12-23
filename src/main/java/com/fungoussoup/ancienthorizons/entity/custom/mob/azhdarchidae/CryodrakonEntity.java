package com.fungoussoup.ancienthorizons.entity.custom.mob.azhdarchidae;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractLargeAzhdarchidEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CryodrakonEntity extends AbstractLargeAzhdarchidEntity {
    public CryodrakonEntity(EntityType<? extends AbstractLargeAzhdarchidEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.CRYODRAKON.get().create(level);
    }
}
