package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.WaterbirdNavigation;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

public class MerganserEntity extends Animal {
    private static final double BABY_RIDE_CHANCE = 0.7D;

    public MerganserEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.navigation = new WaterbirdNavigation(this, level);
    }

    // Attributes (Health, Speed, etc.)
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(NeoForgeMod.SWIM_SPEED, 0.35D);
    }

    // AI Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FISHES);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack stack) {
        return SoundEvents.PARROT_EAT;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mate) {
        MerganserEntity baby = ModEntities.MERGANSER.get().create(serverLevel);
        if (baby != null) {
            baby.setBaby(true);

            // 70% chance to ride a random parent
            if (serverLevel.random.nextDouble() < BABY_RIDE_CHANCE) {
                // Choose random parent between this and mate
                MerganserEntity parentToRide = serverLevel.random.nextBoolean() ? this : (MerganserEntity) mate;

                // Delay mounting slightly to ensure proper spawning
                baby.setPos(parentToRide.getX(), parentToRide.getY() + parentToRide.getBbHeight() * 0.5D, parentToRide.getZ());
                serverLevel.addFreshEntity(baby);

                serverLevel.scheduleTick(parentToRide.blockPosition(), serverLevel.getBlockState(parentToRide.blockPosition()).getBlock(), 1);
                baby.startRiding(parentToRide, true);
            } else {
                serverLevel.addFreshEntity(baby);
            }
        }
        return baby;
    }

    // Sounds
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.MERGANSER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.MERGANSER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.MERGANSER_DEATH;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isPassenger() && !this.isBaby()) {
            this.stopRiding();
        }
    }

    @Override
    public boolean isPushable() {
        return !this.hasPassenger(merganser -> ((MerganserEntity) merganser).isBaby());
    }
}