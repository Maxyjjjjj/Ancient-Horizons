package com.fungoussoup.ancienthorizons.entity.custom.mob.sauropoda;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractSauropodEntity;
import com.fungoussoup.ancienthorizons.registry.ModDamageTypes;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DiplodocusEntity extends AbstractSauropodEntity {

    private static final EntityDataAccessor<Integer> DATA_BARREL_COUNT =
            SynchedEntityData.defineId(DiplodocusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_TAIL_WHIP_READY =
            SynchedEntityData.defineId(DiplodocusEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int DIPLODOCUS_MAX_PASSENGERS = 4;
    private static final int BARREL_INVENTORY_SIZE = 18;
    private static final double BASE_HEALTH = 80.0;
    private static final double BASE_SPEED = 0.25;

    private static final int TAIL_WHIP_DAMAGE = 8;
    private static final int TAIL_WHIP_COOLDOWN_TIME = 60;
    public AnimationState tailWhipAnimationState = new AnimationState();

    private int tailWhipCooldown = 0;

    public DiplodocusEntity(EntityType<? extends AbstractSauropodEntity> type, Level level) {
        super(type, level);
        this.maxPassengers = DIPLODOCUS_MAX_PASSENGERS;
        this.inventorySize = BARREL_INVENTORY_SIZE;

        if (this.isHarnessed()) {
            initializeBarrelInventory();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BARREL_COUNT, 0);
        builder.define(DATA_TAIL_WHIP_READY, true);
    }

    @Override
    public MountCategory getMountCategory() {
        return MountCategory.MULTITASK;
    }

    public List<MountCategory> getCategories() {
        return List.of(MountCategory.CARGO, MountCategory.PASSENGER);
    }

    @Override
    public Item getTamingItem() {
        return Items.SPRUCE_LEAVES;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return ModSoundEvents.DIPLODOCUS_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound() {
        return ModSoundEvents.DIPLODOCUS_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return ModSoundEvents.DIPLODOCUS_DEATH;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.LEAVES) || stack.is(Tags.Items.CROPS) || stack.is(Items.WHEAT) || stack.is(Items.SUGAR_CANE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        DiplodocusEntity baby = ModEntities.DIPLODOCUS.get().create(level);
        if (baby != null) baby.setGrowthStage(0);
        return baby;
    }

    @Override
    protected void initializeHarnessInventory() {
        super.initializeHarnessInventory();
        initializeBarrelInventory();
    }

    private void initializeBarrelInventory() {
        if (this.inventory == null) {
            this.inventory = new SimpleContainer(BARREL_INVENTORY_SIZE);
            this.entityData.set(DATA_BARREL_COUNT, BARREL_INVENTORY_SIZE);
        }
    }

    @Override
    public int getMaxPassengers() {
        return this.isHarnessed() ? DIPLODOCUS_MAX_PASSENGERS : 0;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.isHarnessed() && super.canAddPassenger(passenger);
    }

    public void performTailWhip() {
        if (tailWhipCooldown <= 0 && this.entityData.get(DATA_TAIL_WHIP_READY)) {
            List<Entity> entities = this.level().getEntitiesOfClass(
                    Entity.class,
                    this.getBoundingBox().inflate(4.0, 1.0, 4.0),
                    e -> e != this && !this.getPassengers().contains(e) && !(e instanceof Player p && this.isOwnedBy(p))
            );

            for (Entity e : entities) {
                Vec3 toEntity = e.position().subtract(this.position());
                Vec3 facing = Vec3.directionFromRotation(0, this.getYRot());
                if (toEntity.normalize().dot(facing) < -0.3 && e instanceof LivingEntity living) {
                    living.hurt(this.damageSources().source(ModDamageTypes.TAIL_WHIP), TAIL_WHIP_DAMAGE);
                    Vec3 knockback = toEntity.normalize().scale(1.5);
                    e.setDeltaMovement(e.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
                }
            }

            tailWhipCooldown = TAIL_WHIP_COOLDOWN_TIME;
            this.entityData.set(DATA_TAIL_WHIP_READY, false);
            this.level().playSound(null, this, SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0f, 0.8f);

            // 👇 Tell the client to start the tail whip animation
            this.level().broadcastEntityEvent(this, (byte) 15);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 15) {
            // Play tail whip animation
            this.tailWhipAnimationState.startIfStopped(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }


    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle() && this.canBeControlledByRider()) {
            Entity rider = this.getControllingPassenger();
            if (rider instanceof LivingEntity livingRider) {
                this.setYRot(livingRider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingRider.getXRot() * 0.5f);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;

                float forward = livingRider.zza;
                float strafe = livingRider.xxa;

                if (rider.isSprinting() && forward <= 0) performTailWhip();

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1f);
                super.travel(new Vec3(strafe, travelVector.y, forward));
                return;
            }
        }
        super.travel(travelVector);
    }

    @Override
    protected void performResourceCollection() {
        if (!this.level().isClientSide && this.isHarnessed()) {
            BlockPos pos = this.blockPosition();
            for (int x = -2; x <= 2; x++)
                for (int z = -2; z <= 2; z++)
                    for (int y = 0; y <= 4; y++) {
                        BlockPos checkPos = pos.offset(x, y, z);
                        BlockState state = this.level().getBlockState(checkPos);
                        if (canHarvestBlock(state)) this.level().destroyBlock(checkPos, true);
                    }
        }
    }

    @Override
    protected boolean canHarvestBlock(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.CROPS);
    }

    @Override
    public void tick() {
        super.tick();

        if (tailWhipCooldown > 0) {
            tailWhipCooldown--;
            if (tailWhipCooldown <= 0) this.entityData.set(DATA_TAIL_WHIP_READY, true);
        }

        if (this.isGrazing() && !this.level().isClientSide) {
            List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(2.0));
            for (ItemEntity item : items) {
                if (this.isFood(item.getItem())) {
                    this.heal(0.5f);
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSauropodEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, TAIL_WHIP_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

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
        if (tag.contains("BarrelCount")) this.entityData.set(DATA_BARREL_COUNT, tag.getInt("BarrelCount"));
        if (tag.contains("TailWhipCooldown")) this.tailWhipCooldown = tag.getInt("TailWhipCooldown");
        if (tag.contains("TailWhipReady")) this.entityData.set(DATA_TAIL_WHIP_READY, tag.getBoolean("TailWhipReady"));
        if (this.isHarnessed()) initializeBarrelInventory();
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
        if (!this.isSaddleable() || this.isHarnessed()) return;

        this.equipSaddle(SoundEvents.HORSE_SADDLE);

        if (!itemStack.isEmpty()) {
            itemStack.shrink(1);
        }
    }
}
