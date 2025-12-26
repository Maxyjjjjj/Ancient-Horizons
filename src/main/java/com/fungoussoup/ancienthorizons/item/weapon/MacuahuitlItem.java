package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class MacuahuitlItem extends Item {

    private static final Random RANDOM = new Random();

    public MacuahuitlItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_DAMAGE_ID, 6.0F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_SPEED_ID, 3.4F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
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