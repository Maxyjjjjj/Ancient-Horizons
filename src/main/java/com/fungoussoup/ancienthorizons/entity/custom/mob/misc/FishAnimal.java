package com.fungoussoup.ancienthorizons.entity.custom.mob.misc;

import com.fungoussoup.ancienthorizons.entity.ai.ModFishMoveControl;
import com.fungoussoup.ancienthorizons.entity.custom.mob.TrulyWaterAnimal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class FishAnimal extends TrulyWaterAnimal {
    protected FishAnimal(EntityType<? extends TrulyWaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new ModFishMoveControl(this);
    }
}
