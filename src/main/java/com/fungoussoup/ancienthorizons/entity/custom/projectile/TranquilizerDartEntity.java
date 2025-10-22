package com.fungoussoup.ancienthorizons.entity.custom.projectile;

import com.fungoussoup.ancienthorizons.registry.ModEffects;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class TranquilizerDartEntity extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> CRITICAL = SynchedEntityData.defineId(TranquilizerDartEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FEATHER_COLOR = SynchedEntityData.defineId(TranquilizerDartEntity.class, EntityDataSerializers.INT);

    public TranquilizerDartEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            DyeColor[] colors = DyeColor.values();
            DyeColor randomColor = colors[level.getRandom().nextInt(colors.length)];
            this.setColor(randomColor);
        }
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CRITICAL, false);
        builder.define(FEATHER_COLOR, DyeColor.RED.getId());
    }

    @Override
    protected ItemStack getPickupItem() {
        return getDefaultPickupItem();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModItems.TRANQ_DART.toStack();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.TRANQUILIZED, 400, 0)); // 20 seconds
        }

        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 1.2F);
            this.discard(); // Remove dart after impact
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(FEATHER_COLOR));
    }

    public void setColor(DyeColor color) {
        this.entityData.set(FEATHER_COLOR, color.getId());
    }
}

