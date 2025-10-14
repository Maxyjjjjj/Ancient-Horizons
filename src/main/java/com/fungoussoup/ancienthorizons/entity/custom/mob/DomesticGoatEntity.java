package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.Rutting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class DomesticGoatEntity extends Animal implements Rutting {
    // Data parameters for syncing with client
    private static final EntityDataAccessor<Boolean> IS_MALE = SynchedEntityData.defineId(DomesticGoatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GOAT_IN_RUT = SynchedEntityData.defineId(DomesticGoatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RUT_INTENSITY = SynchedEntityData.defineId(DomesticGoatEntity.class, EntityDataSerializers.INT);

    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;

    public DomesticGoatEntity(EntityType<? extends DomesticGoatEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_MALE, false);
        builder.define(GOAT_IN_RUT, false);
        builder.define(RUT_INTENSITY, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(ItemTags.GOAT_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.SAFE_FALL_DISTANCE, 10.0D);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        // Set random gender (30% chance for male)
        this.setMale(this.random.nextFloat() < 0.3f);

        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.GOAT_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        DomesticGoatEntity baby = ModEntities.DOMESTIC_GOAT.get().create(level);
        if (baby != null) {
            // Inherit traits from parents
            if (otherParent instanceof DomesticGoatEntity) {
                // Random gender
                baby.setMale(baby.getRandom().nextBoolean());
            }
        }
        return baby;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Milking
        if (itemstack.is(Items.BUCKET) && !this.isBaby() && !this.isMale() && this.canBeMilked()) {
            player.playSound(SoundEvents.GOAT_MILK, 1.0F, 1.0F);
            ItemStack milkBucket = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, milkBucket);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Feeding
        if (this.isFood(itemstack)) {
            if (!this.level().isClientSide) {
                this.usePlayerItem(player, hand, itemstack);
                this.heal(2.0F);

                // Play eating sound
                this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    public boolean canBeMilked() {
        return !this.isMale() && !this.isBaby();
    }

    @Override
    public boolean ancient_Horizons$isInRut() {
        return false;
    }

    @Override
    public void ancient_Horizons$setInRut(boolean rutting) {

    }

    @Override
    public int ancient_Horizons$getRutIntensity() {
        return 0;
    }

    @Override
    public void ancient_Horizons$setRutIntensity(int value) {

    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.eatAnimationTick > 0) {
            this.eatAnimationTick--;
        }
    }

    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    public float getHeadEatPositionScale(float partialTick) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - partialTick) / 4.0F : -((float)(this.eatAnimationTick - 40) - partialTick) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float partialTick) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((float)(this.eatAnimationTick - 4) - partialTick) / 32.0F;
            return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
        }
    }

    // Getters and setters for data parameters
    public boolean isMale() {
        return this.entityData.get(IS_MALE);
    }

    public void setMale(boolean male) {
        this.entityData.set(IS_MALE, male);
    }

    public boolean isBuck() {
        return this.isMale();
    }

    public boolean isDoe() {
        return !this.isMale();
    }

    public int getEatAnimationTick() {
        return eatAnimationTick;
    }

    public void setEatAnimationTick(int eatAnimationTick) {
        this.eatAnimationTick = eatAnimationTick;
    }

    // NBT data saving/loading
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsMale", this.isMale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setMale(tag.getBoolean("IsMale"));
    }

    // Sound methods
    public SoundEvent getEatingSound(ItemStack stack) {
        return SoundEvents.GOAT_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GOAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GOAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GOAT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }
}