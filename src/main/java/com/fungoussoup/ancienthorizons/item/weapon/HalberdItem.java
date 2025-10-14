package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HalberdItem extends Item {

    public HalberdItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            // Halberd allows attacking entities slightly further away
            List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(1.5D), // extra reach
                    e -> e != player && e.isAlive()
            );

            for (LivingEntity mob : nearby) {
                mob.hurt(player.damageSources().playerAttack(player), 4.0F);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}