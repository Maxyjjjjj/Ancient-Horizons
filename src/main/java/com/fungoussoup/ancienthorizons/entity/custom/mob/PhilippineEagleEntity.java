package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PhilippineEagleEntity extends AbstractEagleEntity {

    public PhilippineEagleEntity(EntityType<? extends AbstractEagleEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractEagleEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.FLYING_SPEED, 0.8)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 1.5)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    public boolean isValidPrey(LivingEntity entity) {
        return entity.getType().is(ModTags.EntityTypes.PHILIPPINE_EAGLE_PREY);
    }

    @Override
    public int getSwoopCooldownTicks() {
        return 120; // 6 seconds
    }

    @Override
    public double getSwoopSpeed() {
        return this.getAttributeValue(Attributes.FLYING_SPEED);
    }

    @Override
    protected boolean canCarryPrey(LivingEntity prey) {
        return prey.isAlive() && !prey.isRemoved() && isValidPrey(prey);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (!this.level().isClientSide) {
            Entity attacker = source.getEntity();
            if (attacker instanceof Player player) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.BAD_OMEN,
                        12000, 0, false, true, true
                ));
                if (player instanceof ServerPlayer sp) {
                    sp.displayClientMessage(Component.translatable("message.ancienthorizons.bad_omen_eagle"), true);
                }
                this.level().playSound(attacker, this.blockPosition(), SoundEvents.APPLY_EFFECT_RAID_OMEN, SoundSource.PLAYERS, 1.5F, 1.0F);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player) return;
        super.dropCustomDeathLoot(level, source, recentlyHit);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        PhilippineEagleEntity baby = ModEntities.PHILIPPINE_EAGLE.get().create(level);
        if (baby != null) {
            UUID owner = this.getOwnerUUID();
            if (owner != null) {
                baby.setOwnerUUID(owner);
                baby.setTame(true, true);
            }
        }
        return baby;
    }
}