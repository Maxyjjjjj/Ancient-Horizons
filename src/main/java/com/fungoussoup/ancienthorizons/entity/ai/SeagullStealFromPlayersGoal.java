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
import java.util.Objects;

public class SeagullStealFromPlayersGoal extends Goal {

    private final SeagullEntity entity;
    private Vec3 fleeVec = null;
    private Player target;
    private int fleeTime = 0;
    private int stalkTime = 0;
    private boolean isStalkingPhase = false;
    private static final int STALK_DURATION = 80; // 4 seconds

    public SeagullStealFromPlayersGoal(SeagullEntity entity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.TARGET));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        long worldTime = this.entity.level().getGameTime() % 10;
        if (this.entity.getNoActionTime() >= 100 && worldTime != 0) {
            return false;
        }
        if (this.entity.getRandom().nextInt(12) != 0 && worldTime != 0 || entity.stealCooldown > 0) {
            return false;
        }
        if(this.entity.getMainHandItem().isEmpty() && this.entity.isHungry()){
            Player valid = getClosestValidPlayer();
            if(valid != null){
                target = valid;
                return true;
            }
        }
        return false;
    }

    public void start(){
        this.entity.aiItemFlag = true;
        this.isStalkingPhase = true;
        this.stalkTime = 0;
        this.entity.setFlying(true);
    }

    public void stop(){
        this.entity.aiItemFlag = false;
        target = null;
        fleeVec = null;
        fleeTime = 0;
        stalkTime = 0;
        isStalkingPhase = false;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !target.isCreative() &&
                (entity.getMainHandItem().isEmpty() || fleeTime > 0) &&
                target.distanceTo(entity) < 25;
    }

    public void tick(){
        if (isStalkingPhase && stalkTime < STALK_DURATION) {
            // Stalking phase - circle around player
            stalkTime++;
            Vec3 circlePos = getCirclePosition(target.position(), 4.0 + Math.sin(stalkTime * 0.1) * 1.5, stalkTime * 0.15);
            circlePos = circlePos.add(0, 3 + Math.sin(stalkTime * 0.2) * 2, 0);

            entity.setFlying(true);
            entity.getMoveControl().setWantedPosition(circlePos.x, circlePos.y, circlePos.z, 0.9F);

            // Occasionally call to intimidate
            if (stalkTime % 40 == 0 && entity.getRandom().nextInt(3) == 0) {
                entity.playSound(Objects.requireNonNull(entity.getAmbientSound()), 1.0F, 0.8F + entity.getRandom().nextFloat() * 0.4F);
            }

            if (stalkTime >= STALK_DURATION) {
                isStalkingPhase = false;
            }
            return;
        }

        entity.setFlying(true);

        // Approach target
        if (fleeTime <= 0) {
            // More erratic approach pattern
            Vec3 targetPos = target.position().add(
                    (entity.getRandom().nextDouble() - 0.5) * 2,
                    target.getEyeY() - target.getY(),
                    (entity.getRandom().nextDouble() - 0.5) * 2
            );

            entity.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.2F);

            if(entity.distanceTo(target) < 2.5F && entity.getMainHandItem().isEmpty()){
                if(hasFoods(target)){
                    ItemStack foodStack = getFoodItemFrom(target);
                    if(!foodStack.isEmpty()){
                        performSteal(foodStack);
                    } else {
                        stop();
                    }
                } else {
                    stop();
                }
            }
        } else {
            // Flee behavior with more sophisticated pathing
            if(fleeVec == null){
                fleeVec = entity.getBlockInViewAway(target.position(), 6 + entity.getRandom().nextInt(4));
            }
            if(fleeVec != null){
                entity.setFlying(true);
                entity.getMoveControl().setWantedPosition(fleeVec.x, fleeVec.y, fleeVec.z, 1.3F);
                if(entity.distanceToSqr(fleeVec) < 9){
                    fleeVec = entity.getBlockInViewAway(fleeVec, 6 + entity.getRandom().nextInt(4));
                }
            }
            fleeTime--;

            // Taunt the player while fleeing
            if (fleeTime % 30 == 0 && entity.getRandom().nextInt(2) == 0) {
                entity.playSound(Objects.requireNonNull(entity.getAmbientSound()), 1.2F, 1.2F + entity.getRandom().nextFloat() * 0.3F);
            }
        }
    }

    private Vec3 getCirclePosition(Vec3 center, double radius, double angle) {
        double x = center.x + Math.cos(angle) * radius;
        double z = center.z + Math.sin(angle) * radius;
        return new Vec3(x, center.y, z);
    }

    private void performSteal(ItemStack foodStack) {
        ItemStack copy = foodStack.copy();
        int stealAmount = Math.min(foodStack.getCount(), 1 + entity.getRandom().nextInt(2));
        foodStack.shrink(stealAmount);
        copy.setCount(stealAmount);

        entity.steal();
        entity.setItemInHand(InteractionHand.MAIN_HAND, copy);

        // Increase aggression towards this player
        entity.aggressionLevel = Math.min(entity.aggressionLevel + 1, 3);

        fleeTime = 80 + entity.getRandom().nextInt(40); // 4-6 seconds
        entity.stealCooldown = 1000 + entity.getRandom().nextInt(2000); // 50-150 seconds

        // Alert nearby seagulls
        List<SeagullEntity> nearby = entity.level().getEntitiesOfClass(
                SeagullEntity.class,
                entity.getBoundingBox().inflate(15)
        );

        for (SeagullEntity seagull : nearby) {
            if (seagull != entity && seagull.stealCooldown <= 0) {
                seagull.stealCooldown = 500 + seagull.getRandom().nextInt(1000);
                seagull.aggressionLevel = Math.min(seagull.aggressionLevel + 1, 2);
            }
        }
    }

    private Player getClosestValidPlayer(){
        List<Player> list = entity.level().getEntitiesOfClass(
                Player.class,
                entity.getBoundingBox().inflate(15, 25, 15),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR
        );

        Player closest = null;
        if(!list.isEmpty()){
            for(Player player : list){
                if((closest == null || closest.distanceTo(entity) > player.distanceTo(entity)) &&
                        hasFoods(player) && !player.isInvisible()){
                    closest = player;
                }
            }
        }
        return closest;
    }

    private boolean hasFoods(Player player){
        for(int i = 0; i < 9; i++){
            ItemStack stackIn = player.getInventory().items.get(i);
            if(stackIn.is(ModTags.Items.SEAGULL_ROBBABLES)){
                return true;
            }
        }
        return false;
    }

    private ItemStack getFoodItemFrom(Player player){
        List<ItemStack> foods = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            ItemStack stackIn = player.getInventory().items.get(i);
            if(stackIn.is(ModTags.Items.SEAGULL_ROBBABLES)){
                foods.add(stackIn);
            }
        }
        if(!foods.isEmpty()){
            return foods.get(entity.getRandom().nextInt(foods.size()));
        }
        return ItemStack.EMPTY;
    }
}
