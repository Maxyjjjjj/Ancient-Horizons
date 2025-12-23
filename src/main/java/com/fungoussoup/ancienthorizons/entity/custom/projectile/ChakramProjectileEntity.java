package com.fungoussoup.ancienthorizons.entity.custom.projectile;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ChakramProjectileEntity extends ThrowableItemProjectile {

    private Player thrower;

    public ChakramProjectileEntity(EntityType<? extends ChakramProjectileEntity> type, Level level) {
        super(type, level);
    }

    public ChakramProjectileEntity(Level level, Player player) {
        super(ModEntities.CHAKRAM.get(), player, level); // assumes ModEntities has a RegistryObject<EntityType<ChakramProjectileEntity>>
        this.thrower = player;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.CHAKRAM.get(); // assumes you registered ChakramItem
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!this.level().isClientSide) {
            if (result.getEntity() instanceof LivingEntity target && thrower != null) {
                target.hurt(this.damageSources().playerAttack(thrower), 6.0F + (float) this.getDeltaMovement().length());
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (thrower != null) {
                // return to thrower’s inventory (like a boomerang)
                if (!thrower.getInventory().add(new ItemStack(ModItems.CHAKRAM.get()))) {
                    thrower.drop(new ItemStack(ModItems.CHAKRAM.get()), false);
                }
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 40 && !this.level().isClientSide && thrower != null) {
            // auto-return after 2 seconds if no hit
            if (!thrower.getInventory().add(new ItemStack(ModItems.CHAKRAM.get()))) {
                thrower.drop(new ItemStack(ModItems.CHAKRAM.get()), false);
            }
            this.discard();
        }
    }
}

