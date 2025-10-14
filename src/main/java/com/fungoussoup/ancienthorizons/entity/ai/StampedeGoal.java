package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.Stampedeable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class StampedeGoal extends Goal {
    private final Animal animal;
    private final double speedModifier;
    private final int searchRadius;
    private Vec3 fleeDirection;
    private int stampedeTimer;
    private static final int STAMPEDE_DURATION = 100; // 5 seconds (20 ticks per second)

    public StampedeGoal(Animal animal) {
        this(animal, 1.6D, 16);
    }

    public StampedeGoal(Animal animal, double speedModifier, int searchRadius) {
        this.animal = animal;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Check if this animal has been marked for stampede
        if (animal instanceof Stampedeable stampedeable) {
            return stampedeable.isStampeding();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return stampedeTimer > 0;
    }

    @Override
    public void start() {
        stampedeTimer = STAMPEDE_DURATION;

        // Calculate flee direction based on nearby stampeding animals
        Vec3 averagePosition = calculateAverageStampedePosition();
        if (averagePosition != null) {
            // Flee away from the average position of other stampeding animals
            Vec3 currentPos = animal.position();
            fleeDirection = currentPos.subtract(averagePosition).normalize();
        } else {
            // Random flee direction if no other animals found
            double angle = animal.getRandom().nextDouble() * Math.PI * 2;
            fleeDirection = new Vec3(Math.cos(angle), 0, Math.sin(angle));
        }
    }

    @Override
    public void tick() {
        stampedeTimer--;

        if (fleeDirection != null) {
            // Move in the flee direction
            Vec3 targetPos = animal.position().add(fleeDirection.scale(2.0));
            animal.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, speedModifier);
        }

        // Check if we should stop stampeding
        if (stampedeTimer <= 0 && animal instanceof Stampedeable stampedeable) {
            stampedeable.setStampeding(false);
        }
    }

    @Override
    public void stop() {
        fleeDirection = null;
        stampedeTimer = 0;
        if (animal instanceof Stampedeable stampedeable) {
            stampedeable.setStampeding(false);
        }
        animal.getNavigation().stop();
    }

    private Vec3 calculateAverageStampedePosition() {
        AABB searchBox = animal.getBoundingBox().inflate(searchRadius);
        List<? extends Animal> nearbyAnimals = animal.level().getEntitiesOfClass(
                animal.getClass(),
                searchBox,
                e -> e != animal && e instanceof Stampedeable s && s.isStampeding()
        );

        if (nearbyAnimals.isEmpty()) {
            return null;
        }

        Vec3 sum = Vec3.ZERO;
        for (Animal nearbyAnimal : nearbyAnimals) {
            sum = sum.add(nearbyAnimal.position());
        }
        return sum.scale(1.0 / nearbyAnimals.size());
    }
}