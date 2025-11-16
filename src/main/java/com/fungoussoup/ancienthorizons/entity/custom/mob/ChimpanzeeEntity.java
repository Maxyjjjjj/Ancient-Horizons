package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.troop.Troop;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopManager;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopMember;
import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopRank;
import com.fungoussoup.ancienthorizons.entity.ai.troop.goals.DefendTroopGoal;
import com.fungoussoup.ancienthorizons.entity.ai.troop.goals.FollowTroopGoal;
import com.fungoussoup.ancienthorizons.entity.ai.troop.goals.PatrolTerritoryGoal;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.UUID;

public class ChimpanzeeEntity extends Animal implements TroopMember, CuriousAndIntelligentAnimal {

    private UUID troopId = null;
    private TroopRank troopRank = TroopRank.ADULT;

    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CLIMBING =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.BYTE);

    public ChimpanzeeEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(1, new ClimbGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new DefendTroopGoal<>(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.15D, Ingredient.of(ModItems.BANANA), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new FollowTroopGoal<>(this, 1.0D, 5.0F, 15.0F));
        this.goalSelector.addGoal(6, new PatrolTerritoryGoal<>(this, 1.0D));
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
    @javax.annotation.ParametersAreNonnullByDefault
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        ChimpanzeeEntity baby = ModEntities.CHIMPANZEE.get().create(level);
        if (baby != null && entity instanceof ChimpanzeeEntity parent) {
            if (this.troopId != null) {
                troopId = this.getTroopId();
            } else if (parent.troopId != null) {
                troopId = parent.getTroopId();
            } else {
                troopId = UUID.randomUUID();
            }
            baby.setTroopRank(TroopRank.JUVENILE);
        }
        return baby;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SITTING, false);
        builder.define(CLIMBING, (byte) 0);
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

                // Create a new troop if none exists
                if (this.getTroopId() == null) {
                    Troop newTroop = TroopManager.createTroop(this);
                    this.setTroopId(newTroop.getId());
                }
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
        return stack.is(ModItems.BANANA);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public UUID getTroopId() {
        return troopId;
    }

    @Override
    public void setTroopId(UUID id) {
        troopId = id;
    }

    @Override
    public TroopRank getTroopRank() {
        return troopRank;
    }

    @Override
    public void setTroopRank(TroopRank rank) {
        troopRank = rank;
    }

    @Override
    public Troop getTroop() {
        return TroopManager.getTroop(troopId).orElse(null);
    }

    @Override
    public LivingEntity getEntity() {
        return this;
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

    public static class ClimbGoal extends Goal {
        private final ChimpanzeeEntity chimp;

        public ClimbGoal(ChimpanzeeEntity chimp) {
            this.chimp = chimp;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return chimp.horizontalCollision;
        }

        @Override
        public void tick() {
            if (chimp.getDeltaMovement().y < 0.1) {
                chimp.setDeltaMovement(chimp.getDeltaMovement().add(0, 0.1, 0));
            }
        }
    }
}
