package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.util.ZebraVariant;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Objects;

public class ZebraEntity extends AbstractHorse {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(ZebraEntity.class, EntityDataSerializers.INT);

    public ZebraEntity(EntityType<? extends ZebraEntity> type, Level level) {
        super(type, level);
    }

    protected void randomizeAttributes(RandomSource random) {
        AttributeInstance var10000 = this.getAttribute(Attributes.MAX_HEALTH);
        Objects.requireNonNull(random);
        var10000.setBaseValue(generateMaxHealth(random::nextInt));
        var10000 = this.getAttribute(Attributes.JUMP_STRENGTH);
        Objects.requireNonNull(random);
        var10000.setBaseValue(generateJumpStrength(random::nextDouble));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean flag = !this.isBaby() && this.isTamed() && player.isSecondaryUseActive();
        if (!this.isVehicle() && !flag) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (!itemstack.isEmpty()) {
                if (this.isFood(itemstack)) {
                    return this.fedFood(player, itemstack);
                }

                if (!this.isTamed()) {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void containerChanged(Container invBasic) {
        ItemStack oldArmor = this.getBodyArmorItem();
        super.containerChanged(invBasic);
        ItemStack newArmor = this.getBodyArmorItem();

        if (this.tickCount > 20 && isBodyArmorItem(newArmor) && !ItemStack.isSameItem(oldArmor, newArmor)) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) return false;
        return (otherAnimal instanceof Donkey || otherAnimal instanceof Horse || otherAnimal instanceof ZebraEntity)
                && this.canParent();
    }

    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ZEBRA_AMBIENT;
    }

    protected SoundEvent getAngrySound() {
        return ModSoundEvents.ZEBRA_ANGRY;
    }

    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ZEBRA_DEATH;
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.ZEBRA_HURT;
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
    }

    @Override
    public boolean isWearingBodyArmor() {
        return super.isWearingBodyArmor();
    }

    public boolean canUseSlot(EquipmentSlot slot) {
        return true;
    }

    public boolean isBodyArmorItem(ItemStack stack) {
        Item var3 = stack.getItem();
        if (var3 instanceof AnimalArmorItem animalarmoritem) {
            if (animalarmoritem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (otherParent instanceof Donkey donkey) {
            var zonkey = ModEntities.ZONKEY.get().create(level);
            if (zonkey != null) this.setOffspringAttributes(donkey, zonkey);
            return zonkey;
        } else if (otherParent instanceof Horse horse) {
            var zorse = ModEntities.ZORSE.get().create(level);
            if (zorse != null) this.setOffspringAttributes(horse, zorse);
            return zorse;
        } else if (otherParent instanceof ZebraEntity zebra) {
            var zebraChild = ModEntities.ZEBRA.get().create(level);
            if (zebraChild != null) {
                this.setOffspringAttributes(zebra, zebraChild);
                zebraChild.setVariant(getRandomVariant(random)); // Randomize child variant
            }
            return zebraChild;
        }

        return null;
    }

    private ZebraVariant getRandomVariant(RandomSource random) {
        int roll = random.nextInt(1000);
        if (roll <= 5) return ZebraVariant.ZEBRA_POLKADOT;
        if (roll <= 10) return ZebraVariant.ZEBRA_BLONDE;
        return ZebraVariant.ZEBRA_REGULAR;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (level instanceof ServerLevel serverLevel) {
            setVariant(getRandomVariant(serverLevel.getRandom()));
        } else {
            setVariant(getRandomVariant(RandomSource.create()));
        }

        this.randomizeAttributes(level.getRandom());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public void setVariant(ZebraVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public ZebraVariant getVariant() {
        return ZebraVariant.byID(this.getTypeVariant() & 255);
    }
}
