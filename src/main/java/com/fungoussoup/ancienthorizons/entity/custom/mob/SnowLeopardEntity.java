package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.EventHooks;

public class SnowLeopardEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(SnowLeopardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(SnowLeopardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_BLUE_EYES = SynchedEntityData.defineId(SnowLeopardEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState holdTailAnimationState = new AnimationState();

    public SnowLeopardEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // movement / survival
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 2.0D));
        // climbing powder snow is a nicety but lower priority than panic/sit
        this.goalSelector.addGoal(3, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
        // combat / taming behaviors
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        // basic behaviors
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // targets: owner-hurt should be high priority so it defends owner
        this.targetSelector.addGoal(0, new OwnerHurtTargetGoal(this));
        // if untamed, hunt prey (lower priority than owner-hurt)
        this.targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                entity -> entity.getType().is(ModTags.EntityTypes.SNOW_LEOPARD_PREY)) {
            @Override
            public boolean canUse() {
                return !SnowLeopardEntity.this.isBaby() && super.canUse();
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.isSitting()) {
            this.sitAnimationState.start(this.tickCount);
        } else {
            this.sitAnimationState.stop();
        }

        if (this.isHoldingTail()) {
            this.holdTailAnimationState.start(this.tickCount);
        } else {
            this.holdTailAnimationState.stop();
        }
    }

    public boolean isHoldingTail() {
        return this.level().isThundering();
    }

    public boolean isSitting() {
        return this.entityData.get(IS_SITTING);
    }

    public boolean isOrderedToSit() {
        return this.isSitting();
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(IS_SITTING, sitting);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        final boolean blueEyesOther = otherParent instanceof SnowLeopardEntity snowOther && snowOther.isBlueEyes();
        SnowLeopardEntity baby = ModEntities.SNOW_LEOPARD.get().create(level);
        if (baby == null) return null;

        double blueEyesChance = 0.1D;
        if (this.isBlueEyes() && blueEyesOther) {
            blueEyesChance = 0.8D;
        } else if (this.isBlueEyes() != blueEyesOther) {
            blueEyesChance = 0.4D;
        }
        baby.setBlueEyes(this.random.nextDouble() < blueEyesChance);

        // Baby inherits tame status from either parent if either is tame and owned
        if (this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
        } else if (otherParent instanceof TamableAnimal t && t.isTame()) {
            baby.setOwnerUUID(t.getOwnerUUID());
            baby.setTame(true, true);
        }

        return baby;
    }

    public boolean isBlueEyes() {
        return this.entityData.get(IS_BLUE_EYES);
    }

    public void setBlueEyes(boolean b) {
        this.entityData.set(IS_BLUE_EYES, b);
    }

    private void tryToTame(Player player) {
        // event hook: if cancelled, don't tame
        if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        builder.define(IS_SITTING, false);
        builder.define(IS_BLUE_EYES, false);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (this.level().isClientSide) {
            boolean canInteract = this.isOwnedBy(player) || this.isTame() || (!this.isTame() && stack.is(ItemTags.MEAT));
            return canInteract ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties food = stack.getFoodProperties(this);
                float healAmount = food != null ? food.nutrition() : 1.0F;
                this.heal(2.0F * healAmount);
                stack.shrink(1);
                this.gameEvent(GameEvent.EAT);
                return InteractionResult.SUCCESS;
            } else if (item instanceof DyeItem dye && this.isOwnedBy(player)) {
                DyeColor color = dye.getDyeColor();
                if (color != this.getCollarColor()) {
                    this.setCollarColor(color);
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (stack.is(ItemTags.MEAT)) {
            stack.shrink(1);
            this.tryToTame(player);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0F);
            this.setHealth(40.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(18.0F);
        }

    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            return super.hurt(source, amount);
        }
    }

    public boolean canUseSlot(EquipmentSlot slot) {
        return true;
    }

    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        if (!this.canArmorAbsorb(damageSource)) {
            super.actuallyHurt(damageSource, damageAmount);
        } else {
            ItemStack itemstack = this.getBodyArmorItem();
            int i = itemstack.getDamageValue();
            int j = itemstack.getMaxDamage();
            itemstack.hurtAndBreak(Mth.ceil(damageAmount), this, EquipmentSlot.BODY);
            if (Crackiness.WOLF_ARMOR.byDamage(i, j) != Crackiness.WOLF_ARMOR.byDamage(this.getBodyArmorItem())) {
                this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
                Level var7 = this.level();
                if (var7 instanceof ServerLevel) {
                    ServerLevel serverlevel = (ServerLevel)var7;
                    serverlevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ModItems.PANGOLIN_SCALE.get().getDefaultInstance()), this.getX(), this.getY() + (double)1.0F, this.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
                }
            }
        }

    }

    private boolean canArmorAbsorb(DamageSource damageSource) {
        return this.hasArmor() && !damageSource.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
    }

    protected void hurtArmor(DamageSource damageSource, float damageAmount) {
        this.doHurtEquipment(damageSource, damageAmount, new EquipmentSlot[]{EquipmentSlot.BODY});
    }


    public boolean hasArmor() {
        return this.getBodyArmorItem().is(ModItems.SNOW_LEOPARD_ARMOR);
    }
}
