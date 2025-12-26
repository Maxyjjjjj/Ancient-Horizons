package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KatanaItem extends Item {

    public KatanaItem(Properties properties) {
        super(properties);
    }

    /* ---------- ATTRIBUTES ---------- */

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                        new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                BASE_ATTACK_DAMAGE_ID, 6.0F,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                        new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                BASE_ATTACK_SPEED_ID, 3.4F,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    /* ---------- CORE DAMAGE LOGIC ---------- */

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) {
            return super.hurtEnemy(stack, target, attacker);
        }

        float baseDamage = (float) player.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE
        );

        float undeadMultiplier = target.getType().is(EntityTypeTags.UNDEAD) ? 1.25f : 1.0f;

        float finalDamage = baseDamage * undeadMultiplier;

        target.hurt(
                player.damageSources().playerAttack(player),
                finalDamage
        );

        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(this.getDefaultInstance()));
        return true; // prevent vanilla double damage
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }
}
