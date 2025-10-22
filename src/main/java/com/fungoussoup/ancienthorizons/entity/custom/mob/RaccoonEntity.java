package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.interfaces.ILootsChests;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class RaccoonEntity extends Animal implements ILootsChests {
    private static final EntityDataAccessor<Boolean> IS_WASHING = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WASH_TIMER = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.ITEM_STACK);
    public AnimationState sleepAnimationState = new AnimationState();

    private int washingCooldown = 0;
    private int scavengeCooldown = 0;
    private BlockPos lastWaterPos;

    public RaccoonEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_WASHING, false);
        builder.define(IS_SITTING, false);
        builder.define(WASH_TIMER, 0);
        builder.define(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WashItemGoal(this));
        this.goalSelector.addGoal(6, new ScavengeGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.RACCOON_FOOD) ||
                stack.is(Items.SWEET_BERRIES) ||
                stack.is(Items.APPLE) ||
                stack.is(Items.BREAD) ||
                stack.is(Items.COOKED_CHICKEN) ||
                stack.is(Items.COOKED_BEEF) ||
                stack.is(Items.COOKED_PORKCHOP) ||
                stack.is(ModItems.COOKED_CHEVON)||
                stack.is(ModItems.COOKED_PHEASANT) ||
                stack.is(Items.COOKED_COD) ||
                stack.is(Items.COOKED_SALMON);
    }

    @Override
    public boolean isLootable(Container inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (shouldLootItem(inventory.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldLootItem(ItemStack stack) {
        return this.isFood(stack);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.RACCOON.get().create(serverLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.washingCooldown > 0) {
            this.washingCooldown--;
        }

        if (this.scavengeCooldown > 0) {
            this.scavengeCooldown--;
        }

        if (this.isWashing()) {
            int timer = this.getWashTimer();
            if (timer > 0) {
                this.setWashTimer(timer - 1);
            } else {
                this.setWashing(false);
                this.finishWashing();
            }
        }

        // Raccoons are more active at night
        if (this.level().isNight() && this.random.nextFloat() < 0.1F) {
            this.playSound(SoundEvents.FOX_AMBIENT, 0.5F, this.getVoicePitch());
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isFood(itemstack)) {
            if (!this.level().isClientSide) {
                this.usePlayerItem(player, hand, itemstack);
                this.heal(2.0F);
                if (this.getAge() == 0 && this.canFallInLove()) {
                    this.setInLove(player);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.RACCOON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.RACCOON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.RACCOON_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsWashing", this.isWashing());
        compound.putBoolean("IsSitting", this.isSitting());
        compound.putInt("WashTimer", this.getWashTimer());
        compound.putInt("WashingCooldown", this.washingCooldown);
        compound.putInt("ScavengeCooldown", this.scavengeCooldown);

        if (this.lastWaterPos != null) {
            compound.putLong("LastWaterPos", this.lastWaterPos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setWashing(compound.getBoolean("IsWashing"));
        this.setSitting(compound.getBoolean("IsSitting"));
        this.setWashTimer(compound.getInt("WashTimer"));
        this.washingCooldown = compound.getInt("WashingCooldown");
        this.scavengeCooldown = compound.getInt("ScavengeCooldown");

        if (compound.contains("LastWaterPos")) {
            this.lastWaterPos = BlockPos.of(compound.getLong("LastWaterPos"));
        }
    }

    // Data accessor methods
    public boolean isWashing() {
        return this.entityData.get(IS_WASHING);
    }

    public void setWashing(boolean washing) {
        this.entityData.set(IS_WASHING, washing);
    }

    public boolean isSitting() {
        return this.entityData.get(IS_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(IS_SITTING, sitting);
    }

    public int getWashTimer() {
        return this.entityData.get(WASH_TIMER);
    }

    public void setWashTimer(int timer) {
        this.entityData.set(WASH_TIMER, timer);
    }

    public ItemStack getHeldItem() {
        return this.entityData.get(HELD_ITEM);
    }

    public void setHeldItem(ItemStack item) {
        this.entityData.set(HELD_ITEM, item);
    }

    // Washing behavior
    public void startWashing(ItemStack item) {
        if (this.washingCooldown <= 0) {
            this.setHeldItem(item);
            this.setWashing(true);
            this.setWashTimer(60); // 3 seconds
            this.washingCooldown = 200; // 10 seconds cooldown
        }
    }

    private void finishWashing() {
        ItemStack item = this.getHeldItem();
        if (!item.isEmpty()) {
            // Drop the washed item
            this.spawnAtLocation(item);
            this.setHeldItem(ItemStack.EMPTY);
            this.playSound(SoundEvents.GENERIC_SPLASH, 0.5F, 1.0F);
        }
    }

    public boolean canWash() {
        return this.washingCooldown <= 0 && !this.isWashing();
    }

    public boolean canScavenge() {
        return this.scavengeCooldown <= 0;
    }

    public void setScavengeCooldown(int cooldown) {
        this.scavengeCooldown = cooldown;
    }

    public BlockPos getLastWaterPos() {
        return this.lastWaterPos;
    }

    public void setLastWaterPos(BlockPos pos) {
        this.lastWaterPos = pos;
    }

    // Custom AI Goals
    private static class WashItemGoal extends Goal {
        private final RaccoonEntity raccoon;
        private BlockPos waterPos;
        private int washTime;

        public WashItemGoal(RaccoonEntity raccoon) {
            this.raccoon = raccoon;
        }

        @Override
        public boolean canUse() {
            if (!this.raccoon.canWash()) {
                return false;
            }

            // Look for water nearby
            BlockPos pos = this.raccoon.blockPosition();
            for (int x = -8; x <= 8; x++) {
                for (int z = -8; z <= 8; z++) {
                    for (int y = -3; y <= 3; y++) {
                        BlockPos checkPos = pos.offset(x, y, z);
                        if (this.raccoon.level().getBlockState(checkPos).is(Blocks.WATER)) {
                            this.waterPos = checkPos;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.raccoon.getNavigation().moveTo(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), 1.0D);
            this.washTime = 0;
        }

        @Override
        public void tick() {
            if (this.raccoon.distanceToSqr(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ()) < 4.0D) {
                this.washTime++;
                if (this.washTime > 20 && this.raccoon.random.nextFloat() < 0.1F) {
                    // Simulate washing behavior
                    this.raccoon.startWashing(new ItemStack(net.minecraft.world.item.Items.STICK));
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.waterPos != null && !this.raccoon.isWashing();
        }
    }

    private static class ScavengeGoal extends Goal {
        private final RaccoonEntity raccoon;

        public ScavengeGoal(RaccoonEntity raccoon) {
            this.raccoon = raccoon;
        }

        @Override
        public boolean canUse() {
            return this.raccoon.canScavenge() && this.raccoon.random.nextFloat() < 0.02F;
        }

        @Override
        public void start() {
            this.raccoon.setScavengeCooldown(400); // 20 seconds

            if (this.raccoon.random.nextFloat() < 0.3F) {
                if (this.raccoon.random.nextFloat() < 0.99F){
                    // Occasionally find random items
                    ItemStack[] scavengeItems = {
                            new ItemStack(Items.STICK),
                            new ItemStack(Items.STONE),
                            new ItemStack(Items.IRON_NUGGET),
                            new ItemStack(Items.GOLD_NUGGET),
                            new ItemStack(Items.SWEET_BERRIES)
                    };

                    ItemStack found = scavengeItems[this.raccoon.random.nextInt(scavengeItems.length)];
                    this.raccoon.spawnAtLocation(found);
                    this.raccoon.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 1.0F);
                } else {
                    // More rarely find random RARE items
                    ItemStack[] scavengeItems = {
                            new ItemStack(Items.DIAMOND),
                            new ItemStack((ItemLike) ModItems.IVORY),
                            new ItemStack(Items.LAPIS_LAZULI),
                            new ItemStack(Items.EMERALD),
                            new ItemStack((ItemLike) ModItems.TIME_STONE)
                    };

                    ItemStack found = scavengeItems[this.raccoon.random.nextInt(scavengeItems.length)];
                    this.raccoon.spawnAtLocation(found);
                    this.raccoon.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 1.0F);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
}