package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.ModFollowOwnerGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class DearcEntity extends ShoulderRidingEntity implements SemiFlyer {

    // Data accessor to sync the flying state between server and client
    private static final EntityDataAccessor<Boolean> IS_FLYING =
            SynchedEntityData.defineId(DearcEntity.class, EntityDataSerializers.BOOLEAN);

    private final PathNavigation groundNavigation;
    private final BirdNavigation airNavigation;

    public DearcEntity(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveControl(this);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);

        this.groundNavigation = this.getNavigation();
        this.airNavigation = new BirdNavigation(this, level, 128);
    }

    /**
     * Registers the entity's attributes like health, speed, etc.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new LandOnOwnersShoulderGoal(this));
        this.goalSelector.addGoal(2, new ModFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new SemiFlyingFlyGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FLYING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFlying", this.isFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("IsFlying"));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!this.level().isClientSide) {
            // Taming logic
            if (this.isFood(itemStack) && !this.isTame()) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte)7); // Taming success particles
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6); // Taming fail particles
                }
                return InteractionResult.SUCCESS;
            }

            // Interaction with a tamed Dearc
            if (this.isTame() && this.isOwnedBy(player)) {
                // Heal with food
                if (this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal(5.0f);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
                // Toggle sitting state
                if(!itemStack.isEmpty()){
                    this.setOrderedToSit(!this.isOrderedToSit());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Update gravity based on flying state
        this.setNoGravity(this.isFlying());
    }

    /**
     * Prevents fall damage while flying or gliding.
     */
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return this.isFlying() || this.shouldGlide() || super.causeFallDamage(fallDistance, multiplier, source);
    }

    // --- SemiFlyer Interface Implementation ---

    @Override
    public void startFlying() {
        this.setFlying(true);
    }

    @Override
    public void stopFlying() {
        this.setFlying(false);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(IS_FLYING);
    }

    @Override
    public void setFlying(boolean flying) {
        if (flying) {
            this.moveControl = new SemiFlyingMoveControl(this, 10, 9);
            this.navigation = this.airNavigation;
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = this.groundNavigation;
        }
        this.entityData.set(IS_FLYING, flying);
    }

    @Override
    public boolean canFly() {
        return !this.isBaby() && !this.isInWater();
    }

    @Override
    public boolean shouldGlide() {
        return !this.onGround() && this.getDeltaMovement().y < 0.0D;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FISHES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.DEARC.get().create(serverLevel);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT; // Placeholder
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT; // Placeholder
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH; // Placeholder
    }
}