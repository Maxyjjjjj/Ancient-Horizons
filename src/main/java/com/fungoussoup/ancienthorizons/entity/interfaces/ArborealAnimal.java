package com.fungoussoup.ancienthorizons.entity.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Interface for entities that can climb natural blocks like logs and leaves
 * (trees), unlike spiders which can climb any surface.
 */
public interface ArborealAnimal {

    /**
     * @return The climbing speed multiplier (e.g., 0.1 for slow, 0.2 for fast)
     */
    default double getClimbingSpeed() {
        return 0.1;
    }

    /**
     * @return Whether this entity can currently climb
     */
    default boolean canClimb() {
        return true;
    }

    /**
     * Checks if the entity is touching a climbable block (logs or leaves).
     * This is more restrictive than spider climbing.
     */
    default boolean isTouchingClimbableBlock(Mob entity) {
        if (!entity.horizontalCollision) {
            return false;
        }

        BlockPos pos = entity.blockPosition();
        BlockState state = entity.level().getBlockState(pos);

        // Check current position and positions around the entity
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;

                BlockPos checkPos = pos.offset(x, 0, z);
                BlockState checkState = entity.level().getBlockState(checkPos);

                if (isClimbableBlock(checkState)) {
                    return true;
                }

                // Also check one block up
                BlockPos checkPosUp = checkPos.above();
                BlockState checkStateUp = entity.level().getBlockState(checkPosUp);
                if (isClimbableBlock(checkStateUp)) {
                    return true;
                }
            }
        }

        return isClimbableBlock(state);
    }

    /**
     * Determines if a block state is climbable for arboreal animals.
     * Only logs and leaves are climbable.
     */
    default boolean isClimbableBlock(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
    }

    /**
     * Called every tick to handle climbing behavior.
     * Should be called in the entity's tick() method.
     */
    default void handleClimbing(Mob entity) {
        if (isTouchingClimbableBlock(entity) && canClimb()) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < getClimbingSpeed()) {
                entity.setDeltaMovement(
                        motion.x,
                        getClimbingSpeed(),
                        motion.z
                );
            }
        }
    }
}