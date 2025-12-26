package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.ILootsChests;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.level.ServerLevelAccessor;
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
    public static final EntityDataAccessor<Boolean> HAS_STOLEN_ITEM =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STEALING_COOLDOWN =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_LEFT_HANDED =
            SynchedEntityData.defineId(BeipiaosaurusEntity.class, EntityDataSerializers.BOOLEAN);

    private BlockPos targetChestPos;
    private int stealingTimer = 0;
    private static final int STEALING_DURATION = 60; // 3 seconds
    private static final int STEAL_COOLDOWN = 600; // 30 seconds

    // Enhanced inventory system
    private final SimpleContainer inventory;
    private static final int INVENTORY_SIZE = 9; // 9 slots like a chest row
    private static final int MIN_ITEMS_TO_STEAL = 2;
    private static final int MAX_ITEMS_TO_STEAL = 5;

    private int hijackAttemptCooldown = 0;
    private static final int HIJACK_COOLDOWN = 200; // 10 seconds

    public BeipiaosaurusEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.inventory = new SimpleContainer(INVENTORY_SIZE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_STEALING, false);
        builder.define(HAS_STOLEN_ITEM, false);
        builder.define(STEALING_COOLDOWN, 0);
        builder.define(IS_LEFT_HANDED, false);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        this.entityData.set(IS_LEFT_HANDED, this.random.nextFloat() < 0.11F);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
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
        return !isInventoryFull()
                && this.entityData.get(STEALING_COOLDOWN) <= 0
                && !this.isBaby();
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void startStealing(BlockPos chestPos) {
        this.targetChestPos = chestPos;
        this.entityData.set(IS_STEALING, true);
        this.stealingTimer = 0;

        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState state = serverLevel.getBlockState(chestPos);
            serverLevel.blockEvent(chestPos, state.getBlock(), 1, 1);
        }
    }

    private void completeTheft() {
        this.entityData.set(IS_STEALING, false);

        if (targetChestPos != null && this.level() instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(targetChestPos);
            if (blockEntity instanceof ChestBlockEntity chestEntity) {
                int itemsStolen = 0;
                int targetItemCount = MIN_ITEMS_TO_STEAL + this.random.nextInt(MAX_ITEMS_TO_STEAL - MIN_ITEMS_TO_STEAL + 1);

                // Try to steal multiple items
                for (int attempt = 0; attempt < 50 && itemsStolen < targetItemCount; attempt++) {
                    int slot = this.random.nextInt(chestEntity.getContainerSize());
                    ItemStack stack = chestEntity.getItem(slot);

                    if (!stack.isEmpty()) {
                        // Determine how much to steal from this stack
                        int stealAmount = Math.min(
                                stack.getCount(),
                                this.random.nextInt(Math.min(stack.getMaxStackSize() / 2, 8)) + 1
                        );

                        ItemStack stolenStack = stack.split(stealAmount);

                        // Try to add to inventory
                        if (addToInventory(stolenStack)) {
                            chestEntity.setChanged();
                            itemsStolen++;
                            this.playSound(SoundEvents.ITEM_PICKUP, 0.8F, 1.0F + (this.random.nextFloat() * 0.4F));
                        } else {
                            // Inventory full, return item to chest
                            stack.grow(stealAmount);
                            break;
                        }
                    }
                }

                if (itemsStolen > 0) {
                    this.entityData.set(HAS_STOLEN_ITEM, true);
                    this.entityData.set(STEALING_COOLDOWN, STEAL_COOLDOWN);

                    // Play triumphant sound if stole multiple items
                    if (itemsStolen >= 3) {
                        this.playSound(SoundEvents.PLAYER_LEVELUP, 0.5F, 1.5F);
                    }
                }

                BlockState state = serverLevel.getBlockState(targetChestPos);
                serverLevel.blockEvent(targetChestPos, state.getBlock(), 1, 0);
            }
        }

        this.targetChestPos = null;
    }

    private boolean addToInventory(ItemStack stack) {
        // Try to stack with existing items first
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slotStack = inventory.getItem(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int spaceLeft = slotStack.getMaxStackSize() - slotStack.getCount();
                if (spaceLeft > 0) {
                    int toAdd = Math.min(spaceLeft, stack.getCount());
                    slotStack.grow(toAdd);
                    stack.shrink(toAdd);
                    if (stack.isEmpty()) {
                        return true;
                    }
                }
            }
        }

        // Find empty slot
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack.copy());
                return true;
            }
        }

        return false;
    }

    // Get display item for rendering (first non-empty slot)
    public ItemStack getStolenItem() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public boolean isLeftHanded() {
        return this.entityData.get(IS_LEFT_HANDED);
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
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.BEIPIAOSAURUS_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.BEIPIAOSAURUS_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.BEIPIAOSAURUS_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasStolenItem", this.entityData.get(HAS_STOLEN_ITEM));
        tag.putInt("StealingCooldown", this.entityData.get(STEALING_COOLDOWN));
        tag.putBoolean("IsLeftHanded", this.entityData.get(IS_LEFT_HANDED));

        // Save entire inventory
        ListTag inventoryTag = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                inventoryTag.add(stack.save(this.level().registryAccess(), itemTag));
            }
        }
        tag.put("Inventory", inventoryTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(HAS_STOLEN_ITEM, tag.getBoolean("HasStolenItem"));
        this.entityData.set(STEALING_COOLDOWN, tag.getInt("StealingCooldown"));
        this.entityData.set(IS_LEFT_HANDED, tag.getBoolean("IsLeftHanded"));

        // Load inventory
        ListTag inventoryTag = tag.getList("Inventory", 10);
        for (int i = 0; i < inventoryTag.size(); i++) {
            CompoundTag itemTag = inventoryTag.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < inventory.getContainerSize()) {
                inventory.setItem(slot, ItemStack.parseOptional(this.level().registryAccess(), itemTag));
            }
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
        // Drop entire inventory on death
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
            }
        }
        inventory.clearContent();
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
                    if (isChestNonEmpty(pos)) {
                        return pos.immutable();
                    }
                }
            }
            return null;
        }

        private boolean isChestNonEmpty(BlockPos pos) {
            if (mob.level() instanceof ServerLevel serverLevel) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                if (blockEntity instanceof ChestBlockEntity chestEntity) {
                    for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                        if (!chestEntity.getItem(i).isEmpty()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    static class HijackVehicleGoal extends Goal {
        private final BeipiaosaurusEntity mob;

        public HijackVehicleGoal(BeipiaosaurusEntity mob, double speed) {
            this.mob = mob;
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