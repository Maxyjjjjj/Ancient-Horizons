package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Golden Snub-Nosed Monkey — a complex, social arboreal primate.
 */
public class MonkeyEntity extends Animal {
    private int curiosityCooldown = 0; // cooldown to prevent over-interaction with players
    private int groomingTimer = 0;

    public MonkeyEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // --- Attributes ---
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.JUMP_STRENGTH, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    // --- Goals ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, this::isFood, false));
        this.goalSelector.addGoal(5, new FollowMobGoal(this, 1.05D, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new GroomingGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.APPLE)
                || stack.is(Items.MELON_SLICE)
                || stack.is(Items.SWEET_BERRIES)
                || stack.is(Items.GLOW_BERRIES)
                || stack.is(Items.BAMBOO)
                || stack.is(Items.GLOW_LICHEN)
                || stack.is(ItemTags.FLOWERS);
    }

    // --- Breeding ---
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return ModEntities.MONKEY.get().create(level);
    }

    // --- Behavior Ticks ---
    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (curiosityCooldown > 0) curiosityCooldown--;
            if (groomingTimer > 0) groomingTimer--;
        }
    }

    // --- Sounds ---
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT; // placeholder; should be custom
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PARROT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 150;
    }

    // --- Custom inner goals ---

    /**
     * Grooming goal — monkey occasionally sits still for a few seconds.
     */
    private static class GroomingGoal extends Goal {
        private final MonkeyEntity monkey;
        private int timer = 0;

        GroomingGoal(MonkeyEntity monkey) {
            this.monkey = monkey;
        }

        @Override
        public boolean canUse() {
            return monkey.onGround() && monkey.random.nextInt(400) == 0;
        }

        @Override
        public void start() {
            timer = 60 + monkey.random.nextInt(60);
            monkey.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (timer-- > 0) {
                // Cosmetic — imagine grooming animation
                if (timer % 20 == 0) {
                    monkey.playSound(SoundEvents.FOX_SNIFF, 0.4F, 1.0F);
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return timer > 0;
        }
    }
}
