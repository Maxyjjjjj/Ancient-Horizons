package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.tags.EntityTypeTags.UNDEAD;

public class KatanaItem extends Item {

    public KatanaItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_DAMAGE_ID, 6.0F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_SPEED_ID, 3.4F, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            float totalBonusDamage = 0.0f;

            // Undead bonus
            if (target.getType().is(UNDEAD)) {
                totalBonusDamage *= 1.5f;
            }

            // Speed bonus (Thinness)
            totalBonusDamage += getSpeedBonusDamage(player);

            if (totalBonusDamage > 0) {
                target.hurt(player.damageSources().playerAttack(player), totalBonusDamage);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    private static float getSpeedBonusDamage(Player player) {
        Vec3 movement = player.getDeltaMovement();
        double horizontalSpeed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);

        float speedBonusDamage = (float) (horizontalSpeed * 8.0f);
        return Math.min(speedBonusDamage, 1000f);
    }
}
