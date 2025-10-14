package com.fungoussoup.ancienthorizons.compat.sereneseasons;

import com.fungoussoup.ancienthorizons.entity.interfaces.Hibernatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = "ancienthorizons")
public class SereneSeasonHibernation {

    private static final Set<EntityType<?>> HIBERNATING_ANIMALS = new HashSet<>();

    public static void registerHibernatingAnimal(String modId, String entityName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entityName);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
        HIBERNATING_ANIMALS.add(type);
    }

    public static boolean canAnimalHibernate(EntityType<?> type) {
        return HIBERNATING_ANIMALS.contains(type);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!HIBERNATING_ANIMALS.contains(animal.getType())) return;
        if (animal instanceof Hibernatable) {
            animal.goalSelector.addGoal(1, new HibernationGoal((Mob) animal, (Hibernatable) animal));
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!HIBERNATING_ANIMALS.contains(animal.getType())) return;
        if (!(animal instanceof Hibernatable hibernatable)) return;
        if (!(animal.level() instanceof ServerLevel level)) return;

        boolean shouldHibernate = getSereneSeason(level) == Season.WINTER;
        boolean isHibernating = hibernatable.isHibernating();

        if (shouldHibernate && !isHibernating) {
            hibernatable.setHibernating(true);
            startHibernation(animal);
        } else if (!shouldHibernate && isHibernating) {
            hibernatable.setHibernating(false);
            endHibernation(animal);
        }

        if (hibernatable.isHibernating()) {
            applyHibernationEffects(animal);
        }
    }

    private static Season getSereneSeason(Level level) {
        return SeasonHelper.getSeasonState(level).getSeason();
    }

    private static void startHibernation(Animal animal) {
        if (!(animal instanceof Mob mob)) return;

        BlockPos spot = findHibernationSpot(animal);
        if (spot != null) {
            animal.teleportTo(spot.getX() + 0.5, spot.getY() + 1, spot.getZ() + 0.5);
        }

        mob.setNoAi(true);
        mob.setSilent(true);
    }

    private static void endHibernation(Animal animal) {
        if (!(animal instanceof Mob mob)) return;

        mob.setNoAi(false);
        mob.setSilent(false);
    }

    private static void applyHibernationEffects(Animal animal) {
        animal.setDeltaMovement(animal.getDeltaMovement().scale(0.1));

        if (animal.tickCount % 100 == 0 && animal.getHealth() < animal.getMaxHealth()) {
            animal.heal(0.5f);
        }

        animal.setTarget(null);
    }

    private static BlockPos findHibernationSpot(Animal animal) {
        Level level = animal.level();
        BlockPos origin = animal.blockPosition();

        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                for (int y = -4; y <= 4; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (isValidHibernationSpot(level, pos)) return pos;
                }
            }
        }
        return origin;
    }

    private static boolean isValidHibernationSpot(Level level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        return (ground.isSolid() && above.isAir()) ||
                (ground.is(Blocks.GRASS_BLOCK) && level.getBlockState(pos.above(2)).isSolid()) ||
                (ground.is(Blocks.STONE) && above.isAir());
    }

    public static class HibernationGoal extends Goal {
        private final Mob mob;
        private final Hibernatable hibernatable;

        public HibernationGoal(Mob mob, Hibernatable hibernatable) {
            this.mob = mob;
            this.hibernatable = hibernatable;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return hibernatable.isHibernating();
        }

        @Override
        public boolean canContinueToUse() {
            return hibernatable.isHibernating();
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.1));
        }
    }
}