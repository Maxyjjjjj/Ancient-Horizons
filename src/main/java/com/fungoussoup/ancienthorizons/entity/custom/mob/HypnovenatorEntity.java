package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class HypnovenatorEntity extends Animal {
    private static final int HYPNOSIS_RANGE = 16;
    private static final int HYPNOSIS_DURATION = 1200; // 1 minute at 20 ticks/second
    private static final double SMALL_PREY_SIZE_THRESHOLD = 1.25; // Height threshold for small prey
    private int hypnosisCooldown = 0;
    private LivingEntity hypnosisTarget = null;
    private int hypnosisTimer = 0;
    private int huntingCooldown = 0; // Cooldown between hunting attempts

    public AnimationState hypnosisAnimationState = new AnimationState();
    private int hypnosisAnimationTimeout = 0;

    public HypnovenatorEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        // Priority 0 - Float in water (highest priority)
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Priority 1 - Hypnosis (highest priority afer floating)
        this.goalSelector.addGoal(1, new HypnosisGoal());

        // Priority 2 - Survival and animal behaviour
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));

        // Priority 3 - Basic movement and behavior
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Target selector - look for small prey to hypnotize
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Animal.class,
                10, true, false, this::isValidHypnosisTarget));
    }

    @Override
    public void tick() {
        super.tick();

        // Reduce cooldowns
        if (hypnosisCooldown > 0) {
            hypnosisCooldown--;
        }
        if (huntingCooldown > 0) {
            huntingCooldown--;
        }

        // Handle active hypnosis
        if (hypnosisTarget != null) {
            if (hypnosisTimer > 0) {
                hypnosisTimer--;
                maintainHypnosis();
            } else {
                endHypnosis();
            }
        }
    }

    private boolean isValidHypnosisTarget(LivingEntity entity) {
        // Don't target players
        if (entity instanceof Player) {
            return false;
        }

        // Don't target other Hypnovenators
        if (entity instanceof HypnovenatorEntity) {
            return false;
        }

        if (isLargeAnimal(entity)){
            return false;
        }

        // Only target living animals within range
        if (!entity.isAlive() || this.distanceTo(entity) > HYPNOSIS_RANGE) {
            return false;
        }

        if (entity.getType().is(ModTags.EntityTypes.CARNIVORES)) {
            return false;
        }

        // Check if it's a small prey animal
        return isSmallPrey(entity);
    }

    private boolean isSmallPrey(LivingEntity entity) {
        // Explicitly target small animals that the Hypnovenator should hunt
        if (entity instanceof LivingEntity && entity.getType().is(ModTags.EntityTypes.HYPNOVENATOR_PREY)) return true;

        // Target baby turtles specifically (they're smaller and more vulnerable)
        if (entity instanceof Turtle turtle) {
            return turtle.isBaby(); // Only hunt baby turtles, adults are too big
        }

        // For other entities, use size-based filtering
        // Check if the entity is small enough (height-based)
        double entityHeight = entity.getBbHeight();
        if (entityHeight > SMALL_PREY_SIZE_THRESHOLD) {
            return false;
        }

        // Additional check: if it's a baby animal, it's valid prey regardless of type
        if (entity instanceof AgeableMob ageableMob) {
            double ageableMobHeight = ageableMob.getBbHeight();
            if (ageableMobHeight > SMALL_PREY_SIZE_THRESHOLD) {
                return false;
            }
            return ageableMob.isBaby();
        }

        return true;
    }

    private boolean isLargeAnimal(LivingEntity entity) {
        return (entity instanceof AgeableMob && entity.getBbHeight() > SMALL_PREY_SIZE_THRESHOLD);
    }

    private void startHypnosis(LivingEntity target) {
        this.hypnosisTarget = target;
        this.hypnosisTimer = HYPNOSIS_DURATION;
        this.hypnosisCooldown = 200; // 10 second cooldown after hypnosis ends
        this.huntingCooldown = 100; // 5 second hunting cooldown

        // Visual and audio effects
        if (level() instanceof ServerLevel serverLevel) {
            // Spiral particles around the target
            for (int i = 0; i < 20; i++) {
                double angle = (i * Math.PI * 2) / 20;
                double x = target.getX() + Math.cos(angle) * 2;
                double z = target.getZ() + Math.sin(angle) * 2;
                double y = target.getY() + target.getBbHeight() / 2;
                serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0, 0, 0, 0.1);
            }
        }

        // Play hypnosis sound
        this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 0.8F);
    }

    private void maintainHypnosis() {
        if (hypnosisTarget == null || !hypnosisTarget.isAlive()) {
            endHypnosis();
            return;
        }

        // Check if target is still in range
        if (this.distanceTo(hypnosisTarget) > HYPNOSIS_RANGE * 1.5) {
            endHypnosis();
            return;
        }

        // Create particle effects
        if (level() instanceof ServerLevel serverLevel && random.nextInt(5) == 0) {
            Vec3 targetPos = hypnosisTarget.position();
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    targetPos.x, targetPos.y + hypnosisTarget.getBbHeight() / 2, targetPos.z,
                    3, 0.5, 0.5, 0.5, 0.1);
        }

        // Make target move toward the Hypnovenator
        Vec3 hypnovenatorPos = this.position();
        Vec3 targetPos = hypnosisTarget.position();
        Vec3 direction = hypnovenatorPos.subtract(targetPos).normalize();

        // Apply movement to target (slow approach)
        double speed = 0.1;
        Vec3 newVelocity = direction.scale(speed);
        hypnosisTarget.setDeltaMovement(newVelocity.x, hypnosisTarget.getDeltaMovement().y, newVelocity.z);

        // Make target look at the Hypnovenator
        hypnosisTarget.lookAt(EntityAnchorArgument.Anchor.EYES, hypnovenatorPos);
    }

    private void endHypnosis() {
        if (hypnosisTarget != null) {
            // Release the target
            hypnosisTarget.setDeltaMovement(Vec3.ZERO);
            HypnovenatorEntity.this.setHypnosisAnimationState(null);
            LivingEntity formerHypnosisTarget = hypnosisTarget;
            HypnovenatorEntity.this.setTarget(formerHypnosisTarget);
        }
        this.hypnosisTarget = null;
        this.hypnosisTimer = 0;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.CAT_FOOD) || itemStack.is(ItemTags.WOLF_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.HYPNOVENATOR.get().create(serverLevel); // You can return a new HypnovenatorEntity here if you want breeding
    }

    public int getHypnosisAnimationTimeout() {
        return hypnosisAnimationTimeout;
    }

    public void setHypnosisAnimationTimeout(int hypnosisAnimationTimeout) {
        this.hypnosisAnimationTimeout = hypnosisAnimationTimeout;
    }

    public AnimationState getHypnosisAnimationState() {
        return hypnosisAnimationState;
    }

    public void setHypnosisAnimationState(AnimationState state) {
        this.hypnosisAnimationState = state;
    }

    private class HypnosisGoal extends Goal {
        private int chargeTime = 0;
        private static final int CHARGE_DURATION = 40; // 2 seconds to charge hypnosis

        public HypnosisGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Can only use if not on cooldown, no active hypnosis, and has a valid small prey target
            return hypnosisCooldown <= 0 &&
                    huntingCooldown <= 0 &&
                    hypnosisTarget == null &&
                    HypnovenatorEntity.this.getTarget() != null &&
                    HypnovenatorEntity.this.getTarget().isAlive() &&
                    HypnovenatorEntity.this.distanceTo(HypnovenatorEntity.this.getTarget()) <= HYPNOSIS_RANGE &&
                    isSmallPrey(HypnovenatorEntity.this.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return chargeTime < CHARGE_DURATION &&
                    HypnovenatorEntity.this.getTarget() != null &&
                    HypnovenatorEntity.this.getTarget().isAlive() &&
                    isSmallPrey(HypnovenatorEntity.this.getTarget());
        }

        @Override
        public void start() {
            chargeTime = 0;
            // Stop moving while charging
            HypnovenatorEntity.this.getNavigation().stop();
            HypnovenatorEntity.this.setHypnosisAnimationState(hypnosisAnimationState);
        }

        @Override
        public void tick() {
            LivingEntity target = HypnovenatorEntity.this.getTarget();
            if (target == null) return;

            // Look at the target while charging
            HypnovenatorEntity.this.getLookControl().setLookAt(target);

            chargeTime++;

            // Create charging particles (different color for small prey hunting)
            if (HypnovenatorEntity.this.level() instanceof ServerLevel serverLevel &&
                    HypnovenatorEntity.this.random.nextInt(3) == 0) {

                Vec3 eyePos = HypnovenatorEntity.this.getEyePosition();
                // Use different particles to indicate hunting mode
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        eyePos.x, eyePos.y, eyePos.z, 1, 0.2, 0.2, 0.2, 0.05);
            }

            // Play charging sound periodically (lower pitch for hunting)
            if (chargeTime % 20 == 0) {
                HypnovenatorEntity.this.playSound(SoundEvents.BEACON_AMBIENT, 0.5F, 1.2F);
            }
        }

        @Override
        public void stop() {
            if (chargeTime >= CHARGE_DURATION) {
                // Hypnosis is fully charged - activate it
                LivingEntity target = HypnovenatorEntity.this.getTarget();
                if (target != null && target.isAlive() && isSmallPrey(target)) {
                    startHypnosis(target);
                    HypnovenatorEntity.this.setTarget(null);
                }
            }
            chargeTime = 0;
            HypnovenatorEntity.this.setHypnosisAnimationState(null);
        }
    }
}