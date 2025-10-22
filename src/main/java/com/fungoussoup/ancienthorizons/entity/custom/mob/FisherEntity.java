package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FisherEntity extends TamableAnimal {

    private static final EntityDataAccessor<Boolean> IS_FISHING = SynchedEntityData.defineId(FisherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_FISHING_ROD = SynchedEntityData.defineId(FisherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FISHING_COOLDOWN = SynchedEntityData.defineId(FisherEntity.class, EntityDataSerializers.INT);

    private static final Ingredient TAMING_INGREDIENTS = Ingredient.of(Items.CHICKEN, ModItems.RAW_PHEASANT, Items.RABBIT);

    private int fishingTimer = 0;
    private BlockPos fishingSpot = null;

    public FisherEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FISHING, false);
        builder.define(HAS_FISHING_ROD, false);
        builder.define(FISHING_COOLDOWN, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FisherFishingGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.25, TAMING_INGREDIENTS, false));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true, entity -> entity.getType().is(ModTags.EntityTypes.FISHER_PREY)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Handle fishing rod interaction near water
        if (itemstack.is(Items.FISHING_ROD) && this.isTame() && this.isOwnedBy(player)) {
            if (isNearWater()) {
                if (!this.level().isClientSide) {
                    this.setHasFishingRod(true);
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Handle taming
        if (this.isFood(itemstack) && !this.isTame()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
                this.setHasFishingRod(true);
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }

            if (!this.level().isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.CHICKEN) || itemStack.is(ModItems.RAW_PHEASANT) || itemStack.is(Items.RABBIT);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Handle fishing cooldown
            int cooldown = this.getFishingCooldown();
            if (cooldown > 0) {
                this.setFishingCooldown(cooldown - 1);
            }

            // Handle fishing behavior
            if (this.isFishing()) {
                this.fishingTimer++;
                if (this.fishingTimer > 100 + this.random.nextInt(100)) { // 5-10 seconds
                    this.finishFishing();
                }

                // Stay still while fishing
                this.navigation.stop();
                this.getRotationVector();
                this.setDeltaMovement(Vec3.ZERO);
            }
        }
    }

    private boolean isNearWater() {
        BlockPos pos = this.blockPosition();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (this.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void finishFishing() {
        this.setIsFishing(false);
        this.fishingTimer = 0;
        this.fishingSpot = null;
        this.setFishingCooldown(200 + this.random.nextInt(200)); // 10–20s cooldown

        // Chance to catch a fish
        if (this.random.nextFloat() < 0.3f) { // 30% chance
            // Pick a random fish item from the FISHES tag
            ItemStack fish = getRandomFish();
            if (!fish.isEmpty()) {
                this.spawnAtLocation(fish);
                this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 1.0f, 1.0f);
                this.gameEvent(GameEvent.ENTITY_ACTION);
            }
        }
    }

    private ItemStack getRandomFish() {
        var fishes = this.level().registryAccess()
                .registryOrThrow(Registries.ITEM)
                .getTag(ItemTags.FISHES)
                .orElse(null);

        if (fishes == null) {
            return ItemStack.EMPTY;
        }

        // Pick random element
        var holder = fishes.getRandomElement(this.random).orElse(null);
        return holder != null ? new ItemStack(holder.value()) : ItemStack.EMPTY;
    }


    // Getters and setters for synched data
    public boolean isFishing() {
        return this.entityData.get(IS_FISHING);
    }

    public void setIsFishing(boolean fishing) {
        this.entityData.set(IS_FISHING, fishing);
    }

    public boolean hasFishingRod() {
        return this.entityData.get(HAS_FISHING_ROD);
    }

    public void setHasFishingRod(boolean hasRod) {
        this.entityData.set(HAS_FISHING_ROD, hasRod);
    }

    public int getFishingCooldown() {
        return this.entityData.get(FISHING_COOLDOWN);
    }

    public void setFishingCooldown(int cooldown) {
        this.entityData.set(FISHING_COOLDOWN, cooldown);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasFishingRod", this.hasFishingRod());
        tag.putInt("FishingCooldown", this.getFishingCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasFishingRod(tag.getBoolean("HasFishingRod"));
        this.setFishingCooldown(tag.getInt("FishingCooldown"));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        FisherEntity baby = ModEntities.FISHER.get().create(serverLevel);
        if (baby != null) {
            baby.setTame(true, false);
            if (this.getOwner() != null) {
                baby.setOwnerUUID(this.getOwnerUUID());
            }
        }
        return baby;
    }

    // Custom fishing goal
    private static class FisherFishingGoal extends Goal {
        private final FisherEntity fisher;
        private BlockPos waterPos;
        private int timeToStart;

        public FisherFishingGoal(FisherEntity fisher) {
            this.fisher = fisher;
        }

        @Override
        public boolean canUse() {
            if (!this.fisher.hasFishingRod() || this.fisher.isFishing() ||
                    this.fisher.getFishingCooldown() > 0 || this.fisher.isOrderedToSit()) {
                return false;
            }

            if (this.fisher.getRandom().nextInt(200) != 0) {
                return false;
            }

            return this.findWaterSpot();
        }

        @Override
        public boolean canContinueToUse() {
            return this.fisher.isFishing() && this.waterPos != null;
        }

        @Override
        public void start() {
            if (this.waterPos != null) {
                this.fisher.navigation.moveTo(this.waterPos.getX(), this.waterPos.getY(), this.waterPos.getZ(), 1.0);
                this.timeToStart = 40 + this.fisher.getRandom().nextInt(40);
            }
        }

        @Override
        public void tick() {
            if (this.waterPos == null) return;

            if (this.fisher.distanceToSqr(Vec3.atCenterOf(this.waterPos)) < 4.0) {
                if (this.timeToStart > 0) {
                    this.timeToStart--;
                } else if (!this.fisher.isFishing()) {
                    this.fisher.setIsFishing(true);
                    this.fisher.fishingSpot = this.waterPos;
                    this.fisher.getLookControl().setLookAt(Vec3.atCenterOf(this.waterPos));
                }
            }
        }

        @Override
        public void stop() {
            this.waterPos = null;
            this.timeToStart = 0;
        }

        private boolean findWaterSpot() {
            BlockPos pos = this.fisher.blockPosition();
            for (int i = 0; i < 20; i++) {
                BlockPos randomPos = pos.offset(
                        this.fisher.getRandom().nextInt(16) - 8,
                        this.fisher.getRandom().nextInt(6) - 3,
                        this.fisher.getRandom().nextInt(16) - 8
                );

                if (this.fisher.level().getFluidState(randomPos).is(FluidTags.WATER) &&
                        this.fisher.level().getBlockState(randomPos.above()).isAir()) {
                    this.waterPos = randomPos;
                    return true;
                }
            }
            return false;
        }
    }
}