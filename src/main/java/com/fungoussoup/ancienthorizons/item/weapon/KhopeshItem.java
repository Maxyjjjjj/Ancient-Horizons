package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KhopeshItem extends Item {

    public KhopeshItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hasDualKhopesh(player)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000; // hold indefinitely
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
        if (!(user instanceof Player player)) return;
        if (!hasDualKhopesh(player)) {
            player.stopUsingItem();
            return;
        }

        /* VISUAL SPIN (client + server) */
        player.setYRot(player.getYRot() + 30.0F);
        player.yBodyRot = player.getYRot();

        if (level.isClientSide) {
            double time = (player.tickCount + level.getGameTime()) * 0.3D;

            for (int i = 0; i < 6; i++) {
                double angle = time + (Math.PI * 2 * i / 6);
                double radius = 1.6D;

                double x = player.getX() + Math.cos(angle) * radius;
                double z = player.getZ() + Math.sin(angle) * radius;
                double y = player.getY() + 1.0D + Math.sin(time) * 0.2D;

                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,
                        x, y, z,
                        0, 0, 0
                );
            }
        }

        ServerLevel server = (ServerLevel) level;

        AABB area = player.getBoundingBox().inflate(2.5D);
        List<LivingEntity> targets = server.getEntitiesOfClass(
                LivingEntity.class,
                area,
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {

            // RAW DAMAGE
            target.hurt(server.damageSources().playerAttack(player), 4.0F);

            // RADIAL KNOCKBACK
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double dist = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));

            target.push(
                    (dx / dist) * 0.8D,
                    0.25D,
                    (dz / dist) * 0.8D
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            player.setYRot(player.getYRot());
        }
    }

    @Override
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
        if (source.is(DamageTypes.ARROW) || source.is(DamageTypes.SONIC_BOOM)) {
            return false;
        }
        return super.canBeHurtBy(stack, source);
    }


    private boolean hasDualKhopesh(Player player) {
        return player.getMainHandItem().getItem() instanceof KhopeshItem
                && player.getOffhandItem().getItem() instanceof KhopeshItem;
    }
}
