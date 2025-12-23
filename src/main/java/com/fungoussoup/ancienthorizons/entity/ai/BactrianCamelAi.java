package com.fungoussoup.ancienthorizons.entity.ai;

import java.util.Map;
import java.util.function.Predicate;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BactrianCamel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomLookAround;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;

public class BactrianCamelAi {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super BactrianCamel>>> SENSOR_TYPES;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

    public static void initMemories(BactrianCamel camel, RandomSource random) {
        camel.getBrain().setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 0);
        camel.getBrain().setMemory(MemoryModuleType.GAZE_COOLDOWN_TICKS, 0);
    }


    public static Brain.Provider<BactrianCamel> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    public static Brain<?> makeBrain(Brain<BactrianCamel> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.FIGHT);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<BactrianCamel> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new RememberHurtByTarget<>(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS)
        ));
    }


    @SuppressWarnings("deprecation")
    private static void initIdleActivity(Brain<BactrianCamel> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                // Highest priority: Spit at the target if one exists.
                Pair.of(0, new BactrianCamelSpitAttack(40, 80)),
                Pair.of(1, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(2, new AnimalMakeLove(ModEntities.BACTRIAN_CAMEL.get())),
                Pair.of(3, new RunOne<>(ImmutableList.of(
                        Pair.of(new FollowTemptation((p_250812_) -> 2.5F, (p_293990_) -> p_293990_.isBaby() ? 2.5D : 3.5D), 1),
                        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(BactrianCamel::refuseToMove), BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 2.5F)), 1)
                ))),
                Pair.of(4, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(BactrianCamel::refuseToMove), RandomStroll.stroll(2.0F)), 1),
                        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(BactrianCamel::refuseToMove), SetWalkTargetFromLookTarget.create(2.0F, 3)), 1),
                        Pair.of(new BactrianCamelAi.RandomSitting(20), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                )))
        ));
    }

    public static void updateActivity(BactrianCamel camel) {
        camel.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static Predicate<ItemStack> getTemptations() {
        return (stack) -> stack.is(ItemTags.CAMEL_FOOD);
    }

    static {
        SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.CAMEL_TEMPTATIONS, SensorType.NEAREST_ADULT);
        MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.ATTACK_TARGET);
    }

    public static class RandomSitting extends Behavior<BactrianCamel> {
        private final int chance;

        public RandomSitting(int chance) {
            super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                    MemoryModuleType.RIDE_TARGET, MemoryStatus.VALUE_ABSENT));
            this.chance = chance;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, BactrianCamel camel) {
            return camel.onGround()
                    && !camel.isInWaterOrBubble()
                    && level.getRandom().nextInt(this.chance) == 0
                    && !camel.refuseToMove();
        }

        @Override
        protected boolean canStillUse(ServerLevel level, BactrianCamel camel, long gameTime) {
            return false;
        }
    }

    public static class RememberHurtByTarget<E extends Mob> extends Behavior<E> {
        public RememberHurtByTarget() {
            super(Map.of(MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT));
        }

        @Override
        protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
            return true;
        }

        @Override
        protected void start(ServerLevel level, E mob, long gameTime) {
            mob.setTarget(mob.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).get());
        }
    }
}