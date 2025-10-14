package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.BactrianCamel;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BactrianCamelSpitAttack extends Behavior<BactrianCamel> {
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private int attackCooldown;

    public BactrianCamelSpitAttack(int attackIntervalMin, int attackIntervalMax) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
        ));
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, BactrianCamel pOwner) {
        // Don't run if we are on cooldown
        if (this.attackCooldown > 0) {
            --this.attackCooldown;
            return false;
        }
        // Check if there is a target
        return pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent();
    }

    @Override
    protected void start(ServerLevel pLevel, BactrianCamel pEntity, long pGameTime) {
        // Get the target from the brain's memory
        LivingEntity target = pEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        // Make the camel look at its target
        pEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));

        // Perform the attack
        pEntity.performRangedAttack(target, 1.0F);

        // Set the cooldown timer to a random value within the specified range
        this.attackCooldown = pLevel.getRandom().nextInt(attackIntervalMax - attackIntervalMin + 1) + attackIntervalMin;
    }
}