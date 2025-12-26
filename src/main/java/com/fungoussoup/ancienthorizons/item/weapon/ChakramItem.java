package com.fungoussoup.ancienthorizons.item.weapon;

import com.fungoussoup.ancienthorizons.entity.custom.projectile.ChakramProjectileEntity;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChakramItem extends Item {

    public ChakramItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ChakramProjectileEntity projectile = new ChakramProjectileEntity(level, player);
            projectile.setItem(stack.copy());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.8F, 1.0F);

            level.addFreshEntity(projectile);
            level.playSound((Entity) null, player.blockPosition(), ModSoundEvents.CHAKRAM_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}
