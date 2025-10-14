package com.fungoussoup.ancienthorizons.entity.ai.troop.goals;

import com.fungoussoup.ancienthorizons.entity.ai.troop.Troop;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopMember;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopRank;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ChimpanzeeEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class PatrolTerritoryGoal<T extends ChimpanzeeEntity & TroopMember>
        extends RandomStrollGoal {

    private final T chimp;
    private final Troop troop;
    private final Random random = new Random();

    public PatrolTerritoryGoal(T chimp, double speed) {
        super(chimp, speed);
        this.chimp = chimp;
        this.troop = chimp.getTroop();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return troop != null && chimp.getTroopRank() == TroopRank.ALPHA
                && super.canUse();
    }

    @Override
    protected Vec3 getPosition() {
        int x = troop.getTerritoryX() + random.nextInt(troop.getTerritoryRadius() * 2) - troop.getTerritoryRadius();
        int z = troop.getTerritoryZ() + random.nextInt(troop.getTerritoryRadius() * 2) - troop.getTerritoryRadius();
        int y = chimp.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

        return new Vec3(x, y, z);
    }
}

