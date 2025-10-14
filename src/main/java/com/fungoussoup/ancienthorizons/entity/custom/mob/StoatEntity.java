package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StoatEntity extends Animal {

    private static final EntityDataAccessor<Boolean> CHICKEN_JOCKEY = SynchedEntityData.defineId(StoatEntity.class, EntityDataSerializers.BOOLEAN);

    public StoatEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ATTACK_SPEED, 1.2D);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHICKEN_JOCKEY, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(3, new TemptGoal(this, 1.25D, stack -> stack.is(ModTags.Items.STOAT_FOOD), false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Attack goals for adults only
        if (!this.isBaby()) {
            goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Chicken.class, true, this::shouldAttackChicken));
            targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true, entity -> entity.getType().is(ModTags.EntityTypes.STOAT_PREY)));
            targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Turtle.class, true, Turtle.BABY_ON_LAND_SELECTOR));
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Chicken jockey mounting logic
        if (this.isBaby() && !isChickenJockey() && random.nextInt(2000) == 0) {
            tryBecomeChickenJockey();
        }

        // Dismount dead chickens
        if (getVehicle() instanceof Chicken chicken) {
            if (this.isPassenger() || !chicken.isAlive()) {
                this.stopRiding();
                this.setChickenJockey(false);
            }
        }
    }

    private void tryBecomeChickenJockey() {
        if (!level().isClientSide) {
            var chickens = level().getEntitiesOfClass(Chicken.class, getBoundingBox().inflate(8.0),
                    chicken -> !chicken.isVehicle() && chicken.isAlive());

            if (!chickens.isEmpty()) {
                Chicken target = chickens.get(random.nextInt(chickens.size()));
                if (startRiding(target)) {
                    setChickenJockey(true);
                }
            }
        }
    }

    private boolean shouldAttackChicken(LivingEntity entity) {
        if (!(entity instanceof Chicken chicken)) return false;
        if (this.isChickenJockey()) return false;
        return !hasStoatRider(chicken);
    }

    private boolean hasStoatRider(Chicken chicken) {
        return chicken.getPassengers().stream().anyMatch(passenger -> passenger instanceof StoatEntity);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.STOAT_FOOD);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isPassenger(); // prevent movement while riding
    }

    public boolean canBeLeashed(Player player) {
        return !isChickenJockey();
    }

    public void setChickenJockey(boolean value) {
        this.entityData.set(CHICKEN_JOCKEY, value);
    }

    public boolean isChickenJockey() {
        return this.entityData.get(CHICKEN_JOCKEY);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        goalSelector.removeAllGoals(goal -> true);
        targetSelector.removeAllGoals(goal -> true);
        registerGoals();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsChickenJockey", isChickenJockey());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setChickenJockey(tag.getBoolean("IsChickenJockey"));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        StoatEntity baby = ModEntities.STOAT.get().create(level);
        if (baby != null && level.getRandom().nextFloat() < 0.05f) {
            baby.setChickenJockey(true);
            baby.tryBecomeChickenJockey();
        }
        return baby;
    }

    public static void handleStoatSpawn(StoatEntity stoat, ServerLevel level) {
        if (stoat.isBaby() && level.getRandom().nextFloat() < 0.02f) {
            stoat.setChickenJockey(true);
            level.getServer().tell(new net.minecraft.server.TickTask(1, stoat::tryBecomeChickenJockey));
        }
    }
}
