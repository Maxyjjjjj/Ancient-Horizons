package com.fungoussoup.ancienthorizons.entity.ai.troop.goals;

import com.fungoussoup.ancienthorizons.entity.ai.troop.Troop;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopMember;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopRank;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ChimpanzeeEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;
import java.util.List;

public class DefendTroopGoal<T extends ChimpanzeeEntity & com.fungoussoup.ancienthorizons.entity.ai.troop.TroopMember>
        extends Goal {

    private final T chimp;
    private LivingEntity targetThreat;

    public DefendTroopGoal(T chimp) {
        this.chimp = chimp;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Troop troop = chimp.getTroop();
        if (troop == null || chimp.getTroopRank() == TroopRank.JUVENILE) {
            return false;
        }

        List<TroopMember> members = troop.getMembers();
        for (TroopMember member : members) {
            if (member.getEntity() instanceof LivingEntity living && living.getLastHurtByMob() != null) {
                targetThreat = living.getLastHurtByMob();
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        chimp.setTarget(targetThreat);
    }
}

