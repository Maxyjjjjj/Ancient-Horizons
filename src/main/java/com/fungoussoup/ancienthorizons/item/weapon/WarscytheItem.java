package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WarscytheItem extends Item {

    public WarscytheItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(2.5D), // sweep radius
                    e -> e != player && e != target && e.isAlive()
            );

            for (LivingEntity mob : nearby) {
                mob.hurt(player.damageSources().playerAttack(player), 2.0F);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}