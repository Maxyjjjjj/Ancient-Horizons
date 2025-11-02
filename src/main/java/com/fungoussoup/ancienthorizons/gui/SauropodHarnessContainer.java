package com.fungoussoup.ancienthorizons.gui;

import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractSauropodEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SauropodHarnessContainer extends AbstractContainerMenu implements MenuProvider {
    public static final MenuType<SauropodHarnessContainer> MENU_TYPE = null; // Register this in your mod setup

    private final AbstractSauropodEntity sauropod;
    private final Container inventory;
    private final Container cargoInventory;
    private final Inventory playerInventory;

    // Slot indices
    private final int inventoryStartIndex;
    private final int cargoStartIndex;
    private final int playerInventoryStartIndex;
    private final int playerHotbarStartIndex;

    public SauropodHarnessContainer(AbstractSauropodEntity sauropod,
                                    @Nullable Container inventory,
                                    @Nullable Container cargoInventory,
                                    Inventory playerInventory) {
        super(MENU_TYPE, 0);
        this.sauropod = sauropod;
        this.inventory = inventory != null ? inventory : new SimpleContainer(0);
        this.cargoInventory = cargoInventory != null ? cargoInventory : new SimpleContainer(0);
        this.playerInventory = playerInventory;

        int slotIndex = 0;

        // Add sauropod inventory slots (equipment, tools, etc.)
        this.inventoryStartIndex = slotIndex;
        if (inventory != null) {
            int rows = (inventory.getContainerSize() + 8) / 9; // Calculate rows needed
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < 9; col++) {
                    int index = row * 9 + col;
                    if (index < inventory.getContainerSize()) {
                        this.addSlot(new Slot(inventory, index, 8 + col * 18, 18 + row * 18));
                        slotIndex++;
                    }
                }
            }
        }

        // Add cargo inventory slots (bulk storage)
        this.cargoStartIndex = slotIndex;
        if (cargoInventory != null) {
            int rows = (cargoInventory.getContainerSize() + 8) / 9;
            int startY = inventory != null ? 18 + ((inventory.getContainerSize() + 8) / 9) * 18 + 10 : 18;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < 9; col++) {
                    int index = row * 9 + col;
                    if (index < cargoInventory.getContainerSize()) {
                        this.addSlot(new CargoSlot(cargoInventory, index, 8 + col * 18, startY + row * 18));
                        slotIndex++;
                    }
                }
            }
        }

        // Add player inventory
        int playerInventoryY = calculatePlayerInventoryY();
        this.playerInventoryStartIndex = slotIndex;

        // Player main inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInventoryY + row * 18));
                slotIndex++;
            }
        }

        // Player hotbar
        this.playerHotbarStartIndex = slotIndex;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, playerInventoryY + 58));
            slotIndex++;
        }
    }

    // Constructor for network synchronization
    public SauropodHarnessContainer(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(getSauropodFromBuf(playerInventory, extraData), null, null, playerInventory);
    }

    private static AbstractSauropodEntity getSauropodFromBuf(Inventory playerInventory, FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        if (playerInventory.player.level().getEntity(entityId) instanceof AbstractSauropodEntity sauropod) {
            return sauropod;
        }
        throw new IllegalStateException("Invalid sauropod entity ID: " + entityId);
    }

    private int calculatePlayerInventoryY() {
        int baseY = 18;
        if (inventory != null) {
            baseY += ((inventory.getContainerSize() + 8) / 9) * 18 + 10;
        }
        if (cargoInventory != null) {
            baseY += ((cargoInventory.getContainerSize() + 8) / 9) * 18 + 10;
        }
        return baseY + 10; // Extra spacing
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            // From sauropod inventories to player inventory
            if (slotIndex < playerInventoryStartIndex) {
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStartIndex, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From player inventory to sauropod inventories
            else {
                // Try cargo first, then regular inventory
                boolean moved = false;
                if (cargoInventory.getContainerSize() > 0) {
                    moved = this.moveItemStackTo(stackInSlot, cargoStartIndex, cargoStartIndex + cargoInventory.getContainerSize(), false);
                }
                if (!moved && inventory.getContainerSize() > 0) {
                    moved = this.moveItemStackTo(stackInSlot, inventoryStartIndex, inventoryStartIndex + inventory.getContainerSize(), false);
                }
                if (!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return sauropod.isAlive() &&
                sauropod.distanceToSqr(player) < 64.0 &&
                sauropod.isOwnedBy(player) &&
                sauropod.isHarnessed();
    }

    public AbstractSauropodEntity getSauropod() {
        return sauropod;
    }

    public Container getInventory() {
        return inventory;
    }

    public Container getCargoInventory() {
        return cargoInventory;
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.ancienthorizons.sauropod_harness", sauropod.getName());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SauropodHarnessContainer(sauropod, inventory, cargoInventory, playerInventory);
    }

    // Custom slot for cargo that might have restrictions
    public static class CargoSlot extends Slot {
        public CargoSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Add restrictions for cargo slots if needed
            // For example, only allow certain item types
            return super.mayPlace(stack);
        }

        @Override
        public int getMaxStackSize() {
            // Cargo slots might allow larger stacks
            return 64;
        }
    }
}
