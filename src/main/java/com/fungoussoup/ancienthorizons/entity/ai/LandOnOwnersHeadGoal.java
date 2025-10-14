package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.HeadRidingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;

public class LandOnOwnersHeadGoal extends Goal {
    private final HeadRidingEntity entity;
    private ServerPlayer owner;
    private boolean isSittingOnHead;

    public LandOnOwnersHeadGoal(HeadRidingEntity entity) {
        this.entity = entity;
    }

    public boolean canUse() {
        ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
        boolean flag = serverplayer != null && !serverplayer.isSpectator() && !serverplayer.getAbilities().flying && !serverplayer.isInWater() && !serverplayer.isInPowderSnow;
        return !this.entity.isOrderedToSit() && flag && this.entity.canSitOnHead();
    }

    public boolean isInterruptable() {
        return !this.isSittingOnHead;
    }

    public void start() {
        this.owner = (ServerPlayer)this.entity.getOwner();
        this.isSittingOnHead = false;
    }

    public void tick() {
        if (!this.isSittingOnHead && !this.entity.isInSittingPose() && !this.entity.isLeashed() && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnHead = this.entity.setEntityOnHead(this.owner);
        }

    }
}
