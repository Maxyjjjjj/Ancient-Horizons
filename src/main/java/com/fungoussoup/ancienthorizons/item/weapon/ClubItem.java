package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ClubItem extends Item {

    public ClubItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof Player player) {
            float fall = player.fallDistance;

            // extra durability loss if attack was done after falling
            if (fall > 0.0F) {
                int bonusDamage = 1 + (int)(fall / 3.0F);
                stack.hurtAndBreak(bonusDamage, player, EquipmentSlot.MAINHAND);
            }
        }

        return result;
    }
}