package com.fungoussoup.ancienthorizons.item;

import com.fungoussoup.ancienthorizons.entity.custom.projectile.TranquilizerDartEntity;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DartGunItem extends Item {

    private static final String TAG_CHARGED = "Charged";
    private static final int LOAD_TIME = 20; // Time in ticks to load the gun (20 ticks = 1 second)

    // This predicate is correctly defined and will be used by findAmmo
    public static final Predicate<ItemStack> AMMUNITION = (stack) -> stack.is(ModItems.TRANQ_DART.get());

    public DartGunItem(Properties properties) {
        super(properties);
    }

    // --- STATE MANAGEMENT HELPERS ---

    /**
     * Checks if the gun's NBT data says it's charged.
     */
    public static boolean isCharged(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;
        CompoundTag tag = customData.copyTag();
        return tag.getBoolean(TAG_CHARGED);
    }

    /**
     * Sets the gun's NBT data to charged or not charged.
     */
    public static void setCharged(ItemStack stack, boolean charged) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putBoolean(TAG_CHARGED, charged);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    // --- MAIN ITEM LOGIC ---

    /**
     * This is the main interaction method. Its behavior depends on the gun's state.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // If the gun is already charged, FIRE it instantly.
        if (isCharged(stack)) {
            fire(level, player, hand, stack);
            return InteractionResultHolder.consume(stack);
        }

        // If the gun is not charged, check for ammo and begin LOADING.
        // For this to work, you MUST uncomment and define the AMMUNITION predicate above. (Done)
        boolean hasAmmo = !findAmmo(player).isEmpty() || player.getAbilities().instabuild;

        if (hasAmmo) {
            player.startUsingItem(hand); // Begins the loading animation.
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack); // Fails if no ammo is found.
    }

    /**
     * This is called when the player finishes the "use" action (i.e., loading).
     */
    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int ticksRemaining) {
        // This method is now only for completing the loading process.
        if (!(entity instanceof Player player)) return;

        // Check if the player held the button long enough to load.
        int ticksUsed = this.getUseDuration(stack) - ticksRemaining;
        if (ticksUsed < LOAD_TIME) return;

        // If loading is successful, consume ammo (if not creative) and set the state to charged.
        if (!level.isClientSide) {
            boolean hasAmmo = !findAmmo(player).isEmpty() || player.getAbilities().instabuild; // <-- UNCOMMENT (Now working with findAmmo implementation)
            if (hasAmmo) { // <-- UNCOMMENT
                if (!player.getAbilities().instabuild) { // <-- UNCOMMENT
                    ItemStack ammo = findAmmo(player); // <-- UNCOMMENT
                    ammo.shrink(1); // <-- UNCOMMENT
                }
                setCharged(stack, true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    /**
     * A helper method to handle the firing logic.
     */
    private void fire(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (level.isClientSide) return;

        // Create and shoot the dart entity with fixed high velocity.
        TranquilizerDartEntity dart = new TranquilizerDartEntity(ModEntities.TRANQ_DART.get(), level);
        dart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.15F, 1.0F);
        level.addFreshEntity(dart);

        // Play firing sound.
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Set the gun back to an uncharged state.
        setCharged(stack, false);

        // Damage the item if not in creative mode.
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 20); // 1-second cooldown after firing.
    }

    /**
     * FIX: Implement the logic to find ammunition in the player's inventory.
     */
    private ItemStack findAmmo(Player player) {
        // 1. Check if the player is holding ammo in the offhand.
        if (AMMUNITION.test(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        }

        // 2. Check the main hand (only if it's NOT the gun itself).
        ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (AMMUNITION.test(mainHandStack) && mainHandStack.getItem() != this) {
            return mainHandStack;
        }

        // 3. Search the player's inventory.
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = player.getInventory().getItem(i);
            if (AMMUNITION.test(itemstack)) {
                return itemstack;
            }
        }

        // 4. Return empty stack if no ammo is found.
        return ItemStack.EMPTY;
    }


    // --- VISUALS AND ANIMATIONS ---

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        // Use the crossbow animation for loading.
        return UseAnim.CROSSBOW;
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        // This is now the time it takes to LOAD the gun, not hold a charge.
        return LOAD_TIME;
    }

    /**
     * Makes the item glow with an enchantment glint when it is charged.
     * This provides a clear visual cue that the gun is ready to fire.
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isCharged(stack);
    }
}