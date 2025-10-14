package com.fungoussoup.ancienthorizons.entity.ai.troop.goals;

import com.fungoussoup.ancienthorizons.entity.ai.troop.Troop;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopMember;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopRank;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ChimpanzeeEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class FollowTroopGoal<T extends ChimpanzeeEntity & TroopMember>
        extends Goal {

    private final T chimp;
    private TroopMember leader;
    private final double speedModifier;
    private final float minDist;
    private final float maxDist;

    public FollowTroopGoal(T chimp, double speed, float minDist, float maxDist) {
        this.chimp = chimp;
        this.speedModifier = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Troop troop = chimp.getTroop();
        if (troop == null || chimp.getTroopRank() == TroopRank.ALPHA
                || chimp.getTroopRank() == TroopRank.JUVENILE) {
            return false;
        }

        leader = troop.getLeader();
        return leader != null && chimp.distanceTo(leader.getEntity()) > minDist;
    }

    @Override
    public void tick() {
        if (leader != null && chimp.distanceTo(leader.getEntity()) > minDist) {
            chimp.getNavigation().moveTo(leader.getEntity(), speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return leader != null && !leader.getEntity().isDeadOrDying()
                && chimp.distanceTo(leader.getEntity()) > minDist
                && chimp.distanceTo(leader.getEntity()) < maxDist;
    }
}

