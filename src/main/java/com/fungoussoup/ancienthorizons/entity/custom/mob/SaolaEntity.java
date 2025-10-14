package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SaolaEntity extends Animal {

    private static final EntityDataAccessor<Boolean> HAS_HORNS = SynchedEntityData.defineId(SaolaEntity.class, EntityDataSerializers.BOOLEAN);
    private int hornRegenTicks = 0;
    private static final EntityDataAccessor<Optional<UUID>> TRUSTED_PLAYER =
            SynchedEntityData.defineId(SaolaEntity.class, EntityDataSerializers.OPTIONAL_UUID);


    public SaolaEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Wolf.class, 10.0F, 1.2D, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(ModTags.Items.SAOLA_FOOD), true));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new DefendTrustedPlayerGoal<>(this));
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob partner) {
        return ModEntities.SAOLA.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.SAOLA_FOOD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_HORNS, true);
        builder.define(TRUSTED_PLAYER, Optional.empty());
    }

    public boolean hasHorns() {
        return this.entityData.get(HAS_HORNS);
    }

    public void setHasHorns(boolean hasHorns) {
        this.entityData.set(HAS_HORNS, hasHorns);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData) {
        // Initialize entity data properly
        this.setHasHorns(true); // Ensure horns are set on spawn
        return super.finalizeSpawn(world, difficulty, reason, spawnData);
    }

    @Override
    public void tick() {
        super.tick();

        // Only run on server side
        if (!this.level().isClientSide) {
            // Night invisibility effect
            if (this.isAlive() && this.level().isNight() && this.random.nextInt(2000) == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0)); // 30s
            }

            // Horn regeneration
            if (!hasHorns()) {
                hornRegenTicks++;
                if (hornRegenTicks >= 12000) { // 10 min
                    setHasHorns(true);
                    hornRegenTicks = 0;
                }
            }
        }
    }

    public void setTrustedPlayer(@Nullable UUID uuid) {
        this.entityData.set(TRUSTED_PLAYER, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getTrustedPlayerUUID() {
        return this.entityData.get(TRUSTED_PLAYER).orElse(null);
    }

    @Nullable
    public Player getTrustedPlayer() {
        UUID uuid = getTrustedPlayerUUID();
        return uuid == null ? null : this.level().getPlayerByUUID(uuid);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Trust the player with food
        if (stack.is(ModTags.Items.SAOLA_FOOD) && !this.level().isClientSide) {
            this.setTrustedPlayer(player.getUUID());
            this.level().broadcastEntityEvent(this, (byte) 7); // heart particles
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        // Shear horns
        if (stack.is(Items.SHEARS) && hasHorns() && !this.level().isClientSide) {
            this.setHasHorns(false);
            this.hornRegenTicks = 0;
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            this.spawnAtLocation(ModItems.SAOLA_HORN.get());
            this.spawnAtLocation(ModItems.SAOLA_HORN.get());
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    public static class DefendTrustedPlayerGoal<T extends Mob> extends TargetGoal {
        private final T mob;
        private LivingEntity target;

        public DefendTrustedPlayerGoal(T mob) {
            super(mob, false);
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (!(mob instanceof SaolaEntity saola)) return false;
            Player trusted = saola.getTrustedPlayer();
            if (trusted == null || !trusted.isAlive()) return false;

            LivingEntity attacker = trusted.getLastHurtByMob();
            if (attacker == null || !this.canAttack(attacker, TargetingConditions.DEFAULT)) return false;

            this.target = attacker;
            return true;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.target);
            super.start();
        }
    }
}