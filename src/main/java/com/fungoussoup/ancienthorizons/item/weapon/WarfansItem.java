package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class WarfansItem extends Item {

    private static final Random RANDOM = new Random();

    public WarfansItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.5F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_SPEED_ID, 2.0F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            boolean dualWield = player.getMainHandItem().getItem() instanceof WarfansItem && player.getOffhandItem().getItem() instanceof WarfansItem;

            if (dualWield) {
                target.hurt(player.damageSources().playerAttack(player), 2.0F);
                player.getCooldowns().addCooldown(this, 5);
            }

            if (RANDOM.nextFloat() < (dualWield ? 0.5F : 0.25F)) {
                Vec3 knockback = target.position().subtract(player.position()).normalize().scale(0.6);
                target.push(knockback.x, 0.4, knockback.z);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, 20);
            Vec3 push = player.getLookAngle().scale(1.2);
            player.push(-push.x, 0.1, -push.z);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
