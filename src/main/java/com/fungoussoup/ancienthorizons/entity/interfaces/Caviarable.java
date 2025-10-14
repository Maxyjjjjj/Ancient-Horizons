package com.fungoussoup.ancienthorizons.entity.interfaces;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

public interface Caviarable {
    /**
     * Checks if this entity can currently produce caviar
     * @return true if caviar can be harvested, false otherwise
     */
    boolean canProduceCaviar();

    /**
     * Gets the caviar item that this entity produces
     *
     * @return ItemStack of the caviar item
     */
    Holder<Item> getCaviarItem();

    /**
     * Gets the quantity of caviar that can be harvested
     * @return int number of caviar items to drop
     */
    int getCaviarQuantity();

    /**
     * Called when caviar is harvested from this entity
     * @param player The player harvesting the caviar
     * @return true if harvesting was successful
     */
    boolean harvestCaviar(Player player);

    /**
     * Gets the cooldown time before caviar can be harvested again (in ticks)
     * @return int cooldown duration, -1 if single harvest only
     */
    int getCaviarCooldown();

    /**
     * Gets the minimum age/maturity required to produce caviar
     * @return int minimum age in ticks
     */
    int getMinimumMaturityAge();

    /**
     * Checks if this entity dies when caviar is harvested
     * @return true if harvesting kills the entity, false if renewable
     */
    boolean diesOnHarvest();

    /**
     * Gets the tool required to harvest caviar (if any)
     * @return ItemStack of required tool, or ItemStack.EMPTY if no tool needed
     */
    default ItemStack getRequiredHarvestTool() {
        return ItemStack.EMPTY;
    }

    /**
     * Gets the chance of successful caviar harvest (0.0 to 1.0)
     * @return float success probability
     */
    default float getHarvestSuccessChance() {
        return 1.0f;
    }
}
