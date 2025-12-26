package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.Stampedeable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * FIXED: Stampede behavior for herd animals
 * - Prevents animals getting stuck
 * - Better flee direction calculation
 * - Smoother movement
 * - Proper duration management
 */
public class StampedeGoal extends Goal {
    private final Animal animal;
    private final double speedModifier;
    private final int searchRadius;
    private Vec3 fleeDirection;
    private int stampedeTimer;
    private Vec3 lastPosition;
    private int stuckCounter;

    private static final int STAMPEDE_DURATION = 100; // 5 seconds
    private static final int RECALC_INTERVAL = 20; // Recalculate direction every second
    private static final double MIN_MOVEMENT = 0.05;

    public StampedeGoal(Animal animal) {
        this(animal, 1.6D, 16);
    }

    public StampedeGoal(Animal animal, double speedModifier, int searchRadius) {
        this.animal = animal;
        this.speedModifier = speedModifier;
        this.searchRadius = searchRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.lastPosition = animal.position();
    }

    @Override
    public boolean canUse() {
        if (!(animal instanceof Stampedeable stampedeable)) {
            return false;
        }
        return stampedeable.isStampeding();
    }

    @Override
    public boolean canContinueToUse() {
        if (stampedeTimer <= 0) {
            return false;
        }

        // Stop if stuck for too long
        if (stuckCounter > 40) {
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        stampedeTimer = STAMPEDE_DURATION;
        stuckCounter = 0;
        lastPosition = animal.position();
        calculateFleeDirection();
    }

    @Override
    public void tick() {
        stampedeTimer--;

        // Check if stuck
        Vec3 currentPos = animal.position();
        if (currentPos.distanceToSqr(lastPosition) < MIN_MOVEMENT) {
            stuckCounter++;

            // Recalculate direction if stuck
            if (stuckCounter > 10 && stampedeTimer % 10 == 0) {
                calculateFleeDirection();
            }
        } else {
            stuckCounter = 0;
        }
        lastPosition = currentPos;

        // Recalculate direction periodically
        if (stampedeTimer % RECALC_INTERVAL == 0) {
            calculateFleeDirection();
        }

        // Move in flee direction
        if (fleeDirection != null) {
            Vec3 targetPos = animal.position().add(fleeDirection.scale(3.0));

            // Use navigation system for better pathfinding
            animal.getNavigation().moveTo(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    speedModifier
            );
        }

        // Check if should stop stampeding
        if (stampedeTimer <= 0) {
            stopStampede();
        }
    }

    @Override
    public void stop() {
        stopStampede();
        animal.getNavigation().stop();
        fleeDirection = null;
        stampedeTimer = 0;
        stuckCounter = 0;
    }

    /**
     * Calculate the direction to flee
     */
    private void calculateFleeDirection() {
        Vec3 averagePosition = calculateAverageStampedePosition();

        if (averagePosition != null) {
            // Flee away from center of herd
            Vec3 currentPos = animal.position();
            fleeDirection = currentPos.subtract(averagePosition).normalize();

            // Add some randomness to prevent perfect parallel movement
            double randomAngle = (animal.getRandom().nextDouble() - 0.5) * 0.5;
            fleeDirection = rotateVector(fleeDirection, randomAngle);
        } else {
            // No herd found, flee in random direction
            double angle = animal.getRandom().nextDouble() * Math.PI * 2;
            fleeDirection = new Vec3(Math.cos(angle), 0, Math.sin(angle));
        }
    }

    /**
     * Calculate average position of stampeding animals
     */
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

    /**
     * Rotate a vector by an angle (in radians) around Y axis
     */
    private Vec3 rotateVector(Vec3 vec, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return new Vec3(
                vec.x * cos - vec.z * sin,
                vec.y,
                vec.x * sin + vec.z * cos
        );
    }

    /**
     * Stop stampeding
     */
    private void stopStampede() {
        if (animal instanceof Stampedeable stampedeable) {
            stampedeable.setStampeding(false);
        }
    }
}