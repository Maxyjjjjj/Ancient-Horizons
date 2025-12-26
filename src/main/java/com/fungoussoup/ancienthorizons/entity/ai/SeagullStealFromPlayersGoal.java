package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.SeagullEntity;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * FIXED: Seagull stealing behavior
 * - Reduced excessive stalking/circling
 * - Better approach patterns
 * - Smoother flee behavior
 * - Fixed potential infinite loops
 */
public class SeagullStealFromPlayersGoal extends Goal {
    private final SeagullEntity entity;
    private Vec3 fleeVec = null;
    private Player target;
    private int fleeTime = 0;
    private int stalkTime = 0;
    private boolean isStalkingPhase = false;

    private static final int STALK_DURATION = 60; // Reduced from 80
    private static final int MAX_STEAL_ATTEMPTS = 10;
    private static final double STEAL_RANGE = 2.5;
    private static final double CIRCLE_RADIUS_MIN = 3.5;
    private static final double CIRCLE_RADIUS_MAX = 5.0;

    private int stealAttempts = 0;

    public SeagullStealFromPlayersGoal(SeagullEntity entity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.TARGET));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        // Throttle checks
        long worldTime = this.entity.level().getGameTime() % 10;
        if (this.entity.getNoActionTime() >= 100 && worldTime != 0) {
            return false;
        }

        if (this.entity.getRandom().nextInt(15) != 0 && worldTime != 0) {
            return false;
        }

        if (entity.stealCooldown > 0) {
            return false;
        }

        if (this.entity.getMainHandItem().isEmpty() && this.entity.isHungry()) {
            Player valid = getClosestValidPlayer();
            if (valid != null) {
                target = valid;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.entity.aiItemFlag = true;
        this.isStalkingPhase = true;
        this.stalkTime = 0;
        this.stealAttempts = 0;
        this.entity.setFlying(true);
    }

    @Override
    public void stop() {
        this.entity.aiItemFlag = false;
        target = null;
        fleeVec = null;
        fleeTime = 0;
        stalkTime = 0;
        stealAttempts = 0;
        isStalkingPhase = false;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || target.isCreative()) {
            return false;
        }

        if (target.distanceTo(entity) > 30) {
            return false;
        }

        // Stop if took too long
        if (stealAttempts > MAX_STEAL_ATTEMPTS) {
            return false;
        }

        return entity.getMainHandItem().isEmpty() || fleeTime > 0;
    }

    @Override
    public void tick() {
        if (isStalkingPhase && stalkTime < STALK_DURATION) {
            tickStalkingPhase();
            return;
        }

        entity.setFlying(true);

        // Approach or flee logic
        if (fleeTime <= 0) {
            tickApproachPhase();
        } else {
            tickFleePhase();
        }
    }

    /**
     * Stalking phase - circle around player
     */
    private void tickStalkingPhase() {
        stalkTime++;

        // Calculate circle position with varying radius
        double radius = CIRCLE_RADIUS_MIN + Math.sin(stalkTime * 0.1) * (CIRCLE_RADIUS_MAX - CIRCLE_RADIUS_MIN);
        double angle = stalkTime * 0.15;

        Vec3 circlePos = getCirclePosition(target.position(), radius, angle);
        // Add height variation
        circlePos = circlePos.add(0, 3 + Math.sin(stalkTime * 0.2), 0);

        entity.getMoveControl().setWantedPosition(circlePos.x, circlePos.y, circlePos.z, 0.8F);

        // Occasional intimidation call
        if (stalkTime % 50 == 0 && entity.getRandom().nextInt(3) == 0) {
            entity.playSound(
                    entity.getAmbientSound(),
                    0.8F,
                    0.8F + entity.getRandom().nextFloat() * 0.4F
            );
        }

        if (stalkTime >= STALK_DURATION) {
            isStalkingPhase = false;
        }
    }

    /**
     * Approach phase - move toward player to steal
     */
    private void tickApproachPhase() {
        stealAttempts++;

        // More direct approach with slight randomness
        Vec3 targetPos = target.getEyePosition().add(
                (entity.getRandom().nextDouble() - 0.5) * 1.5,
                -0.5,
                (entity.getRandom().nextDouble() - 0.5) * 1.5
        );

        entity.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.1F);

        // Attempt steal when in range
        if (entity.distanceTo(target) < STEAL_RANGE && entity.getMainHandItem().isEmpty()) {
            if (hasFoods(target)) {
                ItemStack foodStack = getFoodItemFrom(target);
                if (!foodStack.isEmpty()) {
                    performSteal(foodStack);
                } else {
                    stop();
                }
            } else {
                stop();
            }
        }
    }

    /**
     * Flee phase - escape with stolen goods
     */
    private void tickFleePhase() {
        fleeTime--;

        // Calculate flee direction if needed
        if (fleeVec == null || entity.distanceToSqr(fleeVec) < 4.0) {
            fleeVec = entity.getBlockInViewAway(target.position(), 8);

            // Fallback if no valid flee position found
            if (fleeVec == null) {
                Vec3 away = entity.position().subtract(target.position()).normalize();
                fleeVec = entity.position().add(away.scale(10));
            }
        }

        if (fleeVec != null) {
            entity.setFlying(true);
            entity.getMoveControl().setWantedPosition(fleeVec.x, fleeVec.y, fleeVec.z, 1.2F);
        }

        // Taunt while fleeing (less frequent)
        if (fleeTime % 40 == 0 && entity.getRandom().nextInt(3) == 0) {
            entity.playSound(
                    entity.getAmbientSound(),
                    1.0F,
                    1.2F + entity.getRandom().nextFloat() * 0.3F
            );
        }
    }

    /**
     * Calculate position on circle around center
     */
    private Vec3 getCirclePosition(Vec3 center, double radius, double angle) {
        double x = center.x + Math.cos(angle) * radius;
        double z = center.z + Math.sin(angle) * radius;
        return new Vec3(x, center.y, z);
    }

    /**
     * Execute the theft
     */
    private void performSteal(ItemStack foodStack) {
        ItemStack copy = foodStack.copy();
        int stealAmount = Math.min(foodStack.getCount(), 1 + entity.getRandom().nextInt(2));
        foodStack.shrink(stealAmount);
        copy.setCount(stealAmount);

        entity.steal();
        entity.setItemInHand(InteractionHand.MAIN_HAND, copy);

        // Increase aggression
        entity.aggressionLevel = Math.min(entity.aggressionLevel + 1, 3);

        fleeTime = 60 + entity.getRandom().nextInt(40); // 3-5 seconds
        entity.stealCooldown = 800 + entity.getRandom().nextInt(1200); // 40-100 seconds

        // Alert nearby seagulls (with distance limit)
        List<SeagullEntity> nearby = entity.level().getEntitiesOfClass(
                SeagullEntity.class,
                entity.getBoundingBox().inflate(12),
                seagull -> seagull != entity && seagull.stealCooldown <= 0
        );

        // Limit how many seagulls get alerted
        int alertedCount = 0;
        for (SeagullEntity seagull : nearby) {
            if (alertedCount >= 3) break; // Max 3 seagulls alerted

            seagull.stealCooldown = 400 + seagull.getRandom().nextInt(800);
            seagull.aggressionLevel = Math.min(seagull.aggressionLevel + 1, 2);
            alertedCount++;
        }
    }

    /**
     * Find closest valid player target
     */
    private Player getClosestValidPlayer() {
        List<Player> list = entity.level().getEntitiesOfClass(
                Player.class,
                entity.getBoundingBox().inflate(15, 20, 15),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR
        );

        Player closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Player player : list) {
            if (!player.isInvisible() && hasFoods(player)) {
                double dist = player.distanceToSqr(entity);
                if (dist < closestDist) {
                    closest = player;
                    closestDist = dist;
                }
            }
        }

        return closest;
    }

    /**
     * Check if player has stealable food
     */
    private boolean hasFoods(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stackIn = player.getInventory().items.get(i);
            if (stackIn.is(ModTags.Items.SEAGULL_ROBBABLES)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get random food item from player hotbar
     */
    private ItemStack getFoodItemFrom(Player player) {
        List<ItemStack> foods = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stackIn = player.getInventory().items.get(i);
            if (stackIn.is(ModTags.Items.SEAGULL_ROBBABLES)) {
                foods.add(stackIn);
            }
        }

        if (!foods.isEmpty()) {
            return foods.get(entity.getRandom().nextInt(foods.size()));
        }

        return ItemStack.EMPTY;
    }
}