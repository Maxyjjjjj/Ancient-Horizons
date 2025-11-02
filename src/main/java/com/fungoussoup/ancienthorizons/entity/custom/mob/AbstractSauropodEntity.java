package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.gui.SauropodHarnessContainer;
import com.fungoussoup.ancienthorizons.registry.ModDamageTypes;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractSauropodEntity extends TamableAnimal implements PlayerRideable, Saddleable {

    // Data Trackers
    private static final EntityDataAccessor<Boolean> DATA_HARNESSED = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_GRAZING = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_GROWTH_STAGE = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_NECK_ROTATION = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_HARNESS_WEAPON = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WEAPON_COOLDOWN = SynchedEntityData.defineId(AbstractSauropodEntity.class, EntityDataSerializers.INT);

    // Mount system variables
    protected int maxPassengers = 1;
    protected double cargoCapacity = 0.0;
    protected int defenseDamage = 0;
    protected boolean canHarvest = false;
    protected int combatDamage = 0;
    protected float armorValue = 0.0f;
    protected int inventorySize = 0;

    // Growth and behavior
    protected int growthStage = 0;
    protected int grazingCooldown = 0;
    protected float neckRotation = 0.0f;
    protected int boostTime = 0;

    // Inventory systems
    protected SimpleContainer inventory;
    protected SimpleContainer cargoInventory;

    // Climbing system for harnesses
    protected AABB ladderHitbox;

    protected MountCategory mainCategory = MountCategory.NOT_RIDEABLE;
    protected List<MountCategory> subCategories = List.of();

    // Harness weapon system
    private HarnessWeapon currentWeapon = HarnessWeapon.NONE;
    private int weaponCooldown = 0;

    protected AbstractSauropodEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        initializeInventory();
        updateAttributesForGrowthStage();
        updateLadderHitbox();
    }

    protected void initializeInventory() {
        if (inventorySize > 0) {
            this.inventory = new SimpleContainer(inventorySize);
        }
        if (getMountCategory() == MountCategory.CARGO && cargoCapacity > 0) {
            this.cargoInventory = new SimpleContainer((int) cargoCapacity);
        }
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof Player;
    }

    public enum MountCategory {
        CARGO("Cargo Transport", "Carries large amounts of items"),
        RESOURCE("Resource Gatherer", "Automatically harvests resources"),
        HEDGEHOG("Defensive Mount", "Damages nearby enemies"),
        PASSENGER("Passenger Transport", "Carries multiple riders"),
        COMBAT("Combat Mount", "Equipped for battle"),
        TANK("Heavy Combat", "Armored war mount"),
        NOT_RIDEABLE("Wild", "Cannot be ridden"),
        MULTITASK("Multi-Purpose", "Serves multiple functions");

        private final String displayName;
        private final String description;

        MountCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isMulti() {
            return this == MULTITASK;
        }
    }

    protected void updateLadderHitbox() {
        if (this.isHarnessed()) {
            double width = this.getBbWidth() * 1.1;
            double height = this.getBbHeight();
            this.ladderHitbox = new AABB(
                    this.getX() - width/2, this.getY(), this.getZ() - width/2,
                    this.getX() + width/2, this.getY() + height, this.getZ() + width/2
            );
        } else {
            this.ladderHitbox = null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HARNESSED, false);
        builder.define(DATA_BOOST_TIME, 0);
        builder.define(DATA_IS_GRAZING, false);
        builder.define(DATA_GROWTH_STAGE, 0);
        builder.define(DATA_NECK_ROTATION, 0.0f);
        builder.define(DATA_HARNESS_WEAPON, HarnessWeapon.NONE.ordinal());
        builder.define(DATA_WEAPON_COOLDOWN, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));

        if (getMountCategory() == MountCategory.NOT_RIDEABLE) {
            this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        } else {
            this.goalSelector.addGoal(1, new SauropodRetaliationGoal(this));
        }

        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Target goals
        if (this.isTame()) {
            this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        } else {
            if (getMountCategory() != MountCategory.NOT_RIDEABLE) {
                this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
                this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                        (entity) -> this.getLastHurtByMob() == entity));
            }
        }
    }

    public MountCategory getMountCategory() {
        return mainCategory;
    }

    public List<MountCategory> getCategories() {
        if (mainCategory == MountCategory.MULTITASK) {
            return subCategories;
        }
        return List.of(mainCategory);
    }

    public abstract Item getTamingItem();
    public abstract SoundEvent getAmbientSound();
    public abstract SoundEvent getHurtSound();
    public abstract SoundEvent getDeathSound();

    // Harness system
    @Override
    public boolean isSaddleable() {
        return getMountCategory() != MountCategory.NOT_RIDEABLE &&
                this.isAlive() &&
                this.getGrowthStage() >= 3;
    }

    public void equipSaddle(@Nullable SoundEvent soundEvent) {
        this.entityData.set(DATA_HARNESSED, true);
        initializeHarnessInventory();
        updateLadderHitbox();
        if (soundEvent != null) {
            this.level().playSound(null, this, soundEvent, this.getSoundSource(), 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.isHarnessed();
    }

    public boolean isHarnessed() {
        return this.entityData.get(DATA_HARNESSED);
    }

    protected void initializeHarnessInventory() {
        if (getCategories().contains(MountCategory.CARGO) && this.cargoInventory == null && cargoCapacity > 0) {
            this.cargoInventory = new SimpleContainer((int) cargoCapacity);
        }
        if ((getCategories().contains(MountCategory.CARGO) || getCategories().contains(MountCategory.PASSENGER))
                && inventorySize > 0 && this.inventory == null) {
            this.inventory = new SimpleContainer(inventorySize);
        }
    }

    // Harness weapon system
    public void equipHarnessWeapon(HarnessWeapon weapon) {
        if (this.isHarnessed() && weapon.canBeEquippedOn(getMountCategory())) {
            this.currentWeapon = weapon;
            this.entityData.set(DATA_HARNESS_WEAPON, weapon.ordinal());
        }
    }

    public HarnessWeapon getHarnessWeapon() {
        int weaponId = this.entityData.get(DATA_HARNESS_WEAPON);
        if (weaponId >= 0 && weaponId < HarnessWeapon.values().length) {
            return HarnessWeapon.values()[weaponId];
        }
        return HarnessWeapon.NONE;
    }

    public boolean hasHarnessWeapon() {
        return getHarnessWeapon() != HarnessWeapon.NONE;
    }

    public int getWeaponCooldown() {
        return this.entityData.get(DATA_WEAPON_COOLDOWN);
    }

    public void setWeaponCooldown(int cooldown) {
        this.weaponCooldown = cooldown;
        this.entityData.set(DATA_WEAPON_COOLDOWN, cooldown);
    }

    public boolean canUseWeapon() {
        return hasHarnessWeapon() && getWeaponCooldown() <= 0 && this.isVehicle();
    }

    public void fireHarnessWeapon(@Nullable LivingEntity target) {
        if (!canUseWeapon()) return;

        HarnessWeapon weapon = getHarnessWeapon();
        Vec3 shootPos = this.getEyePosition().add(this.getLookAngle().scale(2.0));

        switch (weapon) {
            case LIGHT_BALLISTA -> fireBallista(shootPos, target, false);
            case HEAVY_BALLISTA -> fireBallista(shootPos, target, true);
            case LIGHT_CANNON -> fireCannon(shootPos, target, false);
            case HEAVY_CANNON -> fireCannon(shootPos, target, true);
            case SPIKE_LAUNCHER -> fireSpikes(shootPos, target);
            case FLAME_THROWER -> fireFlamethrower(shootPos, target);
        }

        setWeaponCooldown(weapon.getCooldown());
        this.level().playSound(null, this, weapon.getFiringSound(), this.getSoundSource(), 1.0f, 1.0f);
    }

    private void fireBallista(Vec3 shootPos, @Nullable LivingEntity target, boolean heavy) {
        Arrow arrow = new Arrow(this.level(), this, new ItemStack(Items.ARROW), null);
        arrow.setPos(shootPos.x, shootPos.y, shootPos.z);

        if (target != null) {
            double dx = target.getX() - shootPos.x;
            double dy = target.getY(0.3) - shootPos.y;
            double dz = target.getZ() - shootPos.z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            arrow.shoot(dx, dy + distance * 0.2, dz, heavy ? 4.0f : 3.0f, 1.0f);
        } else {
            Vec3 lookAngle = this.getLookAngle();
            arrow.shoot(lookAngle.x, lookAngle.y, lookAngle.z, heavy ? 4.0f : 3.0f, 1.0f);
        }

        arrow.setBaseDamage(heavy ? 15.0 : 10.0);
        arrow.setCritArrow(heavy);
        this.level().addFreshEntity(arrow);
    }

    private void fireCannon(Vec3 shootPos, @Nullable LivingEntity target, boolean heavy) {
        // Create explosive fireball projectile
        Vec3 vec3 = this.getViewVector(1.0F);
        assert target != null;
        double d2 = target.getX() - (this.getX() + vec3.x * (double)4.0F);
        double d3 = target.getY(0.5F) - ((double)0.5F + this.getY((double)0.5F));
        double d4 = target.getZ() - (this.getZ() + vec3.z * (double)4.0F);
        Vec3 vec31 = new Vec3(d2, d3, d4);
        LargeFireball fireball = new LargeFireball(this.level(), this, vec31.normalize(), heavy ? 3 : 2);
        fireball.setPos(shootPos.x, shootPos.y, shootPos.z);
        this.level().addFreshEntity(fireball);
    }

    private void fireSpikes(Vec3 shootPos, @Nullable LivingEntity target) {
        // Fire multiple small projectiles
        for (int i = 0; i < 5; i++) {
            Arrow spike = new Arrow(this.level(), this, new ItemStack(Items.ARROW), null);
            spike.setPos(shootPos.x, shootPos.y, shootPos.z);

            Vec3 direction = this.getLookAngle();
            // Add some spread
            direction = direction.add(
                    (this.random.nextDouble() - 0.5) * 0.3,
                    (this.random.nextDouble() - 0.5) * 0.3,
                    (this.random.nextDouble() - 0.5) * 0.3
            );

            spike.shoot(direction.x, direction.y, direction.z, 2.5f, 2.0f);
            spike.setBaseDamage(6.0);
            this.level().addFreshEntity(spike);
        }
    }

    private void fireFlamethrower(Vec3 shootPos, @Nullable LivingEntity target) {
        // Create flame effect in cone in front
        Vec3 direction = this.getLookAngle();
        for (int i = 0; i < 3; i++) {
            Vec3 flamePos = shootPos.add(direction.scale(i + 1));
            AABB flameArea = new AABB(flamePos.add(-1, -1, -1), flamePos.add(1, 1, 1));

            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, flameArea);
            for (LivingEntity entity : entities) {
                if (entity != this && !(entity instanceof Player && this.isOwnedBy((Player) entity))) {
                    entity.hurt(this.damageSources().onFire(), 8.0f);
                    entity.igniteForTicks(100);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.isTame()) {
            if (this.getGrowthStage() <= 1 && itemstack.is(getTamingItem())) {
                return this.fedFood(player, itemstack);
            }
            return InteractionResult.PASS;
        }

        if (this.isOwnedBy(player)) {
            // Equip harness
            if (itemstack.is(Items.SADDLE) && this.isSaddleable() && !this.isHarnessed()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.equipSaddle(SoundEvents.HORSE_SADDLE);
                return InteractionResult.SUCCESS;
            }

            // Mount the sauropod
            if (this.isHarnessed() && !this.isVehicle() && getMountCategory() != MountCategory.NOT_RIDEABLE) {
                if (!this.level().isClientSide) {
                    player.startRiding(this);
                }
                return InteractionResult.SUCCESS;
            }

            // Open inventory/harness GUI
            if (this.isHarnessed() && player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    openHarnessGUI(player);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    protected void openHarnessGUI(Player player) {
        if (this.level().isClientSide) return;

        // Create a container that includes both inventory and harness configuration
        SauropodHarnessContainer container = new SauropodHarnessContainer(
                this,
                this.inventory,
                this.cargoInventory,
                player.getInventory()
        );
    }

    protected InteractionResult fedFood(Player player, ItemStack itemStack) {
        if (this.random.nextInt(10) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.recomputePath();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    // Passenger system
    @Override
    public boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getMaxPassengers() && super.canAddPassenger(passenger);
    }

    public int getMaxPassengers() {
        if (getCategories().contains(MountCategory.PASSENGER)) {
            return maxPassengers;
        }
        if (getCategories().contains(MountCategory.CARGO) ||
                getCategories().contains(MountCategory.RESOURCE) ||
                getCategories().contains(MountCategory.HEDGEHOG) ||
                getCategories().contains(MountCategory.COMBAT) ||
                getCategories().contains(MountCategory.TANK)) {
            return 1;
        }
        return 0;
    }

    // Growth system
    public void setGrowthStage(int stage) {
        this.growthStage = Math.max(0, Math.min(3, stage));
        this.entityData.set(DATA_GROWTH_STAGE, this.growthStage);
        updateAttributesForGrowthStage();
    }

    public int getGrowthStage() {
        return this.entityData.get(DATA_GROWTH_STAGE);
    }

    public boolean canBeTamed() {
        return this.getGrowthStage() <= 1;
    }

    public boolean canBeRidden() {
        return this.getGrowthStage() >= 3;
    }

    protected void updateAttributesForGrowthStage() {
        float sizeMultiplier = 0.4f + (getGrowthStage() * 0.2f);
        this.refreshDimensions();

        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            double baseHealth = this.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * sizeMultiplier);
        }

        updateLadderHitbox();
    }

    // Climbing system for harnesses
    public boolean canClimbAt(BlockPos pos) {
        if (!this.isHarnessed() || this.ladderHitbox == null) {
            return false;
        }

        Vec3 posVec = Vec3.atCenterOf(pos);
        return this.ladderHitbox.contains(posVec);
    }

    @Override
    public boolean onClimbable() {
        if (this.isHarnessed() && this.ladderHitbox != null) {
            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.ladderHitbox);
            return !nearbyPlayers.isEmpty();
        }
        return super.onClimbable();
    }

    // Specialized behaviors
    public void performSpecialAbility() {
        for (MountCategory cat : getCategories()) {
            switch (cat) {
                case HEDGEHOG -> performHedgehogDefense();
                case RESOURCE -> performResourceCollection();
                case TANK -> performDefensiveStance();
                case COMBAT -> {
                    if (hasHarnessWeapon() && this.isVehicle() && this.getTarget() instanceof LivingEntity target) {
                        fireHarnessWeapon(target);
                    }
                }
            }
        }
    }

    protected void performHedgehogDefense() {
        if (defenseDamage > 0) {
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(2.0),
                    entity -> entity != this && !(entity instanceof Player && this.isOwnedBy((Player) entity))
            );

            for (LivingEntity entity : nearbyEntities) {
                entity.hurt(this.damageSources().thorns(this), defenseDamage);
            }
        }
    }

    protected void performResourceCollection() {
        if (canHarvest && !this.level().isClientSide) {
            BlockPos pos = this.blockPosition();
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = 0; y <= 2; y++) {
                        BlockPos checkPos = pos.offset(x, y, z);
                        BlockState state = this.level().getBlockState(checkPos);
                        if (canHarvestBlock(state)) {
                            this.level().destroyBlock(checkPos, true);
                        }
                    }
                }
            }
        }
    }

    protected boolean canHarvestBlock(BlockState state) {
        return false;
    }

    protected void performDefensiveStance() {
        // Defensive stance implementation
    }

    // Grazing behavior
    public void setGrazing(boolean grazing) {
        this.entityData.set(DATA_IS_GRAZING, grazing);
    }

    public boolean isGrazing() {
        return this.entityData.get(DATA_IS_GRAZING);
    }

    // Neck animation
    public void setNeckRotation(float rotation) {
        this.entityData.set(DATA_NECK_ROTATION, rotation);
    }

    public float getNeckRotation() {
        return this.entityData.get(DATA_NECK_ROTATION);
    }

    @Override
    public void tick() {
        super.tick();

        if (grazingCooldown > 0) {
            grazingCooldown--;
        }

        if (weaponCooldown > 0) {
            weaponCooldown--;
            setWeaponCooldown(weaponCooldown);
        }

        // Natural growth
        if (!this.level().isClientSide && this.getAge() == 0 && this.random.nextInt(12000) == 0) {
            if (getGrowthStage() < 3) {
                setGrowthStage(getGrowthStage() + 1);
            }
        }

        if (this.level().isClientSide) {
            updateNeckAnimation();
        }

        updateLadderHitbox();
    }

    protected void updateNeckAnimation() {
        float targetRotation = (float) (Math.sin(this.tickCount * 0.1) * 15.0);
        if (this.isGrazing()) {
            targetRotation = -45.0f;
        }
        this.neckRotation = this.neckRotation + (targetRotation - this.neckRotation) * 0.1f;
        setNeckRotation(this.neckRotation);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return (source.is(DamageTypes.SONIC_BOOM)||source.is(ModDamageTypes.TAIL_WHIP)) && super.isInvulnerableTo(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Harnessed", this.isHarnessed());
        tag.putInt("BoostTime", this.boostTime);
        tag.putInt("GrowthStage", this.growthStage);
        tag.putFloat("NeckRotation", this.neckRotation);
        tag.putInt("GrazingCooldown", this.grazingCooldown);
        tag.putInt("HarnessWeapon", this.currentWeapon.ordinal());
        tag.putInt("WeaponCooldown", this.weaponCooldown);

        // Inventory
        if (this.inventory != null) {
            CompoundTag inventoryTag = new CompoundTag();
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                ItemStack stack = this.inventory.getItem(i);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    stack.save(this.registryAccess(), itemTag);
                    inventoryTag.put("Item" + i, itemTag);
                }
            }
            tag.put("Inventory", inventoryTag);
        }

        // Cargo
        if (this.cargoInventory != null) {
            CompoundTag cargoTag = new CompoundTag();
            for (int i = 0; i < this.cargoInventory.getContainerSize(); i++) {
                ItemStack stack = this.cargoInventory.getItem(i);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    stack.save(this.registryAccess(), itemTag);
                    cargoTag.put("Cargo" + i, itemTag);
                }
            }
            tag.put("CargoInventory", cargoTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Harnessed")) {
            this.entityData.set(DATA_HARNESSED, tag.getBoolean("Harnessed"));
        }
        if (tag.contains("BoostTime")) {
            this.boostTime = tag.getInt("BoostTime");
        }
        if (tag.contains("GrowthStage")) {
            setGrowthStage(tag.getInt("GrowthStage"));
        }
        if (tag.contains("NeckRotation")) {
            this.neckRotation = tag.getFloat("NeckRotation");
        }
        if (tag.contains("GrazingCooldown")) {
            this.grazingCooldown = tag.getInt("GrazingCooldown");
        }
        if (tag.contains("HarnessWeapon")) {
            int weaponId = tag.getInt("HarnessWeapon");
            if (weaponId >= 0 && weaponId < HarnessWeapon.values().length) {
                this.currentWeapon = HarnessWeapon.values()[weaponId];
            }
        }
        if (tag.contains("WeaponCooldown")) {
            this.weaponCooldown = tag.getInt("WeaponCooldown");
        }

        if (tag.contains("Inventory") && this.inventory != null) {
            CompoundTag inventoryTag = tag.getCompound("Inventory");
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                if (inventoryTag.contains("Item" + i)) {
                    ItemStack stack = ItemStack.parseOptional(this.registryAccess(), inventoryTag.getCompound("Item" + i));
                    this.inventory.setItem(i, stack);
                }
            }
        }

        if (tag.contains("CargoInventory") && this.cargoInventory != null) {
            CompoundTag cargoTag = tag.getCompound("CargoInventory");
            for (int i = 0; i < this.cargoInventory.getContainerSize(); i++) {
                if (cargoTag.contains("Cargo" + i)) {
                    ItemStack stack = ItemStack.parseOptional(this.registryAccess(), cargoTag.getCompound("Cargo" + i));
                    this.cargoInventory.setItem(i, stack);
                }
            }
        }
    }

    // Custom retaliation goal for large wild sauropods
    static class SauropodRetaliationGoal extends Goal {
        private final AbstractSauropodEntity sauropod;
        private LivingEntity targetEntity;

        public SauropodRetaliationGoal(AbstractSauropodEntity sauropod) {
            this.sauropod = sauropod;
        }

        @Override
        public boolean canUse() {
            if (sauropod.isTame() && targetEntity == this.sauropod.getOwner()) {
                return false;
            }

            this.targetEntity = sauropod.getLastHurtByMob();
            return this.targetEntity != null && this.targetEntity.isAlive();
        }

        @Override
        public void start() {
            sauropod.setTarget(this.targetEntity);
        }

        @Override
        public void tick() {
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetEntity != null &&
                    this.targetEntity.isAlive() &&
                    sauropod.distanceToSqr(this.targetEntity) < 100.0;
        }
    }

    // Harness weapon enumeration
    public enum HarnessWeapon {
        NONE("None", 0, 0, SoundEvents.ITEM_BREAK, MountCategory.NOT_RIDEABLE),
        LIGHT_BALLISTA("Light Ballista", 80, 15, SoundEvents.CROSSBOW_SHOOT, MountCategory.COMBAT),
        HEAVY_BALLISTA("Heavy Ballista", 120, 25, SoundEvents.CROSSBOW_SHOOT, MountCategory.COMBAT),
        LIGHT_CANNON("Light Cannon", 100, 20, ModSoundEvents.CANNON_LAUNCH, MountCategory.COMBAT),
        HEAVY_CANNON("Heavy Cannon", 160, 35, ModSoundEvents.CANNON_LAUNCH, MountCategory.TANK),
        SPIKE_LAUNCHER("Spike Launcher", 40, 8, SoundEvents.DISPENSER_LAUNCH, MountCategory.COMBAT),
        FLAME_THROWER("Flame Thrower", 50, 10, SoundEvents.FIRECHARGE_USE, MountCategory.COMBAT);

        private final String displayName;
        private final int cooldown; // in ticks
        private final int cost; // resource cost to craft/install
        private final SoundEvent firingSound;
        private final List<MountCategory> compatibleCategories;

        HarnessWeapon(String displayName, int cooldown, int cost, SoundEvent firingSound, MountCategory categories) {
            this.displayName = displayName;
            this.cooldown = cooldown;
            this.cost = cost;
            this.firingSound = firingSound;
            this.compatibleCategories = List.of(categories);
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getCooldown() {
            return cooldown;
        }

        public int getCost() {
            return cost;
        }

        public SoundEvent getFiringSound() {
            return firingSound;
        }

        public boolean canBeEquippedOn(MountCategory category) {
            return compatibleCategories.contains(category);
        }

        public List<MountCategory> getCompatibleCategories() {
            return compatibleCategories;
        }

        public String getDescription() {
            return switch (this) {
                case NONE -> "No weapon equipped";
                case LIGHT_BALLISTA -> "Fires accurate bolts at medium range";
                case HEAVY_BALLISTA -> "Fires powerful bolts with high damage";
                case LIGHT_CANNON -> "Launches explosive projectiles";
                case HEAVY_CANNON -> "Devastating explosive artillery";
                case SPIKE_LAUNCHER -> "Fires multiple spikes in a spread";
                case FLAME_THROWER -> "Short-range flame attack";
            };
        }
    }
}