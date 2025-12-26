package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.FishAnimal;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.Caviarable;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BelugaSturgeonEntity extends FishAnimal implements Caviarable, Bucketable {

    private static final EntityDataAccessor<Integer> CAVIAR_COOLDOWN =
            SynchedEntityData.defineId(BelugaSturgeonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET =
            SynchedEntityData.defineId(BelugaSturgeonEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int MAX_CAVIAR_COOLDOWN = 6000; // 5 minutes
    private static final int MIN_BREEDING_AGE = 24000; // 20 minutes

    public BelugaSturgeonEntity(EntityType<? extends FishAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAVIAR_COOLDOWN, 0);
        builder.define(FROM_BUCKET, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, Ingredient.of(ModItems.WORM.get()), false));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.6, 1.4,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, PolarBear.class, 8.0F, 1.6, 1.4));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(NeoForgeMod.SWIM_SPEED, 0.3);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.BELUGA_STURGEON.get().create(level);
    }

    @Override
    public boolean canMate(Animal other) {
        if (!(other instanceof BelugaSturgeonEntity)) {
            return false;
        }

        return this != other
                && this.isInLove()
                && other.isInLove()
                && this.getAge() >= MIN_BREEDING_AGE
                && other.getAge() >= MIN_BREEDING_AGE;
    }

    // ===== Caviarable Implementation =====

    @Override
    public boolean canProduceCaviar() {
        return !this.isBaby()
                && this.getAge() >= MIN_BREEDING_AGE
                && this.getCaviarCooldown() <= 0
                && this.isAlive();
    }

    @Override
    public DeferredItem<Item> getCaviarItem() {
        return ModItems.BELUGA_STURGEON_CAVIAR;
    }

    @Override
    public int getCaviarQuantity() {
        return 4;
    }

    @Override
    public boolean harvestCaviar(Player player) {
        if (!this.canProduceCaviar()) {
            return false;
        }

        ItemStack caviar = new ItemStack(this.getCaviarItem().value(), this.getCaviarQuantity());
        if (!player.addItem(caviar)) {
            player.drop(caviar, false);
        }

        this.onCaviarHarvested();
        return true;
    }

    protected void onCaviarHarvested() {
        this.setCaviarCooldown(MAX_CAVIAR_COOLDOWN);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F,
                (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int getCaviarCooldown() {
        return this.entityData.get(CAVIAR_COOLDOWN);
    }

    public void setCaviarCooldown(int cooldown) {
        this.entityData.set(CAVIAR_COOLDOWN, Math.max(0, cooldown));
    }

    @Override
    public int getMinimumMaturityAge() {
        return MIN_BREEDING_AGE;
    }

    @Override
    public boolean diesOnHarvest() {
        return false;
    }

    // ===== Bucketable Implementation =====

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public void saveToBucketTag(@NotNull ItemStack bucket) {
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, tag -> {
            tag.putInt("CaviarCooldown", this.getCaviarCooldown());
            tag.putInt("Age", this.getAge());
        });
    }

    @Override
    public void loadFromBucketTag(@NotNull CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        if (tag.contains("CaviarCooldown")) {
            this.setCaviarCooldown(tag.getInt("CaviarCooldown"));
        }
        if (tag.contains("Age")) {
            this.setAge(tag.getInt("Age"));
        }
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.BELUGA_STURGEON_BUCKET.get());
    }

    @Override
    public @NotNull SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    // ===== Sounds =====

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    // ===== NBT Persistence =====

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CaviarCooldown", this.getCaviarCooldown());
        tag.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setCaviarCooldown(tag.getInt("CaviarCooldown"));
        this.setFromBucket(tag.getBoolean("FromBucket"));
    }

    // ===== Visual Behavior =====

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public float getScale() {
        if (this.isBaby()) {
            return 0.5F;
        }
        // Grow up to 50% larger as they age (maxes out at 40 minutes of age)
        return 1.0F + Math.min(this.getAge() / 48000.0F, 0.5F);
    }
}