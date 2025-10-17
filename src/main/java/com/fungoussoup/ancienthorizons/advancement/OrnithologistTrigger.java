package com.fungoussoup.ancienthorizons.advancement;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
import com.fungoussoup.ancienthorizons.registry.ModAdvancements;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class OrnithologistTrigger extends SimpleCriterionTrigger<OrnithologistTrigger.TriggerInstance> {

    @Override
    public Codec<OrnithologistTrigger.TriggerInstance> codec() {
        return OrnithologistTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AbstractPasserineEntity bird) {
        this.trigger(player, (instance) -> instance.test(player, bird));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<EntityPredicate> entity
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<OrnithologistTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((builder) ->
                builder.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(OrnithologistTrigger.TriggerInstance::player),
                        EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(OrnithologistTrigger.TriggerInstance::entity)
                ).apply(builder, OrnithologistTrigger.TriggerInstance::new)
        );

        // All required passerine types
        private static final Set<EntityType<?>> REQUIRED_BIRDS = Set.of(
                ModEntities.BLACKCAP.get(),
                ModEntities.BLUETHROAT.get(),
                ModEntities.BULLFINCH.get(),
                ModEntities.CANARY.get(),
                ModEntities.CARDINAL.get(),
                ModEntities.CHAFFINCH.get(),
                ModEntities.GOLDCREST.get(),
                ModEntities.GOLDFINCH.get(),
                ModEntities.NIGHTINGALE.get(),
                ModEntities.REDSTART.get(),
                ModEntities.REEDLING.get(),
                ModEntities.ROBIN.get(),
                ModEntities.SISKIN.get(),
                ModEntities.SKYLARK.get(),
                ModEntities.SPARROW.get(),
                ModEntities.TIT.get(),
                ModEntities.WAGTAIL.get(),
                ModEntities.WAXWING.get()
        );

        // NBT key for persistent player data
        private static final String ROOT_TAG = AncientHorizons.MOD_ID;
        private static final String TAMED_BIRDS_TAG = "TamedBirds";

        public static Criterion<OrnithologistTrigger.TriggerInstance> simple() {
            return ModAdvancements.TAME_ALL_BIRDS.get().createCriterion(
                    new OrnithologistTrigger.TriggerInstance(Optional.empty(), Optional.empty())
            );
        }

        public boolean test(ServerPlayer player, AbstractPasserineEntity tamedBird) {
            if (tamedBird == null) return false;

            EntityType<?> birdType = tamedBird.getType();

            // Check if this is a valid passerine type
            if (!REQUIRED_BIRDS.contains(birdType)) return false;

            // Get the set of tamed bird types for this player
            Set<EntityType<?>> tamedBirdTypes = getOrCreateTamedBirds(player);

            // Add the newly tamed bird type
            tamedBirdTypes.add(birdType);
            saveTamedBirds(player, tamedBirdTypes); // persist

            // Check if all required birds have been tamed
            return tamedBirdTypes.containsAll(REQUIRED_BIRDS);
        }

        /**
         * Gets or creates the set of tamed bird types for a player
         * Stored under "ancienthorizons" -> "TamedBirds"
         */
        private Set<EntityType<?>> getOrCreateTamedBirds(ServerPlayer player) {
            CompoundTag playerData = player.getPersistentData();
            CompoundTag modData = playerData.contains(ROOT_TAG, 10)
                    ? playerData.getCompound(ROOT_TAG)
                    : new CompoundTag();

            Set<EntityType<?>> tamedBirds = new HashSet<>();

            if (modData.contains(TAMED_BIRDS_TAG, 9)) { // ListTag
                ListTag list = modData.getList(TAMED_BIRDS_TAG, 8); // StringTag
                for (int i = 0; i < list.size(); i++) {
                    ResourceLocation rl = ResourceLocation.tryParse(list.getString(i));
                    if (rl != null) {
                        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(rl);
                        tamedBirds.add(type);
                    }
                }
            }

            return tamedBirds;
        }

        /**
         * Saves the tamed bird types to player data under "ancient_horizons" -> "TamedBirds"
         */
        private void saveTamedBirds(ServerPlayer player, Set<EntityType<?>> tamedBirds) {
            ListTag list = new ListTag();
            for (EntityType<?> type : tamedBirds) {
                ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(type);
                list.add(net.minecraft.nbt.StringTag.valueOf(rl.toString()));
            }

            CompoundTag playerData = player.getPersistentData();
            CompoundTag modData = playerData.contains(ROOT_TAG, 10)
                    ? playerData.getCompound(ROOT_TAG)
                    : new CompoundTag();

            modData.put(TAMED_BIRDS_TAG, list);
            playerData.put(ROOT_TAG, modData);
        }
    }
}
