package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.ILootsChests;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class BeipiaosaurusEntity extends Animal implements ILootsChests {
    public static final EntityDataAccessor<Boolean> IS_STEALING =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_STOLEN_ITEM =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STEALING_COOLDOWN =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.INT);

    private BlockPos targetChestPos;
    private int stealingTimer = 0;
    private static final int STEALING_DURATION = 60; // 3 seconds
    private static final int STEAL_COOLDOWN = 600; // 30 seconds
    private ItemStack stolenItem = ItemStack.EMPTY;

    private int hijackAttemptCooldown = 0;
    private static final int HIJACK_COOLDOWN = 200; // 10 seconds

    public BeipiaosaurusEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_STEALING, false);
        builder.define(HAS_STOLEN_ITEM, false);
        builder.define(STEALING_COOLDOWN, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new StealFromChestGoal(this, 1.2D));
        this.goalSelector.addGoal(5, new HijackVehicleGoal(this, 1.3D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int cooldown = this.entityData.get(STEALING_COOLDOWN);
            if (cooldown > 0) {
                this.entityData.set(STEALING_COOLDOWN, cooldown - 1);
            }

            if (hijackAttemptCooldown > 0) hijackAttemptCooldown--;

            if (this.entityData.get(IS_STEALING)) {
                stealingTimer++;
                if (stealingTimer >= STEALING_DURATION) completeTheft();
            }
        }
    }

    public boolean canSteal() {
        return !this.entityData.get(HAS_STOLEN_ITEM)
                && this.entityData.get(STEALING_COOLDOWN) <= 0
                && !this.isBaby();
    }

    public void startStealing(BlockPos chestPos) {
        this.targetChestPos = chestPos;
        this.entityData.set(IS_STEALING, true);
        this.stealingTimer = 0;
        this.playSound(SoundEvents.CHEST_OPEN, 0.5F, 1.0F);
    }

    private void completeTheft() {
        this.entityData.set(IS_STEALING, false);

        if (targetChestPos != null && this.level() instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(targetChestPos);
            if (blockEntity instanceof ChestBlockEntity chestEntity) {
                int size = chestEntity.getContainerSize();
                for (int attempts = 0; attempts < 10; attempts++) {
                    int slot = this.random.nextInt(size);
                    ItemStack stack = chestEntity.getItem(slot);
                    if (!stack.isEmpty()) {
                        int stealAmount = Math.min(stack.getCount(), this.random.nextInt(3) + 1);
                        this.stolenItem = stack.split(stealAmount);
                        chestEntity.setChanged();
                        this.entityData.set(HAS_STOLEN_ITEM, true);
                        this.entityData.set(STEALING_COOLDOWN, STEAL_COOLDOWN);
                        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                        break;
                    }
                }
            }
        }

        this.targetChestPos = null;
    }

    public boolean hasVehicleNearby() {
        AABB searchBox = this.getBoundingBox().inflate(8.0D);
        List<Entity> nearby = this.level().getEntities(this, searchBox);
        for (Entity e : nearby) {
            if (isHijackableVehicle(e)) return true;
        }
        return false;
    }

    private boolean isHijackableVehicle(Entity entity) {
        return entity instanceof Boat || entity instanceof Minecart;
    }

    public void attemptHijack() {
        if (hijackAttemptCooldown > 0) return;

        AABB searchBox = this.getBoundingBox().inflate(8.0D);
        List<Entity> nearby = this.level().getEntities(this, searchBox);

        for (Entity e : nearby) {
            if (isHijackableVehicle(e) && e.getPassengers().isEmpty()) {
                this.startRiding(e, true);
                this.playSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, 1.5F);
                hijackAttemptCooldown = HIJACK_COOLDOWN;
                break;
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasStolenItem", this.entityData.get(HAS_STOLEN_ITEM));
        tag.putInt("StealingCooldown", this.entityData.get(STEALING_COOLDOWN));

        if (!this.stolenItem.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();

            this.stolenItem.save(this.level().registryAccess(), itemTag);

            tag.put("StolenItem", itemTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(HAS_STOLEN_ITEM, tag.getBoolean("HasStolenItem"));
        this.entityData.set(STEALING_COOLDOWN, tag.getInt("StealingCooldown"));

        if (tag.contains("StolenItem")) {
            this.stolenItem = ItemStack.parseOptional(
                    this.level().registryAccess(),
                    tag.getCompound("StolenItem")
            );
        } else {
            this.stolenItem = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.PREHISTORIC_HERBIVORE_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return ModEntities.BEIPIAOSAURUS.get().create(level);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        if (!stolenItem.isEmpty()) {
            this.spawnAtLocation(stolenItem);
            stolenItem = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean isLootable(Container inventory) {
        return true;
    }

    @Override
    public boolean shouldLootItem(ItemStack stack) {
        return true;
    }

    // === Custom Goals ===

    static class StealFromChestGoal extends Goal {
        private final BeipiaosaurusEntity mob;
        private final double speed;
        private BlockPos targetPos;
        private int searchCooldown = 0;

        public StealFromChestGoal(BeipiaosaurusEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            if (!mob.canSteal()) return false;
            if (searchCooldown > 0) {
                searchCooldown--;
                return false;
            }
            targetPos = findNearestChest();
            return targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return targetPos != null && mob.canSteal() && !mob.entityData.get(IS_STEALING);
        }

        @Override
        public void start() {
            searchCooldown = 100;
        }

        @Override
        public void tick() {
            if (targetPos != null) {
                mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
                if (mob.distanceToSqr(Vec3.atCenterOf(targetPos)) < 2.0D) {
                    mob.startStealing(targetPos);
                    targetPos = null;
                }
            }
        }

        private BlockPos findNearestChest() {
            BlockPos mobPos = mob.blockPosition();
            int range = 16;

            for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-range, -range, -range),
                    mobPos.offset(range, range, range))) {
                BlockState state = mob.level().getBlockState(pos);
                if (state.getBlock() instanceof AbstractChestBlock<?>) {
                    return pos.immutable();
                }
            }
            return null;
        }
    }

    static class HijackVehicleGoal extends Goal {
        private final BeipiaosaurusEntity mob;
        private final double speed;

        public HijackVehicleGoal(BeipiaosaurusEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return !mob.isVehicle() && mob.hasVehicleNearby() && mob.hijackAttemptCooldown <= 0;
        }

        @Override
        public void start() {
            mob.attemptHijack();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}