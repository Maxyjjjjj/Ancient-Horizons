package com.fungoussoup.ancienthorizons.compat.sereneseasons;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.compat.Mods;
import com.fungoussoup.ancienthorizons.entity.interfaces.Rutting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.*;

@EventBusSubscriber(modid = AncientHorizons.MOD_ID)
public class SereneSeasonsRut {

    private static final Set<EntityType<?>> RUTTING_ANIMALS = new HashSet<>();

    public static void registerRuttingAnimal(EntityType<?> type) {
        RUTTING_ANIMALS.add(type);
    }

    public static void registerRuttingAnimal(String modId, String entityName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entityName);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
        RUTTING_ANIMALS.add(type);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!(animal instanceof Rutting rutting)) return;
        if (!RUTTING_ANIMALS.contains(animal.getType())) return;

        animal.goalSelector.addGoal(2, new RuttingBehaviorGoal(animal, rutting));
        animal.goalSelector.addGoal(3, new RuttingAggressionGoal(animal, rutting));
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!(animal instanceof Rutting rutting)) return;
        if (!RUTTING_ANIMALS.contains(animal.getType())) return;
        if (animal.level().isClientSide) return;

        boolean shouldBeInRut = isAutumn(animal.level());
        boolean isInRut = rutting.ancient_Horizons$isInRut();

        if (shouldBeInRut && !isInRut) {
            rutting.ancient_Horizons$setInRut(true);
            rutting.ancient_Horizons$setRutIntensity(20);
            applyRuttingAttributeModifiers(animal);
            animal.playSound(SoundEvents.GENERIC_HURT, 1.0f, 0.8f);
        } else if (!shouldBeInRut && isInRut) {
            rutting.ancient_Horizons$setInRut(false);
            rutting.ancient_Horizons$setRutIntensity(0);
            removeRuttingAttributeModifiers(animal);
        }

        if (rutting.ancient_Horizons$isInRut()) {
            updateRutIntensity(animal, rutting);
            applyRuttingEffects(animal, rutting);
        }
    }

    private static boolean isAutumn(Level level) {
        if (!Mods.SERENE_SEASONS.isLoaded()) return false;
        return Mods.SERENE_SEASONS.runIfInstalled(() -> () -> {
            Season season = SeasonHelper.getSeasonState(level).getSeason();
            return season == Season.AUTUMN || season.toString().equalsIgnoreCase("FALL");
        }).orElse(false);
    }

    private static void updateRutIntensity(Animal animal, Rutting rutting) {
        int currentIntensity = rutting.ancient_Horizons$getRutIntensity();

        List<Animal> nearby = animal.level().getEntitiesOfClass(Animal.class,
                new AABB(animal.blockPosition()).inflate(16),
                other -> other != animal && other.getType() == animal.getType());

        if (!nearby.isEmpty()) {
            currentIntensity = Math.min(100, currentIntensity + 2);
        } else {
            currentIntensity = Math.max(0, currentIntensity - 1);
        }

        rutting.ancient_Horizons$setRutIntensity(currentIntensity);
        updateRuttingAttributeModifiers(animal, currentIntensity);
    }

    private static void applyRuttingEffects(Animal animal, Rutting rutting) {
        int intensity = rutting.ancient_Horizons$getRutIntensity();

        if (animal.tickCount % (200 - intensity) == 0 && intensity > 30) {
            animal.playSound(SoundEvents.GENERIC_HURT, 1.0f, 0.6f + (intensity / 200.0f));
        }

        if (intensity > 50 && animal.getRandom().nextInt(100) < intensity) {
            animal.setTarget(null);
        }
    }

    private static void applyRuttingAttributeModifiers(Mob mob) {
        String modId = AncientHorizons.MOD_ID;

        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.addTransientModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(modId, "rut_speed_modifier"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        AttributeInstance attack = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) attack.addTransientModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(modId, "rut_damage_modifier"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.addTransientModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(modId, "rut_hp_modifier"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void updateRuttingAttributeModifiers(Mob mob, int intensity) {
        double scale = intensity / 100.0;
        String modId = AncientHorizons.MOD_ID;

        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(ResourceLocation.fromNamespaceAndPath(modId, "rut_speed_modifier"));
            speed.addTransientModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(modId, "rut_speed_modifier"), 0.3 * scale, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        AttributeInstance attack = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            attack.removeModifier(ResourceLocation.fromNamespaceAndPath(modId, "rut_damage_modifier"));
            attack.addTransientModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(modId, "rut_damage_modifier"), 0.5 * scale, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void removeRuttingAttributeModifiers(Mob mob) {
        String modId = AncientHorizons.MOD_ID;

        Objects.requireNonNull(mob.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(ResourceLocation.fromNamespaceAndPath(modId, "rut_speed_modifier"));
        Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).removeModifier(ResourceLocation.fromNamespaceAndPath(modId, "rut_damage_modifier"));
        Objects.requireNonNull(mob.getAttribute(Attributes.MAX_HEALTH)).removeModifier(ResourceLocation.fromNamespaceAndPath(modId, "rut_hp_modifier"));
    }

    private static class RuttingBehaviorGoal extends Goal {
        private final Mob mob;
        private final Rutting rutting;
        private Animal target;
        private int cooldown;

        public RuttingBehaviorGoal(Mob mob, Rutting rutting) {
            this.mob = mob;
            this.rutting = rutting;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!rutting.ancient_Horizons$isInRut() || cooldown > 0) return false;

            List<Animal> nearby = mob.level().getEntitiesOfClass(Animal.class,
                    new AABB(mob.blockPosition()).inflate(16),
                    other -> other != mob && other.getType() == mob.getType());

            if (!nearby.isEmpty()) {
                target = nearby.get(mob.getRandom().nextInt(nearby.size()));
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() &&
                    mob.distanceToSqr(target) < 256 && rutting.ancient_Horizons$isInRut();
        }

        @Override
        public void start() {
            cooldown = 200 + mob.getRandom().nextInt(200);
        }

        @Override
        public void stop() {
            target = null;
            cooldown = 100;
        }

        @Override
        public void tick() {
            if (target != null) {
                mob.getLookControl().setLookAt(target, 10.0f, 10.0f);
                if (mob.distanceToSqr(target) > 4.0) {
                    mob.getNavigation().moveTo(target, 1.2);
                } else if (mob.getRandom().nextInt(20) == 0) {
                    mob.playSound(SoundEvents.GENERIC_HURT, 1.0f, 0.7f);
                }
            }

            if (cooldown > 0) cooldown--;
        }
    }

    private static class RuttingAggressionGoal extends Goal {
        private final Mob mob;
        private final Rutting rutting;
        private LivingEntity target;

        public RuttingAggressionGoal(Mob mob, Rutting rutting) {
            this.mob = mob;
            this.rutting = rutting;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!rutting.ancient_Horizons$isInRut() || rutting.ancient_Horizons$getRutIntensity() < 60) return false;

            List<LivingEntity> nearby = mob.level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(mob.blockPosition()).inflate(8),
                    entity -> entity != mob && entity instanceof Mob && entity.getType() == mob.getType());

            if (!nearby.isEmpty()) {
                target = nearby.get(mob.getRandom().nextInt(nearby.size()));
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() &&
                    mob.distanceToSqr(target) < 64 && rutting.ancient_Horizons$isInRut();
        }

        @Override
        public void start() {
            mob.setTarget(target);
        }

        @Override
        public void stop() {
            target = null;
            mob.setTarget(null);
        }
    }
}
