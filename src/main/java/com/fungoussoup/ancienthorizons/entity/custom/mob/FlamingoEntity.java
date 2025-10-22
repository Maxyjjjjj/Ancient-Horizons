package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.interfaces.InterchangeableVariantsEntity;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FlamingoEntity extends Animal implements InterchangeableVariantsEntity {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(ModItems.RAW_SHRIMP.get());

    // Flamingo Variants - Diet-based coloration
    public static final int VARIANT_WHITE = 0;      // No/little shrimp diet - pale white/gray
    public static final int VARIANT_PALE = 1;       // Some shrimp diet - light pink
    public static final int VARIANT_PINK = 2;       // Regular shrimp diet - pink
    public static final int VARIANT_RED = 3;        // High shrimp diet - bright pink
    public static final int MAX_VARIANTS = 4;

    // Data Accessors for syncing
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(FlamingoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SHRIMP_EATEN = SynchedEntityData.defineId(FlamingoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> DATA_LAST_FED = SynchedEntityData.defineId(FlamingoEntity.class, EntityDataSerializers.LONG);

    // Diet tracking for coloration
    public int shrimpEatenCount = 0;
    public long lastFedTime = 0;
    private int variantUpdateTimer = 0;

    public FlamingoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, VARIANT_PALE);
        builder.define(DATA_SHRIMP_EATEN, 0);
        builder.define(DATA_LAST_FED, 0L);
    }

    @Override
    protected void registerGoals() {
        // Basic AI Goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new FlamingoWaterStrollGoal(this, 1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.FLAMINGO.get().create(serverLevel);
    }

    // Sound Events
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    public static boolean checkFlamingoSpawnRules(EntityType<FlamingoEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(Blocks.SAND)
                || level.getBlockState(pos.below()).is(Blocks.WATER)
                && level.getRawBrightness(pos, 0) > 8;
    }

    // Movement and Behavior
    @Override
    public void tick() {
        super.tick();

        // Update variant based on diet every 200 ticks (10 seconds)
        if (++variantUpdateTimer >= 200) {
            variantUpdateTimer = 0;
            updateVariant();
        }

        // Add some flamingo-specific behavior here
        if (this.isInWater() && this.random.nextInt(200) == 0) {
            // Occasionally stand on one leg when in water
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void ageUp(int ageInTicks, boolean forced) {
        super.ageUp(ageInTicks, forced);
        if (this.isBaby()) {
            // Baby flamingos could have different behavior
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(8.0D);
        } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(15.0D);
        }
    }

    // Flamingo specific methods
    public boolean isInShallowWater() {
        return this.isInWater() && this.getFluidHeight(FluidTags.WATER) < 0.4;
    }

    @Override
    public void ate() {
        super.ate();
        // Track shrimp consumption for coloration
        this.shrimpEatenCount++;
        this.lastFedTime = this.level().getGameTime();
        this.entityData.set(DATA_SHRIMP_EATEN, this.shrimpEatenCount);
        this.entityData.set(DATA_LAST_FED, this.lastFedTime);
    }

    // InterchangeableVariantsEntity Implementation
    @Override
    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    @Override
    public void setVariant(int variant) {
        int oldVariant = getVariant();
        this.entityData.set(DATA_VARIANT, Math.max(0, Math.min(variant, MAX_VARIANTS - 1)));
        if (oldVariant != variant) {
            onVariantChanged(oldVariant, variant);
        }
    }

    @Override
    public int getMaxVariants() {
        return MAX_VARIANTS;
    }

    @Override
    public EntityDataAccessor<Integer> getVariantDataAccessor() {
        return DATA_VARIANT;
    }

    @Override
    public void updateVariant() {
        if (shouldUpdateVariant()) {
            setVariant(getTargetVariant());
        }
    }

    @Override
    public boolean shouldUpdateVariant() {
        return this.level().getGameTime() % 1200 == 0; // Update every minute
    }

    @Override
    public int getTargetVariant() {
        long currentTime = this.level().getGameTime();
        long timeSinceLastFed = currentTime - this.lastFedTime;
        int shrimpCount = this.shrimpEatenCount;

        // Shrimp diet affects coloration - carotenoids make flamingos pink
        // Color fades over time without proper diet

        // If it's been too long since eating shrimp, fade to pale
        if (timeSinceLastFed > 24000) { // 20 minutes without food
            shrimpCount = Math.max(0, shrimpCount - 1);
            this.shrimpEatenCount = shrimpCount;
            this.entityData.set(DATA_SHRIMP_EATEN, shrimpCount);
        }

        // Determine variant based on shrimp consumption
        if (shrimpCount >= 30) {
            return VARIANT_RED;
        } else if (shrimpCount >= 20) {
            return VARIANT_PINK;
        } else if (shrimpCount >= 10) {
            return VARIANT_PALE;
        } else {
            return VARIANT_WHITE;
        }
    }

    @Override
    public String getVariantName() {
        return switch (getVariant()) {
            case VARIANT_WHITE -> "white";
            case VARIANT_PALE -> "pale";
            case VARIANT_PINK -> "pink";
            case VARIANT_RED -> "red";
            default -> "unknown";
        };
    }

    @Override
    public void onVariantChanged(int oldVariant, int newVariant) {
        // Play sound effect when changing color
        if (!this.level().isClientSide && oldVariant != newVariant) {
            this.playSound(SoundEvents.DYE_USE, 0.5F, 1.0F + (this.random.nextFloat() - 0.5F) * 0.2F);

            // Spawn particles to indicate color change
            if (newVariant > oldVariant) {
                // Getting pinker - spawn pink particles
                for (int i = 0; i < 5; i++) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HEART,
                            this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                            d0, d1, d2);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        saveVariantToNBT(tag);
        tag.putInt("ShrimpEaten", this.shrimpEatenCount);
        tag.putLong("LastFed", this.lastFedTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        loadVariantFromNBT(tag);
        this.shrimpEatenCount = tag.getInt("ShrimpEaten");
        this.lastFedTime = tag.getLong("LastFed");
        this.entityData.set(DATA_SHRIMP_EATEN, this.shrimpEatenCount);
        this.entityData.set(DATA_LAST_FED, this.lastFedTime);
    }

    // Custom AI Goal for flamingo behavior in water
    public static class FlamingoWaterStrollGoal extends Goal {
        private final FlamingoEntity flamingo;
        private double targetX;
        private double targetY;
        private double targetZ;
        private final double speedModifier;

        public FlamingoWaterStrollGoal(FlamingoEntity flamingo, double speedModifier) {
            this.flamingo = flamingo;
            this.speedModifier = speedModifier;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.flamingo.isVehicle() || !this.flamingo.isInShallowWater()) {
                return false;
            } else {
                Vec3 vec3 = this.getPosition();
                if (vec3 == null) {
                    return false;
                } else {
                    this.targetX = vec3.x;
                    this.targetY = vec3.y;
                    this.targetZ = vec3.z;
                    return true;
                }
            }
        }

        @Nullable
        private Vec3 getPosition() {
            RandomSource random = this.flamingo.getRandom();
            BlockPos blockpos = this.flamingo.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(
                        random.nextInt(20) - 10,
                        random.nextInt(6) - 3,
                        random.nextInt(20) - 10
                );

                if (this.flamingo.level().getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.flamingo.getNavigation().isDone() && this.flamingo.isInShallowWater();
        }

        @Override
        public void start() {
            this.flamingo.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speedModifier);
        }
    }
}