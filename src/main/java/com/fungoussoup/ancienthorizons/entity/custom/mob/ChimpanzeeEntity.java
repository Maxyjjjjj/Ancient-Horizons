package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.ArborealAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.CuriousAndIntelligentAnimal;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

public class ChimpanzeeEntity extends Animal implements CuriousAndIntelligentAnimal, ArborealAnimal {

    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.BOOLEAN);

    public ChimpanzeeEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.15D, Ingredient.of(ModItems.BANANA), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.BANANA);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        return ModEntities.CHIMPANZEE.get().create(level);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SITTING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isFood(stack) && this.isBaby()) {
            this.usePlayerItem(player, hand, stack);
            this.ageUp(getAge() + 200);
            return InteractionResult.SUCCESS;
        }

        if (!this.isBaby() && isBarterCurrency(stack)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.getNavigation().stop();
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isFood(stack) && !this.isBaby()) {
            this.usePlayerItem(player, hand, stack);
            this.heal(2.0F);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private boolean isBarterCurrency(ItemStack stack) {
        return stack.is(ModItems.BANANA) || stack.is(Items.APPLE);
    }

    @Override
    public void tick() {
        super.tick();
        // Handle climbing behavior through ArborealAnimal interface
        handleClimbing(this);
    }

    @Override
    public double getClimbingSpeed() {
        return 0.15; // Chimps are agile climbers
    }

    @Override
    public boolean canClimb() {
        // Don't climb while sitting or if panicking
        return !this.entityData.get(SITTING) && !this.isInPowderSnow;
    }

    public static class SitGoal extends Goal {
        private final ChimpanzeeEntity chimp;

        public SitGoal(ChimpanzeeEntity chimp) {
            this.chimp = chimp;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return chimp.entityData.get(SITTING);
        }

        @Override
        public void start() {
            chimp.getNavigation().stop();
        }

        @Override
        public void tick() {
            chimp.getLookControl().setLookAt(chimp.getX() + 1, chimp.getEyeY(), chimp.getZ());
        }
    }
}