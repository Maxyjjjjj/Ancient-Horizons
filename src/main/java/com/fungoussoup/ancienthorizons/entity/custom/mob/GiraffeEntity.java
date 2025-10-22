package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GiraffeEntity extends Animal implements PlayerRideable {
    private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(GiraffeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SNEEZING = SynchedEntityData.defineId(GiraffeEntity.class, EntityDataSerializers.BOOLEAN);

    public GiraffeEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOOST_TIME, 0);
        builder.define(SNEEZING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BoostTime", this.getBoostTime());
        compound.putBoolean("Sneezing", this.isSneezing());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBoostTime(compound.getInt("BoostTime"));
        this.setSneezing(compound.getBoolean("Sneezing"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.LEAVES);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.GIRAFFE.get().create(serverLevel);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        // Base on the entity's current bounding-box height so it scales if you tweak dimensions elsewhere
        float baseHeight = dimensions.height();

        if (this.isBaby()) {
            return baseHeight * 0.6F;
        }

        float eye = baseHeight * 0.85F;

        if (this.isSneezing()) {
            eye += 0.4F;
        }

        return eye;
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.isBaby()) {
            if (!this.isVehicle() && !player.isSecondaryUseActive() && itemstack.isEmpty()) {
                if (!this.level().isClientSide) {
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if (this.isFood(itemstack)) {
            return super.mobInteract(player, hand);
        }

        return InteractionResult.PASS;
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(0.0D, 0.0D, 1.0D);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.5D);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isVehicle() ? ModSoundEvents.GIRAFFE_SNORT : ModSoundEvents.GIRAFFE_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.GIRAFFE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GIRAFFE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.HORSE_STEP, 0.15F, 1.0F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 300;
    }

    @Override
    public void tick() {
        super.tick();

        // Handle neck reaching high places (giraffe characteristic)
        // Adjust threshold as needed
        this.setSneezing(this.getY() > 150);

        // Handle boost time for riding
        if (this.getBoostTime() > 0) {
            this.setBoostTime(this.getBoostTime() - 1);
        }
    }

    public int getBoostTime() {
        return this.entityData.get(BOOST_TIME);
    }

    public void setBoostTime(int boostTime) {
        this.entityData.set(BOOST_TIME, boostTime);
    }

    public boolean isSneezing() {
        return this.entityData.get(SNEEZING);
    }

    public void setSneezing(boolean sneezing) {
        this.entityData.set(SNEEZING, sneezing);
    }
}