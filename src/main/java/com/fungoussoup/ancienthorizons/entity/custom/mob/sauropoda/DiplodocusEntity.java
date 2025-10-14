package com.fungoussoup.ancienthorizons.entity.custom.mob.sauropoda;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractSauropodEntity;
import com.fungoussoup.ancienthorizons.registry.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.JTextComponent;
import java.util.List;

public class DiplodocusEntity extends AbstractSauropodEntity {

    // Diplodocus-specific data trackers
    private static final EntityDataAccessor<Integer> DATA_BARREL_COUNT = SynchedEntityData.defineId(DiplodocusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_TAIL_WHIP_READY = SynchedEntityData.defineId(DiplodocusEntity.class, EntityDataSerializers.BOOLEAN);

    // Diplodocus specific properties
    private static final int DIPLODOCUS_MAX_PASSENGERS = 4; // Can carry multiple passengers
    private static final int BARREL_INVENTORY_SIZE = 18; // Two barrels worth of storage (9 slots each)
    private static final double BASE_HEALTH = 80.0;
    private static final double BASE_SPEED = 0.25;
    public AnimationState tailWhipAnimationState;


    // Tail whip attack properties
    private int tailWhipCooldown = 0;
    private static final int TAIL_WHIP_DAMAGE = 8;
    private static final int TAIL_WHIP_COOLDOWN_TIME = 60; // 3 seconds

    public DiplodocusEntity(EntityType<? extends AbstractSauropodEntity> entityType, Level level) {
        super(entityType, level);

        // Set Diplodocus-specific properties
        this.maxPassengers = DIPLODOCUS_MAX_PASSENGERS;
        this.inventorySize = BARREL_INVENTORY_SIZE;

        // Initialize barrel inventory when harnessed
        if (this.isHarnessed()) {
            initializeBarrelInventory();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TAIL_WHIP_READY, true);
    }

    @Override
    public MountCategory getMountCategory() {
        return MountCategory.PASSENGER; // Primary category
    }

    // Diplodocus can function as both passenger and light cargo mount
    public List<MountCategory> getSecondaryCategories() {
        return List.of(MountCategory.CARGO);
    }

    @Override
    public Item getTamingItem() {
        return Items.SPRUCE_LEAVES;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvents.HORSE_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.LEAVES) ||
                itemStack.is(Items.WHEAT) ||
                itemStack.is(Items.SUGAR_CANE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        DiplodocusEntity baby = ModEntities.DIPLODOCUS.get().create(serverLevel);
        if (baby != null) {
            baby.setGrowthStage(0); // Start as hatchling
        }
        return baby;
    }

    @Override
    protected void initializeHarnessInventory() {
        super.initializeHarnessInventory();
        initializeBarrelInventory();
    }

    private void initializeBarrelInventory() {
        if (this.isHarnessed() && this.inventory == null) {
            this.inventory = new SimpleContainer(BARREL_INVENTORY_SIZE);
        }
    }

    // Enhanced passenger system
    @Override
    public int getMaxPassengers() {
        if (this.isHarnessed()) {
            return DIPLODOCUS_MAX_PASSENGERS;
        }
        return 0;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getMaxPassengers() &&
                this.isHarnessed() &&
                super.canAddPassenger(passenger);
    }

    // Tail whip attack ability
    public void performTailWhip() {
        if (tailWhipCooldown <= 0 && this.entityData.get(DATA_TAIL_WHIP_READY)) {
            List<Entity> nearbyEntities = this.level().getEntitiesOfClass(
                    Entity.class,
                    this.getBoundingBox().inflate(4.0, 1.0, 4.0),
                    entity -> entity != this &&
                            !this.getPassengers().contains(entity) &&
                            !(entity instanceof Player && this.isOwnedBy((Player) entity))
            );

            for (Entity entity : nearbyEntities) {
                // Check if entity is behind the Diplodocus (tail area)
                Vec3 toEntity = entity.position().subtract(this.position());
                Vec3 facing = Vec3.directionFromRotation(0, this.getYRot());
                double dot = toEntity.normalize().dot(facing);

                if (dot < -0.3) { // Behind the dinosaur
                    if (entity instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(this.damageSources().source(ModDamageTypes.TAIL_WHIP), TAIL_WHIP_DAMAGE);
                        // Knockback effect
                        Vec3 knockback = toEntity.normalize().scale(1.5);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
                    }
                }
            }

            this.tailWhipCooldown = TAIL_WHIP_COOLDOWN_TIME;
            this.entityData.set(DATA_TAIL_WHIP_READY, false);
            this.level().playSound(null, this, SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0f, 0.8f);
        }
    }

    // Enhanced movement for passenger transport
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider()) {
                Entity rider = this.getControllingPassenger();
                if (rider instanceof net.minecraft.world.entity.LivingEntity livingRider) {

                    this.setYRot(livingRider.getYRot());
                    this.yRotO = this.getYRot();
                    this.setXRot(livingRider.getXRot() * 0.5f);
                    this.setRot(this.getYRot(), this.getXRot());
                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.yBodyRot;

                    float forward = livingRider.zza;
                    float strafe = livingRider.xxa;

                    // Diplodocus is naturally faster than other sauropods
                    if (forward <= 0.0f) {
                        forward *= 0.25f;
                    }

                    // Check for tail whip input (when rider is moving backwards)
                    if (rider.isSprinting() && forward <= 0) {
                        performTailWhip();
                    }

                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1f); // 10% speed bonus
                    super.travel(new Vec3(strafe, travelVector.y, forward));
                }
            } else {
                super.travel(travelVector);
            }
        }
    }

    // Resource collection (light foraging)
    @Override
    protected void performResourceCollection() {
        if (!this.level().isClientSide && this.isHarnessed()) {
            BlockPos pos = this.blockPosition();
            // Diplodocus can reach higher due to long neck
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    for (int y = 0; y <= 4; y++) { // Can reach higher than other sauropods
                        BlockPos checkPos = pos.offset(x, y, z);
                        BlockState state = this.level().getBlockState(checkPos);
                        if (canHarvestBlock(state)) {
                            this.level().destroyBlock(checkPos, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean canHarvestBlock(BlockState state) {
        // Diplodocus can harvest leaves and crops
        return state.is(BlockTags.LEAVES) ||
                state.is(BlockTags.CROPS);
    }

    @Override
    public void tick() {
        super.tick();

        // Handle tail whip cooldown
        if (tailWhipCooldown > 0) {
            tailWhipCooldown--;
            if (tailWhipCooldown <= 0) {
                this.entityData.set(DATA_TAIL_WHIP_READY, true);
            }
        }

        // Auto-collect nearby food items when grazing
        if (this.isGrazing() && !this.level().isClientSide) {
            List<ItemEntity> nearbyItems = this.level().getEntitiesOfClass(
                    ItemEntity.class,
                    this.getBoundingBox().inflate(2.0)
            );

            for (ItemEntity itemEntity : nearbyItems) {
                if (this.isFood(itemEntity.getItem())) {
                    itemEntity.playerTouch(null); // Pick up the item
                    this.heal(0.5f); // Small healing from eating
                }
            }
        }
    }

    // Custom attributes for Diplodocus
    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSauropodEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, 4.0) // More armor than base
                .add(Attributes.ATTACK_DAMAGE, TAIL_WHIP_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 24.0) // Longer range due to size
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8); // High knockback resistance
    }

    // Enhanced NBT data saving
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BarrelCount", this.entityData.get(DATA_BARREL_COUNT));
        tag.putInt("TailWhipCooldown", this.tailWhipCooldown);
        tag.putBoolean("TailWhipReady", this.entityData.get(DATA_TAIL_WHIP_READY));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BarrelCount")) {
            this.entityData.set(DATA_BARREL_COUNT, tag.getInt("BarrelCount"));
        }
        if (tag.contains("TailWhipCooldown")) {
            this.tailWhipCooldown = tag.getInt("TailWhipCooldown");
        }
        if (tag.contains("TailWhipReady")) {
            this.entityData.set(DATA_TAIL_WHIP_READY, tag.getBoolean("TailWhipReady"));
        }

        // Reinitialize inventory based on saved barrel count
        if (this.isHarnessed()) {
            initializeBarrelInventory();
        }
    }

    // Getter methods for rendering and client-side logic
    public int getBarrelCount() {
        return this.entityData.get(DATA_BARREL_COUNT);
    }

    public boolean isTailWhipReady() {
        return this.entityData.get(DATA_TAIL_WHIP_READY);
    }

    public int getTailWhipCooldown() {
        return this.tailWhipCooldown;
    }

    // Override to handle multiple categories
    public boolean hasCategory(MountCategory category) {
        return getMountCategory() == category || getSecondaryCategories().contains(category);
    }

    // Enhanced size scaling for growth stages
    @Override
    protected void updateAttributesForGrowthStage() {
        super.updateAttributesForGrowthStage();

        // Adult Diplodocus gets passenger capacity bonus
        if (getGrowthStage() >= 3) {
            this.maxPassengers = DIPLODOCUS_MAX_PASSENGERS;
        } else {
            this.maxPassengers = 1;
        }
    }

    @Override
    public void performSpecialAbility() {
        // Diplodocus special ability is tail whip
        performTailWhip();

        // Also perform light resource collection if moving slow
        if (this.getDeltaMovement().length() < 0.1) {
            performResourceCollection();
        }
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {

    }
}