package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.ModFollowOwnerGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.DancingAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Objects;

import static com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity.MovementState.FLYING;

public abstract class AbstractPasserineEntity extends ShoulderRidingEntity
        implements SemiFlyer, DancingAnimal {

    /* ---------- STATE ---------- */

    public enum MovementState {
        GROUNDED,
        HOPPING,
        FLYING,
        RESTING
    }

    private MovementState movementState = MovementState.GROUNDED;

    /* ---------- SYNCED DATA ---------- */

    private static final EntityDataAccessor<Boolean> DANCING =
            SynchedEntityData.defineId(AbstractPasserineEntity.class, EntityDataSerializers.BOOLEAN);

    /* ---------- NAVIGATION ---------- */

    protected final PathNavigation groundNavigation;
    protected final PathNavigation flyingNavigation;

    /* ---------- TIMERS ---------- */

    private int hopTicks;
    private int hopDuration;
    private int hopCooldown;

    private int flapTicks;
    private int restTicks;
    private int chirpCooldown;

    private static final int MAX_REST_TIME = 300;
    private static final int CHIRP_COOLDOWN = 100;

    /* ---------- DANCING ---------- */

    public float prevDanceProgress;
    public float danceProgress;
    private BlockPos jukeboxPos;

    /* ---------- CONSTRUCTOR ---------- */

    protected AbstractPasserineEntity(EntityType<? extends ShoulderRidingEntity> type, Level level) {
        super(type, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
        this.jumpControl = new PasserineJumpController(this);
    }

    /* ---------- ATTRIBUTES ---------- */

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    /* ---------- DATA ---------- */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DANCING, false);
    }

    /* ---------- GOALS ---------- */

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new PanicGoal(this, 1.4));
        goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        goalSelector.addGoal(1, new LandOnOwnersShoulderGoal(this));
        goalSelector.addGoal(2, new TemptGoal(this, 1.25, s -> s.is(ModTags.Items.PASSERINE_FOOD), true));
        goalSelector.addGoal(2, new ModFollowOwnerGoal(this, 1.25, 10, 1));
        goalSelector.addGoal(3, new SemiFlyingFlyGoal(this, 1.0));
        goalSelector.addGoal(4, new PasserineHopGoal(this));
        goalSelector.addGoal(5, new PasserineRestGoal(this));
        goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0,
                new NearestAttackableTargetGoal<>(this, EarthwormEntity.class, false));
    }

    /* ---------- TICK ---------- */

    @Override
    public void tick() {
        super.tick();

        switch (movementState) {
            case FLYING -> tickFlying();
            case HOPPING -> tickHopping();
            case GROUNDED -> tickGrounded();
            case RESTING -> tickResting();
        }

        tickDancing();
        tickSounds();
    }

    /* ---------- STATE MACHINE ---------- */

    private void setMovementState(MovementState next) {
        if (movementState == next) return;

        movementState = next;

        switch (next) {
            case GROUNDED -> {
                setNoGravity(false);
                navigation = groundNavigation;
                moveControl = new MoveControl(this);
            }
            case FLYING -> {
                setNoGravity(true);
                navigation = flyingNavigation;
                moveControl = new SemiFlyingMoveControl(this, 10, 9);
                flapTicks = 0;
            }
            case HOPPING -> {
                setNoGravity(false);
                hopTicks = 0;
                hopDuration = 8 + random.nextInt(4);
            }
            case RESTING -> navigation.stop();
        }
    }

    /* ---------- MOVEMENT ---------- */

    private void tickFlying() {
        flapTicks++;
        resetFallDistance();

        if (onGround()) {
            setMovementState(MovementState.GROUNDED);
        }
    }

    private void tickHopping() {
        hopTicks++;
        float p = (float) hopTicks / hopDuration;
        float y = 4f * p * (1f - p) * 0.15f;

        setDeltaMovement(getDeltaMovement().x, y, getDeltaMovement().z);

        if (hopTicks >= hopDuration) {
            hopCooldown = 20 + random.nextInt(40);
            setMovementState(MovementState.GROUNDED);
        }
    }

    private void tickGrounded() {
        if (hopCooldown > 0) hopCooldown--;

        if (!onGround() && !isPassenger()) {
            setMovementState(FLYING);
        }
    }

    private void tickResting() {
        restTicks++;
        if (restTicks > MAX_REST_TIME) {
            restTicks = 0;
            setMovementState(MovementState.GROUNDED);
        }
    }

    /* ---------- DANCING ---------- */

    private void tickDancing() {
        prevDanceProgress = danceProgress;
        boolean dancing = isDancing();

        if (dancing && danceProgress < 5) danceProgress++;
        if (!dancing && danceProgress > 0) danceProgress--;
    }

    /* ---------- SOUNDS ---------- */

    private void tickSounds() {
        if (chirpCooldown > 0) chirpCooldown--;
        if (chirpCooldown == 0 && random.nextFloat() < 0.005f) {
            playSound(Objects.requireNonNull(getAmbientSound()), 0.6f, 1.0f);
            chirpCooldown = CHIRP_COOLDOWN;
        }
    }

    /* ---------- AI REQUEST METHODS ---------- */

    public void requestFly() {
        if (movementState == MovementState.GROUNDED && !isPassenger()) {
            setMovementState(FLYING);
        }
    }

    public void requestHop() {
        if (movementState == MovementState.GROUNDED && hopCooldown == 0) {
            setMovementState(MovementState.HOPPING);
        }
    }

    public void requestRest() {
        if (movementState == MovementState.GROUNDED) {
            restTicks = 0;
            setMovementState(MovementState.RESTING);
        }
    }

    /* ---------- GOALS ---------- */

    static class PasserineHopGoal extends Goal {
        private final AbstractPasserineEntity bird;

        PasserineHopGoal(AbstractPasserineEntity bird) {
            this.bird = bird;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return bird.movementState == MovementState.GROUNDED
                    && bird.hopCooldown == 0
                    && bird.onGround()
                    && bird.random.nextFloat() < 0.02f;
        }

        @Override
        public void start() {
            bird.requestHop();
        }
    }

    static class PasserineRestGoal extends Goal {
        private final AbstractPasserineEntity bird;

        PasserineRestGoal(AbstractPasserineEntity bird) {
            this.bird = bird;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return bird.movementState == MovementState.GROUNDED
                    && bird.random.nextFloat() < 0.001f;
        }

        @Override
        public void start() {
            bird.requestRest();
        }
    }

    /* ---------- JUMP ---------- */

    static class PasserineJumpController extends JumpControl {
        private final AbstractPasserineEntity bird;

        PasserineJumpController(AbstractPasserineEntity bird) {
            super(bird);
            this.bird = bird;
        }

        @Override
        public void tick() {
            if (jump && bird.onGround()) {
                bird.requestHop();
                jump = false;
            }
        }
    }

    /* ---------- INTERACTION ---------- */

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModTags.Items.PASSERINE_FOOD) && !isTame()) {
            if (!player.getAbilities().instabuild) stack.shrink(1);

            if (random.nextBoolean()) {
                tame(player);
                navigation.stop();
                setOrderedToSit(true);
                level().broadcastEntityEvent(this, (byte) 7);
            } else {
                level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    /* ---------- SOUNDS ---------- */

    @Override protected SoundEvent getAmbientSound() { return ModSoundEvents.PASSERINE_CHIRP; }
    @Override protected SoundEvent getHurtSound(DamageSource d) { return SoundEvents.PARROT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PARROT_DEATH; }

    /* ---------- DANCING ---------- */

    public boolean isDancing() {
        return entityData.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        entityData.set(DANCING, dancing);
    }

    @Override
    public void setJukeboxPos(BlockPos pos) {
        jukeboxPos = pos;
    }

    @Override
    public void startFlying() {
        // Public API → request, never force
        if (!canFly()) return;
        requestFly();
    }

    @Override
    public void stopFlying() {
        // Only stop if actually flying
        if (movementState == MovementState.FLYING) {
            setMovementState(MovementState.GROUNDED);
        }
    }

    @Override
    public boolean isFlying() {
        return movementState == MovementState.FLYING;
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    @Override
    public void setFlying(boolean flying) {
        // NEVER toggle blindly — this is where deadlocks are born
        if (flying) {
            startFlying();
        } else {
            stopFlying();
        }
    }

}
