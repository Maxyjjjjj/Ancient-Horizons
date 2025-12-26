package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class BrownBearEntity extends Animal implements NeutralMob {

    /* ---------------- DATA ---------------- */

    private static final EntityDataAccessor<Integer> DATA_MAIN_GENE =
            SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HIDDEN_GENE =
            SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANGER_TIME =
            SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);

    private UUID persistentAngerTarget;

    /* ---------------- GENE STATE ---------------- */

    private int weakPauseTicks;
    private int weakCooldown;
    private int playfulTicks;
    private int worriedCooldown;
    private int lazyNapTicks;

    /* ---------------- CONSTRUCTOR ---------------- */

    public BrownBearEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.BEAR_FOOD);
    }

    /* ---------------- ATTRIBUTES ---------------- */

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    /* ---------------- DATA ---------------- */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MAIN_GENE, Gene.NORMAL.id);
        builder.define(DATA_HIDDEN_GENE, Gene.NORMAL.id);
        builder.define(DATA_ANGER_TIME, 0);
    }

    public enum Gene {
        NORMAL(0, false),
        LAZY(1, false),
        WORRIED(2, false),
        PLAYFUL(3, false),
        WEAK(4, true),
        AGGRESSIVE(5, false),
        WINNIETHEPOOH(6, true);

        final int id;
        final boolean isRecessive;

        Gene(int id, boolean isRecessive) {
            this.id = id;
            this.isRecessive = isRecessive;
        }

        static Gene byId(int id) {
            for (Gene g : values()) if (g.id == id) return g;
            return NORMAL;
        }

        static Gene random(RandomSource r) {
            int i = r.nextInt(16);
            if (i == 0) return LAZY;
            if (i == 1) return WORRIED;
            if (i == 2) return PLAYFUL;
            if (i < 6) return WEAK;
            if (i == 6) return AGGRESSIVE;
            if (i == 11) return WINNIETHEPOOH;
            return NORMAL;
        }
    }

    public Gene getGene() {
        return Gene.byId(this.entityData.get(DATA_MAIN_GENE));
    }

    /* ---------------- GOALS ---------------- */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    /* ---------------- TICK ---------------- */

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            updatePersistentAnger((ServerLevel) level(), true);

            switch (getGene()) {
                case WEAK -> tickWeak();
                case PLAYFUL -> tickPlayful();
                case WORRIED -> tickWorried();
                case LAZY -> tickLazy();
                case WINNIETHEPOOH -> tickPooh();
            }
        }
    }

    public Gene getPhenotype() {
        Gene main = Gene.byId(entityData.get(DATA_MAIN_GENE));
        Gene hidden = Gene.byId(entityData.get(DATA_HIDDEN_GENE));

        if (main.isRecessive) {
            return main == hidden ? main : Gene.NORMAL;
        }
        return main;
    }

    private void tickWeak() {
        if (weakCooldown > 0) weakCooldown--;

        if (weakPauseTicks > 0) {
            weakPauseTicks--;
            getNavigation().stop();

            if (weakPauseTicks % 20 == 0) {
                playSound(SoundEvents.POLAR_BEAR_AMBIENT, 0.4F, 0.8F);
            }
            return;
        }

        if (weakCooldown == 0 && random.nextInt(600) == 0) {
            weakPauseTicks = 40 + random.nextInt(40);
            weakCooldown = 200;
        }

        if (isInWaterOrRain() && random.nextInt(200) == 0) {
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
        }
    }

    private void tickPlayful() {
        Player player = level().getNearestPlayer(this, 8);

        if (playfulTicks > 0) {
            playfulTicks--;

            if (onGround() && random.nextInt(15) == 0) {
                setDeltaMovement(
                        (random.nextDouble() - 0.5D) * 0.6D,
                        0.45D,
                        (random.nextDouble() - 0.5D) * 0.6D
                );
            }

            if (player != null) {
                getLookControl().setLookAt(player, 10, 10);
            }

            if (random.nextInt(60) == 0) {
                playSound(SoundEvents.POLAR_BEAR_AMBIENT_BABY, 0.8F, 1.2F);
            }
            return;
        }

        if (!isAngry() && player != null && random.nextInt(400) == 0) {
            playfulTicks = 60 + random.nextInt(80);
        }
    }

    private void tickWorried() {
        if (worriedCooldown > 0) worriedCooldown--;

        Player player = level().getNearestPlayer(this, 10);

        if (player != null && (player.isSprinting() || player.isOnFire())) {
            panic();
        }

        if (random.nextInt(800) == 0 && worriedCooldown == 0) {
            panic();
        }
    }

    private void tickPooh() {
        // Poohs are calm, slow, food-motivated
        if (random.nextInt(400) == 0) {
            addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }

        // Loves honey WAY more
        if (random.nextInt(600) == 0) {
            heal(1.0F);
            playSound(SoundEvents.HONEY_BLOCK_PLACE, 0.6F, 0.9F);
        }

        // Less likely to stay angry
        if (getRemainingPersistentAngerTime() > 0 && random.nextInt(100) == 0) {
            setRemainingPersistentAngerTime(
                    Math.max(0, getRemainingPersistentAngerTime() - 40)
            );
        }
    }


    private void panic() {
        worriedCooldown = 200;
        Vec3 dir = position().subtract(
                level().getNearestPlayer(this, 10).position()).normalize();
        setDeltaMovement(dir.x * 0.6, 0.4, dir.z * 0.6);
    }

    private void tickLazy() {
        if (lazyNapTicks > 0) {
            lazyNapTicks--;
            getNavigation().stop();
            return;
        }

        if (level().isNight() && random.nextInt(800) == 0) {
            lazyNapTicks = 200 + random.nextInt(200);
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return entityData.get(DATA_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        entityData.set(DATA_ANGER_TIME, time);
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(UUID target) {
        persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        setRemainingPersistentAngerTime(400 + random.nextInt(400));
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        BrownBearEntity baby = ModEntities.BROWN_BEAR.get().create(level);
        if (baby != null && other instanceof BrownBearEntity parent) {

            RandomSource r = level.random;

            Gene g1 = r.nextBoolean()
                    ? Gene.byId(entityData.get(DATA_MAIN_GENE))
                    : Gene.byId(entityData.get(DATA_HIDDEN_GENE));

            Gene g2 = r.nextBoolean()
                    ? Gene.byId(parent.entityData.get(DATA_MAIN_GENE))
                    : Gene.byId(parent.entityData.get(DATA_HIDDEN_GENE));

            baby.entityData.set(DATA_MAIN_GENE, g1.id);
            baby.entityData.set(DATA_HIDDEN_GENE, g2.id);
        }
        return baby;
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isFood(stack)) {
            if (!level().isClientSide) {
                heal(getGene() == Gene.WINNIETHEPOOH ? 6.0F : 4.0F);

                if (getGene() == Gene.WINNIETHEPOOH) {
                    addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION, 200, 0));
                }

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

}
