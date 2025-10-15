package com.fungoussoup.ancienthorizons.entity.custom.mob.passerine;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BlackcapEntity extends AbstractPasserineEntity{
    public BlackcapEntity(EntityType<? extends AbstractPasserineEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.BLACKCAP.get().create(serverLevel);
    }
}
