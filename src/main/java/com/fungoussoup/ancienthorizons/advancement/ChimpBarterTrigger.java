package com.fungoussoup.ancienthorizons.advancement;

import com.fungoussoup.ancienthorizons.registry.ModAdvancements;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ChimpBarterTrigger extends SimpleCriterionTrigger<ChimpBarterTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return ChimpBarterTrigger.TriggerInstance.CODEC;
    }

    // Simple trigger - just tracks that bartering happened
    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> instance.test(ItemStack.EMPTY));
    }

    // Advanced trigger - tracks what item was received
    public void trigger(ServerPlayer player, ItemStack receivedItem) {
        this.trigger(player, (instance) -> instance.test(receivedItem));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<ItemPredicate> receivedItem
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((builder) ->
                builder.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("received_item").forGetter(TriggerInstance::receivedItem)
                ).apply(builder, TriggerInstance::new)
        );

        // Simple criterion - any barter
        public static Criterion<TriggerInstance> simple() {
            return ModAdvancements.BARTER_WITH_CHIMP.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        // Advanced criterion - specific item received
        public static Criterion<TriggerInstance> receivedItem(ItemPredicate itemPredicate) {
            return ModAdvancements.BARTER_WITH_CHIMP.get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(itemPredicate)));
        }

        // Test the trigger conditions
        public boolean test(ItemStack receivedItem) {
            // If no item predicate specified, always pass
            return this.receivedItem.map(itemPredicate -> !receivedItem.isEmpty() && itemPredicate.test(receivedItem)).orElse(true);

            // If item predicate specified, test against received item
        }
    }
}