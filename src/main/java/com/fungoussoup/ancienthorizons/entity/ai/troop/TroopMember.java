package com.fungoussoup.ancienthorizons.entity.ai.troop;

import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public interface TroopMember {
    UUID getTroopId();
    void setTroopId(UUID id);

    TroopRank getTroopRank();
    void setTroopRank(TroopRank rank);

    Troop getTroop();
    LivingEntity getEntity();
}


