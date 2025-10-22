package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BearBreakBeeNestGoal;
import com.fungoussoup.ancienthorizons.entity.ai.FollowPlayerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.IntFunction;

public class BrownBearEntity extends Animal implements NeutralMob {

    // Data accessors for synching data between client and server
    private static final EntityDataAccessor<Integer> DATA_MAIN_GENE = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HIDDEN_GENE = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ANGER_TIME = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PLAYING = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_COWERING = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID persistentAngerTarget;
    private int ticksSinceLastHoneyEaten = 0;
    private int sleepTimer = 0;
    private int playTimer = 0;
    private int cowerTimer = 0;
    private int weaknessTimer = 0;
    private BlockPos lastPlayerPos;
    private Gene currentGene = Gene.NORMAL; // Make it non-final and properly named

    public BrownBearEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            Gene randomGene = Gene.getRandom(random);
            setMainGene(randomGene);
            setHiddenGene(Gene.getRandom(random));
            this.currentGene = Gene.getVariantFromGenes(getMainGene(), getHiddenGene());
        }
    }

    public Gene getGene() {
        if (this.currentGene == null) {
            this.currentGene = Gene.getVariantFromGenes(getMainGene(), getHiddenGene());
        }
        return this.currentGene;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MAIN_GENE, Gene.NORMAL.getId());
        builder.define(DATA_HIDDEN_GENE, Gene.NORMAL.getId());
        builder.define(DATA_SITTING, false);
        builder.define(DATA_ANGER_TIME, 0);
        builder.define(DATA_SLEEPING, false);
        builder.define(DATA_PLAYING, false);
        builder.define(DATA_COWERING, false);
    }

    public enum Gene implements StringRepresentable {
        NORMAL(0, "normal", false),
        LAZY(1, "lazy", false),
        WORRIED(2, "worried", false),
        PLAYFUL(3, "playful", false),
        WINNIETHEPOOH(4, "winnie_the_pooh", true),
        WEAK(5, "weak", true),
        AGGRESSIVE(6, "aggressive", false);

        public static final StringRepresentable.EnumCodec<BrownBearEntity.Gene> CODEC = StringRepresentable.fromEnum(BrownBearEntity.Gene::values);
        private static final IntFunction<BrownBearEntity.Gene> BY_ID = ByIdMap.continuous(BrownBearEntity.Gene::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private static final int MAX_GENE = 6;
        private final int id;
        private final String name;
        private final boolean isRecessive;

        Gene(int id, String name, boolean isRecessive) {
            this.id = id;
            this.name = name;
            this.isRecessive = isRecessive;
        }

        public int getId() {
            return this.id;
        }

        public String getSerializedName() {
            return this.name;
        }

        public boolean isRecessive() {
            return this.isRecessive;
        }

        static BrownBearEntity.Gene getVariantFromGenes(BrownBearEntity.Gene mainGene, BrownBearEntity.Gene hiddenGene) {
            if (mainGene.isRecessive()) {
                return mainGene == hiddenGene ? mainGene : NORMAL;
            } else {
                return mainGene;
            }
        }

        public static BrownBearEntity.Gene byId(int index) {
            return BY_ID.apply(index);
        }

        public static BrownBearEntity.Gene byName(String name) {
            return CODEC.byName(name, NORMAL);
        }

        public static BrownBearEntity.Gene getRandom(RandomSource random) {
            int i = random.nextInt(16);
            if (i == 0) {
                return LAZY;
            } else if (i == 1) {
                return WORRIED;
            } else if (i == 2) {
                return PLAYFUL;
            } else if (i == 4) {
                return AGGRESSIVE;
            } else if (i < 9) {
                return WEAK;
            } else {
                return i < 11 ? WINNIETHEPOOH : NORMAL;
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    // Apply gene-specific attribute modifiers
    private void applyGeneModifiers() {
        Gene currentGene = getCurrentGene();
        switch (currentGene) {
            case WEAK:
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
                break;
            case PLAYFUL:
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
                this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(25.0D);
                break;
            case WORRIED:
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
                this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30.0D);
                break;
            case AGGRESSIVE:
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8.0D);
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(35.0D);
                break;
            case LAZY:
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.15D);
                break;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BearAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new BearSitGoal(this));
        this.goalSelector.addGoal(5, new BearBreakBeeNestGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Conditional goals based on gene
        Gene currentGene = getCurrentGene();
        switch (currentGene) {
            case AGGRESSIVE:
                this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
                this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
                break;
            case LAZY:
                this.goalSelector.addGoal(1, new BearSleepGoal(this));
                break;
            case WORRIED:
                this.goalSelector.addGoal(1, new WorriedBearGoal(this));
                this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.6D, 1.4D));
                break;
            case PLAYFUL:
                this.goalSelector.addGoal(1, new PlayfulBearGoal(this));
                this.goalSelector.addGoal(2, new FollowPlayerGoal(this, 1.2D, 3.0F, 15.0F));
                break;
            case WEAK:
                this.goalSelector.addGoal(1, new WeakBearGoal(this));
                break;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);

        // Refresh gene cache if gene data changed
        if (DATA_MAIN_GENE.equals(dataAccessor) || DATA_HIDDEN_GENE.equals(dataAccessor)) {
            this.currentGene = null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Handle anger timer
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

        // Handle honey effects
        if (ticksSinceLastHoneyEaten > 0) {
            ticksSinceLastHoneyEaten--;
            if (ticksSinceLastHoneyEaten <= 0) {
                this.removeEffect(net.minecraft.world.effect.MobEffects.REGENERATION);
            }
        }

        // Handle gene-specific behaviors
        Gene currentGene = getCurrentGene();
        if (!this.level().isClientSide) {
            switch (currentGene) {
                case LAZY:
                    handleSleeping();
                    break;
                case WORRIED:
                    handleWorried();
                    break;
                case PLAYFUL:
                    handlePlayful();
                    break;
                case WEAK:
                    handleWeakness();
                    break;
            }
        }
    }

    private void handleSleeping() {
        if (this.level().isNight() && !this.isSleeping()) {
            sleepTimer++;
            if (sleepTimer > 100) { // 5 seconds
                this.setSleeping(true);
                this.setDeltaMovement(0, 0, 0);
            }
        } else if (this.level().isDay() && this.isSleeping()) {
            sleepTimer = 0;
            this.setSleeping(false);
        }
    }

    private void handleWorried() {
        Player nearestPlayer = this.level().getNearestPlayer(this, 10.0D);

        if (nearestPlayer != null) {
            // Check for sudden movements or loud sounds
            if (this.lastPlayerPos != null) {
                double playerMovement = nearestPlayer.position().distanceTo(
                        net.minecraft.world.phys.Vec3.atCenterOf(this.lastPlayerPos));

                if (playerMovement > 2.0D || nearestPlayer.isSprinting()) {
                    this.startCowering();
                }
            }
            this.lastPlayerPos = nearestPlayer.blockPosition();
        }

        // Random anxiety episodes
        if (this.random.nextInt(1200) == 0) { // Every minute on average
            this.startCowering();
        }

        // Handle cowering state
        if (this.isCowering()) {
            cowerTimer--;
            if (cowerTimer <= 0) {
                this.setCowering(false);
            }
        }
    }

    private void handlePlayful() {
        Player nearestPlayer = this.level().getNearestPlayer(this, 8.0D);

        if (nearestPlayer != null && !this.isAngry()) {
            if (this.random.nextInt(200) == 0) { // Random play initiation
                this.setPlaying(true);
                this.playTimer = 100 + this.random.nextInt(100); // 5-10 seconds

                // Jump around playfully
                if (this.onGround()) {
                    this.setDeltaMovement(
                            this.getDeltaMovement().x + (this.random.nextDouble() - 0.5D) * 0.5D,
                            0.42D,
                            this.getDeltaMovement().z + (this.random.nextDouble() - 0.5D) * 0.5D
                    );
                }
            }
        }

        if (this.isPlaying()) {
            playTimer--;
            if (playTimer <= 0) {
                this.setPlaying(false);
            }
        }
    }

    private void handleWeakness() {
        // Weak bears tire more easily
        if (this.isMoving() && this.random.nextInt(300) == 0) {
            this.weaknessTimer = 60; // 3 seconds of slowness
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        }

        // Weak bears are more susceptible to weather
        if (this.isInWaterOrRain() && this.random.nextInt(200) == 0) {
            this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.WEAKNESS, 100, 0));
        }

        if (weaknessTimer > 0) {
            weaknessTimer--;
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.01D;
    }

    private void startCowering() {
        this.setCowering(true);
        this.cowerTimer = 80 + this.random.nextInt(40); // 4-6 seconds
        this.getNavigation().stop();
        this.setDeltaMovement(0, 0, 0);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Worried bears might flee from interaction
        if (getCurrentGene() == Gene.WORRIED) {
            if (this.random.nextInt(3) == 0) {
                this.startCowering();
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        // Playful bears are more receptive to interaction
        if (getCurrentGene() == Gene.PLAYFUL && itemStack.isEmpty()) {
            if (!this.level().isClientSide) {
                this.setPlaying(true);
                this.playTimer = 100;
                // Small chance to drop an item as a "gift"
                if (this.random.nextInt(10) == 0) {
                    this.spawnAtLocation(Items.STICK);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Weak bears are more grateful for food
        if (getCurrentGene() == Gene.WEAK && this.isFood(itemStack)) {
            if (!this.level().isClientSide) {
                this.heal(6.0F); // Extra healing for weak bears
                this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 600, 0));
            }
        }

        if (this.isFood(itemStack)) {
            if (!this.level().isClientSide) {
                this.heal(4.0F);
                this.ticksSinceLastHoneyEaten = 600; // 30 seconds
                this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.REGENERATION, 200, 0));

                if (getCurrentGene() == Gene.WINNIETHEPOOH) {
                    // Winnie the Pooh bears love honey even more
                    this.heal(2.0F);
                    this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 400, 0));
                }

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (itemStack.isEmpty() && !this.isAngry()) {
            if (!this.level().isClientSide) {
                this.setSitting(!this.isSitting());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.HONEY_BLOCK) ||
                itemStack.is(Items.HONEYCOMB) ||
                itemStack.is(Items.HONEY_BOTTLE) ||
                itemStack.is(Items.HONEYCOMB_BLOCK) ||
                itemStack.is(Items.SWEET_BERRIES);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        BrownBearEntity baby = ModEntities.BROWN_BEAR.get().create(serverLevel);
        if (baby != null && ageableMob instanceof BrownBearEntity otherParent) {
            // Genetic inheritance
            Gene motherMainGene = this.getMainGene();
            Gene motherHiddenGene = this.getHiddenGene();
            Gene fatherMainGene = otherParent.getMainGene();
            Gene fatherHiddenGene = otherParent.getHiddenGene();

            // Random selection of genes from parents
            Gene babyGene1 = serverLevel.getRandom().nextBoolean() ? motherMainGene : motherHiddenGene;
            Gene babyGene2 = serverLevel.getRandom().nextBoolean() ? fatherMainGene : fatherHiddenGene;

            baby.setMainGene(babyGene1);
            baby.setHiddenGene(babyGene2);
        }
        return baby;
    }

    // Gene getters and setters
    public Gene getMainGene() {
        return Gene.byId(this.entityData.get(DATA_MAIN_GENE));
    }

    public void setMainGene(Gene gene) {
        this.entityData.set(DATA_MAIN_GENE, gene.getId());
        this.currentGene = null; // Reset cache
        this.applyGeneModifiers();
    }

    public void setHiddenGene(Gene gene) {
        this.entityData.set(DATA_HIDDEN_GENE, gene.getId());
        this.currentGene = null; // Reset cache
        this.applyGeneModifiers();
    }

    public Gene getHiddenGene() {
        return Gene.byId(this.entityData.get(DATA_HIDDEN_GENE));
    }

    public Gene getCurrentGene() {
        return Gene.getVariantFromGenes(getMainGene(), getHiddenGene());
    }

    // Sitting behavior
    public boolean isSitting() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
    }

    // Sleeping behavior
    public boolean isSleeping() {
        return this.entityData.get(DATA_SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(DATA_SLEEPING, sleeping);
    }

    // Playing behavior
    public boolean isPlaying() {
        return this.entityData.get(DATA_PLAYING);
    }

    public void setPlaying(boolean playing) {
        this.entityData.set(DATA_PLAYING, playing);
    }

    // Cowering behavior
    public boolean isCowering() {
        return this.entityData.get(DATA_COWERING);
    }

    public void setCowering(boolean cowering) {
        this.entityData.set(DATA_COWERING, cowering);
    }

    // NeutralMob implementation
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.entityData.set(DATA_ANGER_TIME, angerTime);
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(400 + this.random.nextInt(400));
    }

    // Sound events
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        Gene gene = getGene();
        setMainGene(gene);
        setHiddenGene(gene);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    // NBT save/load
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MainGene", this.getMainGene().getId());
        tag.putInt("HiddenGene", this.getHiddenGene().getId());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putBoolean("Sleeping", this.isSleeping());
        tag.putBoolean("Playing", this.isPlaying());
        tag.putBoolean("Cowering", this.isCowering());
        tag.putInt("AngerTime", this.getRemainingPersistentAngerTime());
        tag.putInt("PlayTimer", this.playTimer);
        tag.putInt("CowerTimer", this.cowerTimer);
        tag.putInt("WeaknessTimer", this.weaknessTimer);
        if (this.persistentAngerTarget != null) {
            tag.putUUID("AngerTarget", this.persistentAngerTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setMainGene(Gene.byId(tag.getInt("MainGene")));
        this.setHiddenGene(Gene.byId(tag.getInt("HiddenGene")));
        this.setSitting(tag.getBoolean("Sitting"));
        this.setSleeping(tag.getBoolean("Sleeping"));
        this.setPlaying(tag.getBoolean("Playing"));
        this.setCowering(tag.getBoolean("Cowering"));
        this.setRemainingPersistentAngerTime(tag.getInt("AngerTime"));
        this.playTimer = tag.getInt("PlayTimer");
        this.cowerTimer = tag.getInt("CowerTimer");
        this.weaknessTimer = tag.getInt("WeaknessTimer");
        if (tag.hasUUID("AngerTarget")) {
            this.persistentAngerTarget = tag.getUUID("AngerTarget");
        }
        // Refresh gene cache after loading data
        this.currentGene = null;
        this.applyGeneModifiers();
    }

    static class BearAttackGoal extends MeleeAttackGoal {
        public BearAttackGoal(BrownBearEntity bear, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(bear, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !((BrownBearEntity)this.mob).isSitting();
        }
    }

    static class BearSitGoal extends Goal {
        private final BrownBearEntity bear;

        public BearSitGoal(BrownBearEntity bear) {
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.isSitting();
        }

        @Override
        public void start() {
            this.bear.getNavigation().stop();
            this.bear.setDeltaMovement(0, 0, 0);
        }
    }

    static class BearSleepGoal extends Goal {
        private final BrownBearEntity bear;

        public BearSleepGoal(BrownBearEntity bear) {
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.isSleeping();
        }

        @Override
        public void start() {
            this.bear.getNavigation().stop();
            this.bear.setDeltaMovement(0, 0, 0);
        }
    }

    static class WorriedBearGoal extends Goal {
        private final BrownBearEntity bear;
        private BlockPos hideSpot;
        private int hideTicks;
        private int scanTicks;
        private LivingEntity lastThreat;

        public WorriedBearGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return bear.getCurrentGene() == BrownBearEntity.Gene.WORRIED &&
                    (bear.isCowering() || hasNearbyThreats() || bear.getRandom().nextInt(800) == 0);
        }

        @Override
        public boolean canContinueToUse() {
            return bear.isCowering() || hasNearbyThreats() || (hideSpot != null && hideTicks > 0);
        }

        @Override
        public void start() {
            if (bear.isCowering()) {
                hideSpot = findHideSpot();
                bear.getNavigation().moveTo(hideSpot.getX(), hideSpot.getY(), hideSpot.getZ(), 1.4D);
                hideTicks = 100 + bear.getRandom().nextInt(100);
            }
        }

        @Override
        public void tick() {
            // Constantly scan for threats
            scanTicks++;
            if (scanTicks > 20) {
                scanForThreats();
                scanTicks = 0;
            }

            // If hiding, stay put and reduce timer
            if (hideSpot != null && bear.distanceToSqr(Vec3.atCenterOf(hideSpot)) < 4.0D) {
                bear.getNavigation().stop();
                bear.getLookControl().setLookAt(Vec3.atCenterOf(hideSpot));
                hideTicks--;

                // Peek out occasionally
                if (bear.getRandom().nextInt(60) == 0) {
                    bear.getLookControl().setLookAt(bear.getX() + bear.getRandom().nextGaussian() * 5,
                            bear.getY(),
                            bear.getZ() + bear.getRandom().nextGaussian() * 5);
                }
            }

            // Random anxiety episodes
            if (bear.getRandom().nextInt(400) == 0) {
                bear.startCowering();
            }
        }

        @Override
        public void stop() {
            hideSpot = null;
            hideTicks = 0;
            lastThreat = null;
        }

        private boolean hasNearbyThreats() {
            List<LivingEntity> nearbyEntities = bear.level().getEntitiesOfClass(LivingEntity.class,
                    bear.getBoundingBox().inflate(12.0D));

            for (LivingEntity entity : nearbyEntities) {
                if (entity instanceof Player player && player.isSprinting()) return true;
                if (entity instanceof Monster) return true;
                if (entity.isOnFire()) return true;
            }
            return false;
        }

        private void scanForThreats() {
            LivingEntity threat = bear.level().getNearestEntity(LivingEntity.class,
                    TargetingConditions.forNonCombat().range(15.0D),
                    bear, bear.getX(), bear.getY(), bear.getZ(),
                    bear.getBoundingBox().inflate(15.0D));

            if (threat != null && threat != bear && isThreatening(threat)) {
                lastThreat = threat;
                if (bear.getRandom().nextInt(3) == 0) {
                    bear.startCowering();
                }
            }
        }

        private boolean isThreatening(LivingEntity entity) {
            if (entity instanceof Player player) {
                return player.isSprinting() || player.isInLava() || !player.getMainHandItem().isEmpty();
            }
            return entity instanceof Monster || entity.isOnFire();
        }

        private BlockPos findHideSpot() {
            BlockPos bearPos = bear.blockPosition();

            // Look for spots with overhead cover
            for (int attempt = 0; attempt < 10; attempt++) {
                int x = bearPos.getX() + bear.getRandom().nextInt(16) - 8;
                int y = bearPos.getY() + bear.getRandom().nextInt(6) - 3;
                int z = bearPos.getZ() + bear.getRandom().nextInt(16) - 8;
                BlockPos pos = new BlockPos(x, y, z);

                if (bear.level().getBlockState(pos).isAir() &&
                        bear.level().getBlockState(pos.below()).isSolid() &&
                        !bear.level().getBlockState(pos.above()).isAir()) {
                    return pos;
                }
            }

            // Fallback to any solid spot
            return bearPos.offset(bear.getRandom().nextInt(10) - 5, 0, bear.getRandom().nextInt(10) - 5);
        }

        public LivingEntity getLastThreat() {
            return lastThreat;
        }

        public void setLastThreat(LivingEntity lastThreat) {
            this.lastThreat = lastThreat;
        }
    }

    static class PlayfulBearGoal extends Goal {
        private final BrownBearEntity bear;
        private Player targetPlayer;
        private int playTicks;
        private int playType;
        private Vec3 playCenter;
        private int circleDirection;
        private int jumpCooldown;
        private int giftCooldown;
        private BlockPos lastGiftPos;

        private static final int PLAY_CHASE = 0;
        private static final int PLAY_CIRCLE = 1;
        private static final int PLAY_JUMP = 2;
        private static final int PLAY_HIDE_SEEK = 3;

        public PlayfulBearGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bear.getCurrentGene() != BrownBearEntity.Gene.PLAYFUL) return false;
            if (bear.isAngry() || bear.isBaby()) return false;

            Player nearestPlayer = this.bear.level().getNearestPlayer(this.bear, 10.0D);
            if (nearestPlayer == null) return false;

            // Higher chance to play if player is crouching (friendly gesture)
            int playChance = nearestPlayer.isCrouching() ? 150 : 300;

            if (this.bear.getRandom().nextInt(playChance) == 0) {
                this.targetPlayer = nearestPlayer;
                return true;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.targetPlayer == null || !this.targetPlayer.isAlive()) return false;
            if (this.bear.distanceToSqr(this.targetPlayer) > 144.0D) return false; // 12 blocks
            if (this.bear.isAngry()) return false;

            return this.playTicks > 0;
        }

        @Override
        public void start() {
            this.playTicks = 200 + this.bear.getRandom().nextInt(200); // 10-20 seconds
            this.playType = this.bear.getRandom().nextInt(4);
            this.circleDirection = this.bear.getRandom().nextBoolean() ? 1 : -1;
            this.jumpCooldown = 0;
            this.giftCooldown = 400 + this.bear.getRandom().nextInt(600);
            this.bear.setPlaying(true);

            if (this.playType == PLAY_CIRCLE) {
                this.playCenter = this.targetPlayer.position();
            }
        }

        @Override
        public void tick() {
            this.playTicks--;
            this.jumpCooldown--;
            this.giftCooldown--;

            if (this.targetPlayer != null) {
                this.bear.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float) this.bear.getMaxHeadXRot());

                switch (this.playType) {
                    case PLAY_CHASE:
                        playChase();
                        break;
                    case PLAY_CIRCLE:
                        playCircle();
                        break;
                    case PLAY_JUMP:
                        playJump();
                        break;
                    case PLAY_HIDE_SEEK:
                        playHideSeek();
                        break;
                }

                // Occasionally give gifts
                if (this.giftCooldown <= 0 && this.bear.distanceToSqr(this.targetPlayer) < 9.0D) {
                    giveGift();
                    this.giftCooldown = 800 + this.bear.getRandom().nextInt(1200);
                }

                // Random playful sounds
                if (this.bear.getRandom().nextInt(60) == 0) {
                    this.bear.playSound(SoundEvents.POLAR_BEAR_AMBIENT_BABY, 0.8F, 1.2F);
                }
            }
        }

        @Override
        public void stop() {
            this.bear.setPlaying(false);
            this.targetPlayer = null;
            this.bear.getNavigation().stop();
        }

        private void playChase() {
            double distance = this.bear.distanceToSqr(this.targetPlayer);

            if (distance > 16.0D) {
                // Get closer
                this.bear.getNavigation().moveTo(this.targetPlayer, 1.3D);
            } else if (distance < 4.0D) {
                // Too close, back away playfully
                Vec3 awayVector = this.bear.position().subtract(this.targetPlayer.position()).normalize();
                Vec3 targetPos = this.bear.position().add(awayVector.scale(3.0D));
                this.bear.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.1D);

                // Playful jump away
                if (this.jumpCooldown <= 0 && this.bear.onGround()) {
                    this.bear.setDeltaMovement(
                            awayVector.x * 0.3D,
                            0.4D,
                            awayVector.z * 0.3D
                    );
                    this.jumpCooldown = 40;
                }
            } else {
                // Perfect distance, move in playful patterns
                if (this.bear.getRandom().nextInt(40) == 0) {
                    Vec3 sideVector = this.bear.position().subtract(this.targetPlayer.position()).cross(new Vec3(0, 1, 0)).normalize();
                    Vec3 targetPos = this.bear.position().add(sideVector.scale(2.0D));
                    this.bear.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.2D);
                }
            }
        }

        private void playCircle() {
            if (this.playCenter != null) {
                double angle = (this.bear.tickCount * 0.1D) * this.circleDirection;
                double radius = 4.0D;

                double targetX = this.playCenter.x + Math.cos(angle) * radius;
                double targetZ = this.playCenter.z + Math.sin(angle) * radius;

                this.bear.getNavigation().moveTo(targetX, this.playCenter.y, targetZ, 1.1D);

                // Update center occasionally to follow player
                if (this.bear.tickCount % 60 == 0) {
                    this.playCenter = this.targetPlayer.position();
                }
            }
        }

        private void playJump() {
            if (this.jumpCooldown <= 0 && this.bear.onGround()) {
                // Jump towards or around the player
                Vec3 direction = this.targetPlayer.position().subtract(this.bear.position()).normalize();

                // Add some randomness to the jump direction
                direction = direction.add(
                        (this.bear.getRandom().nextDouble() - 0.5D) * 0.8D,
                        0,
                        (this.bear.getRandom().nextDouble() - 0.5D) * 0.8D
                ).normalize();

                this.bear.setDeltaMovement(
                        direction.x * 0.4D,
                        0.5D,
                        direction.z * 0.4D
                );

                this.jumpCooldown = 30 + this.bear.getRandom().nextInt(50);
            }

            // Move towards player when not jumping
            if (this.bear.onGround() && this.bear.distanceToSqr(this.targetPlayer) > 9.0D) {
                this.bear.getNavigation().moveTo(this.targetPlayer, 1.0D);
            }
        }

        private void playHideSeek() {
            // Find a spot to "hide" behind
            if (this.bear.getRandom().nextInt(80) == 0) {
                BlockPos bearPos = this.bear.blockPosition();
                BlockPos hidePos = null;

                for (int i = 0; i < 10; i++) {
                    BlockPos testPos = bearPos.offset(
                            this.bear.getRandom().nextInt(7) - 3,
                            0,
                            this.bear.getRandom().nextInt(7) - 3
                    );

                    if (this.bear.level().getBlockState(testPos).isSolid() &&
                            this.bear.level().getBlockState(testPos.above()).isAir()) {
                        hidePos = testPos.above();
                        break;
                    }
                }

                if (hidePos != null) {
                    this.bear.getNavigation().moveTo(hidePos.getX(), hidePos.getY(), hidePos.getZ(), 1.2D);
                }
            }
        }

        private void giveGift() {
            if (this.bear.blockPosition().equals(this.lastGiftPos)) {
                return; // Don't spam gifts in the same spot
            }

            ItemStack gift = getRandomGift();
            if (gift != null) {
                this.bear.spawnAtLocation(gift);
                this.lastGiftPos = this.bear.blockPosition();

                // Special sound for giving gift
                this.bear.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.5F);
            }
        }

        private ItemStack getRandomGift() {
            RandomSource random = this.bear.getRandom();
            int giftType = random.nextInt(10);

            return switch (giftType) {
                case 0, 1, 2 -> new ItemStack(Items.STICK, 1 + random.nextInt(3));
                case 3 -> new ItemStack(Items.STONE, 1 + random.nextInt(2));
                case 4 -> new ItemStack(Items.DIRT, 1);
                case 5 -> new ItemStack(Items.DANDELION, 1);
                case 6 -> new ItemStack(Items.POPPY, 1);
                case 7 -> new ItemStack(Items.WHEAT_SEEDS, 1 + random.nextInt(3));
                case 8 -> new ItemStack(Items.FEATHER, 1);
                case 9 -> new ItemStack(Items.BONE, 1);
                default -> null;
            };
        }
    }

    static class WeakBearGoal extends Goal {
        private final BrownBearEntity bear;
        private BlockPos restSpot;
        private int restTicks;
        private int exhaustionLevel;
        private int weatherSickTimer;
        private boolean isResting;
        private int activityPoints;
        private static final int MAX_ACTIVITY = 100;

        public WeakBearGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bear.getCurrentGene() != BrownBearEntity.Gene.WEAK) return false;

            updateExhaustion();

            // Need rest if exhausted, sick, or low health
            return this.exhaustionLevel > 70 ||
                    this.bear.getHealth() < this.bear.getMaxHealth() * 0.4F ||
                    this.weatherSickTimer > 0 ||
                    shouldSeekShelter();
        }

        @Override
        public boolean canContinueToUse() {
            return this.exhaustionLevel > 20 ||
                    this.bear.getHealth() < this.bear.getMaxHealth() * 0.6F ||
                    this.weatherSickTimer > 0 ||
                    this.isResting;
        }

        @Override
        public void start() {
            this.restTicks = 0;
            this.isResting = false;
            findRestSpot();
        }

        @Override
        public void tick() {
            updateExhaustion();

            if (this.restSpot != null) {
                double distanceToRest = this.bear.distanceToSqr(Vec3.atCenterOf(this.restSpot));

                if (distanceToRest > 4.0D) {
                    // Move to rest spot slowly
                    this.bear.getNavigation().moveTo(this.restSpot.getX(), this.restSpot.getY(), this.restSpot.getZ(), 0.8D);
                    this.activityPoints = Math.min(MAX_ACTIVITY, this.activityPoints + 2);
                } else {
                    // At rest spot, start resting
                    this.isResting = true;
                    this.bear.getNavigation().stop();
                    this.bear.setSitting(true);
                    this.restTicks++;

                    // Slow recovery while resting
                    if (this.restTicks % 20 == 0) {
                        this.exhaustionLevel = Math.max(0, this.exhaustionLevel - 8);
                        this.bear.heal(0.5F);
                        this.activityPoints = Math.max(0, this.activityPoints - 5);
                    }

                    // Weather protection
                    if (this.weatherSickTimer > 0) {
                        this.weatherSickTimer--;
                        if (this.restTicks % 40 == 0) {
                            this.bear.heal(1.0F);
                        }
                    }

                    // Tired breathing effect
                    if (this.restTicks % 30 == 0) {
                        this.bear.playSound(SoundEvents.POLAR_BEAR_AMBIENT, 0.3F, 0.8F);
                    }
                }
            } else {
                // No rest spot, rest in place
                this.isResting = true;
                this.bear.setSitting(true);
                this.bear.getNavigation().stop();
                this.restTicks++;

                if (this.restTicks % 30 == 0) {
                    this.exhaustionLevel = Math.max(0, this.exhaustionLevel - 3);
                    this.bear.heal(0.25F);
                }
            }

            // Check if bear should continue or can stop resting
            if (this.restTicks > 100 && this.exhaustionLevel < 30) {
                // Well rested, but weak bears tire quickly
                if (this.bear.getRandom().nextInt(200) == 0) {
                    this.exhaustionLevel = Math.min(100, this.exhaustionLevel + 20);
                }
            }
        }

        @Override
        public void stop() {
            this.bear.setSitting(false);
            this.isResting = false;
            this.restSpot = null;
            this.restTicks = 0;
        }

        private void updateExhaustion() {
            // Movement exhaustion
            if (this.bear.getDeltaMovement().lengthSqr() > 0.01D) {
                this.activityPoints = Math.min(MAX_ACTIVITY, this.activityPoints + 1);
            }

            // Convert activity to exhaustion
            if (this.activityPoints >= MAX_ACTIVITY) {
                this.exhaustionLevel = Math.min(100, this.exhaustionLevel + 15);
                this.activityPoints = 0;
            }

            // Weather effects
            if (this.bear.isInWaterOrRain()) {
                if (this.bear.getRandom().nextInt(100) == 0) {
                    this.weatherSickTimer = 400 + this.bear.getRandom().nextInt(400);
                    this.bear.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
                }
            }

            // Combat exhaustion
            if (this.bear.getTarget() != null) {
                this.exhaustionLevel = Math.min(100, this.exhaustionLevel + 5);
            }

            // Natural recovery (very slow)
            if (this.bear.getRandom().nextInt(200) == 0 && !this.bear.isMoving()) {
                this.exhaustionLevel = Math.max(0, this.exhaustionLevel - 1);
            }
        }

        private boolean shouldSeekShelter() {
            return this.bear.level().isThundering() ||
                    (this.bear.level().isRaining() && this.bear.getHealth() < this.bear.getMaxHealth() * 0.7F);
        }

        private void findRestSpot() {
            BlockPos bearPos = this.bear.blockPosition();
            BlockPos bestSpot = null;
            double bestScore = Double.MAX_VALUE;

            for (BlockPos pos : BlockPos.betweenClosed(bearPos.offset(-8, -2, -8), bearPos.offset(8, 2, 8))) {
                if (isValidRestSpot(pos)) {
                    double score = calculateRestSpotScore(pos);
                    if (score < bestScore) {
                        bestScore = score;
                        bestSpot = pos.immutable();
                    }
                }
            }

            this.restSpot = bestSpot;
        }

        private boolean isValidRestSpot(BlockPos pos) {
            Level level = this.bear.level();

            // Need air blocks for bear to sit
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                return false;
            }

            // Need solid ground
            return level.getBlockState(pos.below()).isSolid();
        }

        private double calculateRestSpotScore(@NotNull BlockPos pos) {
            double score = 0;
            Level level = this.bear.level();

            // Prefer sheltered spots
            if (level.getBlockState(pos.above()).isSolid()) {
                score -= 20;
            }

            // Prefer spots with walls nearby
            int wallCount = 0;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (level.getBlockState(pos.relative(dir)).isSolid()) {
                    wallCount++;
                }
            }
            score -= wallCount * 5;

            // Prefer closer spots (distance penalty)
            double distance = this.bear.distanceToSqr(Vec3.atCenterOf(pos));
            score += distance * 0.5;

            // Avoid water
            if (level.getBlockState(pos).getFluidState().isSource()) {
                score += 100;
            }

            // Prefer darker spots (less exposed)
            int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
            score += lightLevel * 2;

            return score;
        }
    }
}