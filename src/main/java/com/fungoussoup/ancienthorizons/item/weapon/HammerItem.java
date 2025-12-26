package com.fungoussoup.ancienthorizons.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem {
    private static final double LAUNCH_RADIUS = 4.0; // Radius to search for mobs
    private static final double LAUNCH_FORCE = 0.8; // Vertical launch force
    private static final int COOLDOWN_TICKS = 40; // 2 seconds cooldown

    public HammerItem(Tier tier, Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
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
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Check if the player is hitting the ground (top face of a block)
            if (context.getClickedFace() == Direction.UP && !player.isCrouching()) {
                // Launch surrounding mobs
                launchSurroundingMobs(level, pos, serverPlayer);

                // Add cooldown to prevent spam
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    private void launchSurroundingMobs(Level level, BlockPos hitPos, ServerPlayer player) {
        Vec3 centerPos = Vec3.atCenterOf(hitPos.above()); // Center above the hit block

        // Create an AABB (bounding box) around the hit position
        AABB searchArea = new AABB(hitPos).inflate(LAUNCH_RADIUS);

        // Find all living entities in the area
        List<LivingEntity> nearbyMobs = level.getEntitiesOfClass(LivingEntity.class, searchArea,
                entity -> entity != player && entity.isAlive() && !entity.isSpectator());

        for (LivingEntity mob : nearbyMobs) {
            // Calculate distance for force scaling
            double distance = mob.position().distanceTo(centerPos);
            if (distance <= LAUNCH_RADIUS) {
                // Scale launch force based on distance (closer = stronger launch)
                double forceMultiplier = Math.max(0.2, 1.0 - (distance / LAUNCH_RADIUS));

                // Calculate horizontal knockback direction (away from impact point)
                Vec3 knockbackDirection = mob.position().subtract(centerPos).normalize();

                // Apply launch force (mostly vertical with some horizontal knockback)
                Vec3 launchVelocity = new Vec3(
                        knockbackDirection.x * 0.3 * forceMultiplier, // Horizontal X
                        LAUNCH_FORCE * forceMultiplier, // Vertical (main launch)
                        knockbackDirection.z * 0.3 * forceMultiplier  // Horizontal Z
                );

                // Apply the velocity
                mob.setDeltaMovement(mob.getDeltaMovement().add(launchVelocity));
                mob.hasImpulse = true; // Mark entity for velocity update

                // Optional: Add a small amount of damage (like fall damage)
                // mob.hurt(level.damageSources().playerAttack(player), 1.0f);
            }
        }
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, ServerPlayer player) {
        List<BlockPos> positions = new ArrayList<>();

        BlockHitResult traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if(traceResult.getType() == HitResult.Type.MISS) {
            return positions;
        }

        if(traceResult.getDirection() == Direction.DOWN || traceResult.getDirection() == Direction.UP) {
            for(int x = -range; x <= range; x++) {
                for(int y = -range; y <= range; y++) {
                    positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
                }
            }
        }

        if(traceResult.getDirection() == Direction.NORTH || traceResult.getDirection() == Direction.SOUTH) {
            for(int x = -range; x <= range; x++) {
                for(int y = -range; y <= range; y++) {
                    positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
                }
            }
        }

        if(traceResult.getDirection() == Direction.EAST || traceResult.getDirection() == Direction.WEST) {
            for(int x = -range; x <= range; x++) {
                for(int y = -range; y <= range; y++) {
                    positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
                }
            }
        }

        return positions;
    }
}

