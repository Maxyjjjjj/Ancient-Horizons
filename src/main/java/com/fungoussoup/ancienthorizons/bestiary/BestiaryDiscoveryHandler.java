package com.fungoussoup.ancienthorizons.bestiary;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.network.BestiaryNetworking;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

/**
 * Handles automatic discovery of bestiary entries
 */
@EventBusSubscriber(modid = AncientHorizons.MOD_ID)
public class BestiaryDiscoveryHandler {

    // Entities that can be discovered by killing (food animals)
    private static final Set<Object> KILLABLE_FOR_DISCOVERY = Set.of(
            EntityType.COW,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.RABBIT,
            EntityType.SHEEP,
            EntityType.GOAT,
            ModEntities.PHEASANT,
            ModEntities.DOMESTIC_GOAT
    );

    /**
     * Discovers entry when player kills certain food animals
     */
    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity entity = event.getEntity();

            // Only allow kill discovery for specific food animals
            if (KILLABLE_FOR_DISCOVERY.contains(entity.getType())) {
                discoverEntity(player, entity, "killed");
            } else {
                // Check if it's a modded killable entity
                ResourceLocation entityId = getEntityResourceLocation(entity);
                if (entityId.getNamespace().equals(AncientHorizons.MOD_ID) &&
                        isModdedKillableEntity(entityId)) {
                    discoverEntity(player, entity, "killed");
                }
            }
        }
    }

    /**
     * Discovers entry when player interacts with an entity (right-click)
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Entity target = event.getTarget();
            if (target instanceof LivingEntity living) {
                discoverEntity(player, living, "interacted");
            }
        }
    }

    /**
     * Discovers entry when player feeds an entity
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Entity target = event.getTarget();
            if (target instanceof Animal animal) {
                // Check if player is holding food the animal wants
                if (animal.isFood(event.getItemStack())) {
                    discoverEntity(player, animal, "fed");
                }
            }
        }
    }

    /**
     * Discovers entry when player breeds animals
     */
    @SubscribeEvent
    public static void onAnimalBreed(BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() instanceof ServerPlayer player) {
            // Discover parent A
            discoverEntity(player, event.getParentA(), "bred");

            // Discover parent B
            discoverEntity(player, event.getParentB(), "bred");

            // Discover the baby
            if (event.getChild() != null) {
                discoverEntity(player, event.getChild(), "bred");
            }
        }
    }

    /**
     * Core discovery logic
     */
    private static void discoverEntity(ServerPlayer player, LivingEntity entity, String method) {
        ResourceLocation entityType = getEntityResourceLocation(entity);

        ServerLevel level = player.serverLevel();
        BestiaryData data = BestiaryData.get(level);

        boolean wasNew = data.discover(entityType);

        if (wasNew) {
            // Get entry name for notification
            BestiaryEntry entry = BestiaryManager.getEntry(entityType);
            String name = entry != null ? entry.getName() : entityType.getPath();

            // Send discovery notification to player
            BestiaryNetworking.notifyDiscovery(player, entityType, name);

            AncientHorizons.LOGGER.info("Player {} discovered {} by {}",
                    player.getName().getString(), entityType, method);
        }
    }

    /**
     * Gets the resource location for an entity
     */
    private static ResourceLocation getEntityResourceLocation(Entity entity) {
        return entity.getType().builtInRegistryHolder().key().location();
    }

    /**
     * Checks if a modded entity can be discovered by killing
     */
    private static boolean isModdedKillableEntity(ResourceLocation entityId) {
        String path = entityId.getPath();
        // Add modded food animals here
        return path.equals("pheasant") || path.equals("domestic_goat");
    }
}