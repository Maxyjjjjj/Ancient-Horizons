package com.fungoussoup.ancienthorizons.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Unique
    private static final EntityDataAccessor<CompoundTag> DATA_HEAD = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
        protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci){
            builder.define(DATA_HEAD, new CompoundTag());
        }
    
    @Unique
    private long ancientHorizons$timeEntitySatOnHead;

    @Unique
    Player ancientHorizons$player;

    @Unique
    public boolean ancientHorizons$setEntityOnHead(CompoundTag entityCompound) {
        if (!ancientHorizons$player.isPassenger() && ancientHorizons$player.onGround() && !ancientHorizons$player.isInWater() && !ancientHorizons$player.isInPowderSnow) {
            if (this.ancientHorizons$getHeadEntity().isEmpty()) {
                this.ancientHorizons$getHeadEntity();
                this.ancientHorizons$timeEntitySatOnHead = ancientHorizons$player.level().getGameTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Unique
    public CompoundTag ancientHorizons$getHeadEntity() {
        return ancientHorizons$player.getEntityData().get(DATA_HEAD);
    }

    @Unique
    protected void ancientHorizons$setHeadEntity(CompoundTag entityCompound) {
        ancientHorizons$player.getEntityData().set(DATA_HEAD, entityCompound);
    }

    @Unique
    protected void ancientHorizons$removeEntitiesOnHead() {
        if (this.ancientHorizons$timeEntitySatOnHead + 20L < ancientHorizons$player.level().getGameTime()) {
            this.ancientHorizons$respawnEntityOnHead(this.ancientHorizons$getHeadEntity());
            this.ancientHorizons$setHeadEntity(new CompoundTag());
        }
    }

    @Unique
    public boolean ancientHorizons$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ancientHorizons$player.isInvulnerableTo(source)) {
            return false;
        } else if (ancientHorizons$player.getAbilities().invulnerable && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            ancientHorizons$player.setNoActionTime(0);
            if (ancientHorizons$player.isDeadOrDying()) {
                return false;
            } else {
                if (!ancientHorizons$player.level().isClientSide) {
                    this.ancientHorizons$removeEntitiesOnHead();
                }

                amount = Math.max(0.0F, source.type().scaling().getScalingFunction().scaleDamage(source, ancientHorizons$player, amount, ancientHorizons$player.level().getDifficulty()));
                return amount != 0.0F && ancientHorizons$player.hurt(source, amount);
            }
        }
    }

    @Unique
    private void ancientHorizons$respawnEntityOnHead(CompoundTag entityCompound) {
        if (!ancientHorizons$player.level().isClientSide && !entityCompound.isEmpty()) {
            EntityType.create(entityCompound, ancientHorizons$player.level()).ifPresent((p_352835_) -> {
                if (p_352835_ instanceof TamableAnimal) {
                    ((TamableAnimal)p_352835_).setOwnerUUID(ancientHorizons$player.getUUID());
                }

                p_352835_.setPos(ancientHorizons$player.getX(), ancientHorizons$player.getY() + (double)0.7F, ancientHorizons$player.getZ());
                ((ServerLevel) ancientHorizons$player.level()).addWithUUID(p_352835_);
            });
        }

    }
}
