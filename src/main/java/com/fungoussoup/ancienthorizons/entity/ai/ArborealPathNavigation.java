package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.ArborealAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class ArborealPathNavigation extends GroundPathNavigation {
    public ArborealPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected boolean canMoveDirectly(net.minecraft.world.phys.Vec3 start, net.minecraft.world.phys.Vec3 end) {
        // Allows the mob to move directly toward a target even if it's "up"
        if (this.mob instanceof ArborealAnimal arboreal) {
            arboreal.canClimb();
        }
        return super.canMoveDirectly(start, end);
    }

    @Override
    public void tick() {
        super.tick();
        this.mob.setNoGravity(this.mob instanceof ArborealAnimal);
    }
}