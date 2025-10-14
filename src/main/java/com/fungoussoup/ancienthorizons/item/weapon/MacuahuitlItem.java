package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public class MacuahuitlItem extends Item {

    private static final Random RANDOM = new Random();

    public MacuahuitlItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDeadOrDying() && attacker instanceof Player player && !player.level().isClientSide) {
            ServerLevel level = (ServerLevel) player.level();

            // Small chance to drop mob head
            if (RANDOM.nextFloat() < 0.15f) {
                if (target.getType() == EntityType.ZOMBIE) {
                    target.spawnAtLocation(Items.ZOMBIE_HEAD);
                }
                if (target.getType() == EntityType.SKELETON) {
                    target.spawnAtLocation(Items.SKELETON_SKULL);
                }
                if (target.getType() == EntityType.WITHER_SKELETON) {
                    target.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
                }
                if (target.getType() == EntityType.SKELETON) {
                    target.spawnAtLocation(Items.SKELETON_SKULL);
                }
                if (target.getType() == EntityType.CREEPER) {
                    target.spawnAtLocation(Items.CREEPER_HEAD);
                }
                if (target.getType() == EntityType.PIGLIN || target.getType() == EntityType.PIGLIN_BRUTE) {
                    target.spawnAtLocation(Items.PIGLIN_HEAD);
                }
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}