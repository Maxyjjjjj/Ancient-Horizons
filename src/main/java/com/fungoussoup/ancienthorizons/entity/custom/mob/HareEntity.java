package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaEntity;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public class HareEntity extends Animal implements VariantHolder<HareEntity.Type> {

    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HareEntity.class, EntityDataSerializers.INT);

    public HareEntity(EntityType<? extends HareEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID, 0);
    }

    @Override
    protected void registerGoals() {
        // Basic AI goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(ItemTags.RABBIT_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Combat behavior - kick smaller predators
        this.goalSelector.addGoal(1, new HareDefensiveGoal(this, 1.4D));

        // Flee from larger predators
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 2.2D, 2.2D,
                this::shouldFleeFrom));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    // Determine what the hare should flee from (larger predators)
    private boolean shouldFleeFrom(LivingEntity entity) {
        if (entity instanceof Player) return false;

        // Check if entity is in carnivore tag and is bigger than a hare
        if (entity.getType().is(ModTags.EntityTypes.CARNIVORES)) {
            return entity.getBbHeight() > 1.0F || entity.getBbWidth() > 1.0F;
        }

        return false;
    }

    // Determine what the hare can fight (smaller predators)
    private boolean canFightBack(LivingEntity entity) {
        // Only fight small predators in the carnivore tag
        if (entity.getType().is(ModTags.EntityTypes.CARNIVORES)) {
            return entity.getBbHeight() <= 1.0F && entity.getBbWidth() <= 1.0F;
        }

        return false;
    }


    public void doHurtTarget(LivingEntity target) {
        // Play kick sound and animation
        this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

        // Add knockback effect to kicked targets
        if (target != null) {
            double knockbackStrength = 0.5D;
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0) {
                target.push(dx / distance * knockbackStrength, 0.1D, dz / distance * knockbackStrength);
            }
        }

        assert target != null;
        super.doHurtTarget(target);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.RABBIT_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        HareEntity baby = ModEntities.HARE.get().create(serverLevel);
        if (baby != null) {
            baby.setVariant(this.getVariant());
        }
        return baby;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    public void setVariant(Type variant) {
        this.entityData.set(DATA_TYPE_ID, variant.getId());
    }

    public Type getVariant() {
        return Type.byId((Integer)this.entityData.get(DATA_TYPE_ID));
    }

    private static class HareDefensiveGoal extends MeleeAttackGoal {
        private final HareEntity hare;

        public HareDefensiveGoal(HareEntity hare, double speedModifier) {
            super(hare, speedModifier, false);
            this.hare = hare;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.hare.getTarget();
            if (target == null) return false;
            return this.hare.canFightBack(target) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.hare.getTarget();
            if (target == null) return false;
            return this.hare.canFightBack(target) && super.canContinueToUse();
        }

        protected void checkAndPerformAttack(LivingEntity target, double distanceToTargetSqr) {
            double attackReachSqr = getAttackReachSqr(target); // compute squared reach here
            if (distanceToTargetSqr <= attackReachSqr && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.hare.swing(this.hare.getUsedItemHand());
                this.hare.doHurtTarget(target);

                // After kicking, try to hop away
                this.hare.getNavigation().stop();
                double dx = this.hare.getX() - target.getX();
                double dz = this.hare.getZ() - target.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance > 0) {
                    double hopDistance = 3.0D;
                    double hopX = this.hare.getX() + (dx / distance) * hopDistance;
                    double hopZ = this.hare.getZ() + (dz / distance) * hopDistance;
                    this.hare.getNavigation().moveTo(hopX, this.hare.getY(), hopZ, 1.5D);
                }
            }
        }

        /**
         * Return squared attack reach. We use entity widths as a simple heuristic.
         * distanceToTargetSqr is a squared distance, so return a squared reach threshold.
         */
        private double getAttackReachSqr(LivingEntity target) {
            double reach = this.hare.getBbWidth() * 2.0D + target.getBbWidth();
            return reach * reach;
        }
    }

    public static enum Type implements StringRepresentable {
        BROWN(0, "brown"),
        SNOW(1, "snow");

        public static final EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final int id;
        private final String name;

        private Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String name) {
            return (Type)CODEC.byName(name, BROWN);
        }

        public static Type byId(int index) {
            return (Type)BY_ID.apply(index);
        }

        public static Type byBiome(Holder<Biome> biome) {
            return biome.is(BiomeTags.SPAWNS_SNOW_FOXES) ? SNOW : BROWN;
        }
    }

}