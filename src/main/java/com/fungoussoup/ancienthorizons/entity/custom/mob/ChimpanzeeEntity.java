package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.compat.Mods;
import com.fungoussoup.ancienthorizons.compat.create.UseCrankGoal;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.CuriousAndIntelligentAnimal;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class ChimpanzeeEntity extends Animal implements CuriousAndIntelligentAnimal {

    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> CLIMBING =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> TROOP_ID =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> TROOP_RANK =
            SynchedEntityData.defineId(ChimpanzeeEntity.class, EntityDataSerializers.INT);

    // Troop diplomacy system
    private final Map<UUID, TroopRelation> troopRelations = new HashMap<>();
    private int territoryCenterX;
    private int territoryCenterZ;
    private int territoryRadius = 64;
    private int troopScanCooldown = 0;
    private int barterCooldown = 0;
    private boolean isExaminingItem = false;
    private int examineTicks = 0;

    public enum TroopRelation {
        ALLY(0),
        NEUTRAL(1),
        ENEMY(2);

        private final int id;
        TroopRelation(int id) { this.id = id; }
        public int getId() { return id; }

        public static TroopRelation fromId(int id) {
            for (TroopRelation r : values()) {
                if (r.id == id) return r;
            }
            return NEUTRAL;
        }
    }

    public enum TroopRank {
        JUVENILE(0),
        ADULT(1),
        ALPHA(2);

        private final int id;
        TroopRank(int id) { this.id = id; }
        public int getId() { return id; }

        public static TroopRank fromId(int id) {
            for (TroopRank r : values()) {
                if (r.id == id) return r;
            }
            return ADULT;
        }
    }

    public ChimpanzeeEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.territoryCenterX = (int) this.getX();
        this.territoryCenterZ = (int) this.getZ();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData) {
        // Assign to troop or create new one
        if (spawnData instanceof ChimpTroopData troopData) {
            this.setTroopId(troopData.troopId);
            this.setTroopRank(TroopRank.ADULT);
        } else {
            UUID newTroopId = UUID.randomUUID();
            this.setTroopId(newTroopId);
            this.setTroopRank(TroopRank.ALPHA);
            spawnData = new ChimpTroopData(newTroopId);
        }

        this.territoryCenterX = (int) this.getX();
        this.territoryCenterZ = (int) this.getZ();

        return spawnData;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(1, new ClimbGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new DefendTroopGoal(this, 1.3D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.15D, Ingredient.of(ModItems.BANANA), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new FollowTroopGoal(this, 1.0D, 5.0F, 15.0F));
        this.goalSelector.addGoal(6, new PatrolTerritoryGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ChimpanzeeEntity.class));
        this.targetSelector.addGoal(2, new DefendAgainstEnemyTroopsGoal(this));

        if (Mods.CREATE.isLoaded()) {
            this.goalSelector.addGoal(2, new UseCrankGoal(this));
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.BANANA);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        ChimpanzeeEntity baby = ModEntities.CHIMPANZEE.get().create(level);
        if (baby != null && entity instanceof ChimpanzeeEntity parent) {
            // Baby inherits troop from parents
            baby.setTroopId(this.getTroopId().orElse(parent.getTroopId().orElse(UUID.randomUUID())));
            baby.setTroopRank(TroopRank.JUVENILE);
            baby.territoryCenterX = this.territoryCenterX;
            baby.territoryCenterZ = this.territoryCenterZ;
        }
        return baby;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.PANDA_STEP;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SITTING, false);
        builder.define(CLIMBING, (byte)0);
        builder.define(TROOP_ID, Optional.empty());
        builder.define(TROOP_RANK, TroopRank.ADULT.getId());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Baby growth acceleration
        if (this.isFood(stack) && this.isBaby()) {
            this.usePlayerItem(player, hand, stack);
            this.ageUp(getAge() + 200);
            return InteractionResult.SUCCESS;
        }

        // Bartering system - only adults and alphas
        if (!this.isBaby() && isBarterCurrency(stack) && barterCooldown <= 0) {
            if (!this.level().isClientSide) {
                // Take the currency item
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                // Start examining animation
                isExaminingItem = true;
                examineTicks = 40; // 2 seconds
                this.getNavigation().stop();
                this.getLookControl().setLookAt(player, 30.0F, 30.0F);

                // Play sound
                this.playSound(SoundEvents.PANDA_BITE, 1.0F, 1.0F);

                barterCooldown = 200; // 10 second cooldown
            }
            return InteractionResult.SUCCESS;
        }

        // Regular feeding (healing)
        if (this.isFood(stack) && !this.isBaby()) {
            this.usePlayerItem(player, hand, stack);
            this.heal(2.0F);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private boolean isBarterCurrency(ItemStack stack) {
        // Check for custom tag or specific items
        return stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("ancienthorizons", "chimp_currency")))
                || stack.is(ModItems.BANANA)
                || stack.is(Items.APPLE)
                || stack.is(Items.GOLDEN_APPLE);
    }

    private void performBarter(Player player) {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Get the appropriate loot table
            ResourceKey<LootTable> lootTableId = ModLootTables.CHIMP_BARTERING;

            LootTable lootTable = serverLevel.getServer()
                    .reloadableRegistries()
                    .getLootTable(lootTableId);

            LootParams lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.ORIGIN, this.position())
                    .create(LootContextParamSets.GIFT);

            // Generate loot
            lootTable.getRandomItems(lootParams).forEach(this::spawnAtLocation);

            // Play success sound
            this.playSound(SoundEvents.VILLAGER_YES, 1.0F, 1.0F);
        }
    }

    // Climbing mechanics
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Handle bartering examination
            if (isExaminingItem) {
                examineTicks--;
                if (examineTicks <= 0) {
                    isExaminingItem = false;
                    // Find nearest player to give item to
                    Player nearestPlayer = this.level().getNearestPlayer(this, 5.0D);
                    if (nearestPlayer != null) {
                        performBarter(nearestPlayer);
                    }
                }
            }

            // Update bartering cooldown
            if (barterCooldown > 0) {
                barterCooldown--;
            }

            // Update climbing state
            if (this.horizontalCollision) {
                BlockPos checkPos = this.blockPosition();
                BlockState state = this.level().getBlockState(checkPos);

                if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES) || state.is(BlockTags.CLIMBABLE)) {
                    this.setClimbing(true);
                    // Climb up when colliding with climbable blocks
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.15D, 0));
                }
            } else {
                this.setClimbing(false);
            }

            // Scan for other troops periodically
            if (troopScanCooldown <= 0) {
                scanForOtherTroops();
                troopScanCooldown = 100; // Check every 5 seconds
            } else {
                troopScanCooldown--;
            }

            // Promote to alpha if no alpha exists
            if (!this.isBaby() && this.getTroopRank() == TroopRank.ADULT) {
                checkForAlphaPromotion();
            }

            if (!this.level().isClientSide) {
                this.setClimbing(this.horizontalCollision);
            }
        }
    }

    // Troop diplomacy methods
    public Optional<UUID> getTroopId() {
        return this.entityData.get(TROOP_ID);
    }

    public void setTroopId(UUID troopId) {
        this.entityData.set(TROOP_ID, Optional.of(troopId));
    }

    public TroopRank getTroopRank() {
        return TroopRank.fromId(this.entityData.get(TROOP_RANK));
    }

    public void setTroopRank(TroopRank rank) {
        this.entityData.set(TROOP_RANK, rank.getId());
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    public TroopRelation getTroopRelation(UUID otherTroopId) {
        return troopRelations.getOrDefault(otherTroopId, TroopRelation.NEUTRAL);
    }

    public void setTroopRelation(UUID otherTroopId, TroopRelation relation) {
        troopRelations.put(otherTroopId, relation);
    }

    private void scanForOtherTroops() {
        Optional<UUID> myTroopId = getTroopId();
        if (myTroopId.isEmpty()) return;

        List<ChimpanzeeEntity> nearbyChimps = this.level().getEntitiesOfClass(
                ChimpanzeeEntity.class,
                new AABB(this.blockPosition()).inflate(32.0D)
        );

        for (ChimpanzeeEntity other : nearbyChimps) {
            Optional<UUID> otherTroopId = other.getTroopId();
            if (otherTroopId.isEmpty() || otherTroopId.equals(myTroopId)) continue;

            // Initialize neutral relations if first encounter
            if (!troopRelations.containsKey(otherTroopId.get())) {
                troopRelations.put(otherTroopId.get(), TroopRelation.NEUTRAL);

                // Territory overlap can cause hostility
                double distance = Math.sqrt(
                        Math.pow(this.territoryCenterX - other.territoryCenterX, 2) +
                                Math.pow(this.territoryCenterZ - other.territoryCenterZ, 2)
                );

                if (distance < territoryRadius) {
                    // Overlapping territories = enemy
                    troopRelations.put(otherTroopId.get(), TroopRelation.ENEMY);
                }
            }
        }
    }

    private void checkForAlphaPromotion() {
        Optional<UUID> myTroopId = getTroopId();
        if (myTroopId.isEmpty()) return;

        List<ChimpanzeeEntity> troopMembers = this.level().getEntitiesOfClass(
                ChimpanzeeEntity.class,
                new AABB(this.blockPosition()).inflate(48.0D)
        );

        boolean hasAlpha = false;
        for (ChimpanzeeEntity member : troopMembers) {
            if (member.getTroopId().equals(myTroopId) && member.getTroopRank() == TroopRank.ALPHA) {
                hasAlpha = true;
                break;
            }
        }

        if (!hasAlpha && this.random.nextFloat() < 0.01F) {
            this.setTroopRank(TroopRank.ALPHA);
        }
    }

    public List<ChimpanzeeEntity> getTroopMembers(double range) {
        Optional<UUID> myTroopId = getTroopId();
        if (myTroopId.isEmpty()) return new ArrayList<>();

        return this.level().getEntitiesOfClass(
                        ChimpanzeeEntity.class,
                        new AABB(this.blockPosition()).inflate(range)
                ).stream()
                .filter(e -> e.getTroopId().equals(myTroopId))
                .toList();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            // Call troop for help
            if (source.getEntity() instanceof LivingEntity attacker) {
                alertTroop(attacker);
            }
            return true;
        }
        return false;
    }

    private void alertTroop(LivingEntity threat) {
        List<ChimpanzeeEntity> troopMembers = getTroopMembers(16.0D);
        for (ChimpanzeeEntity member : troopMembers) {
            if (member != this && member.getTarget() == null) {
                member.setTarget(threat);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getTroopId().ifPresent(uuid -> tag.putUUID("TroopId", uuid));
        tag.putInt("TroopRank", getTroopRank().getId());
        tag.putInt("TerritoryCenterX", territoryCenterX);
        tag.putInt("TerritoryCenterZ", territoryCenterZ);
        tag.putInt("TerritoryRadius", territoryRadius);
        tag.putInt("BarterCooldown", barterCooldown);
        tag.putBoolean("IsExaminingItem", isExaminingItem);
        tag.putInt("ExamineTicks", examineTicks);

        // Save relations
        CompoundTag relationsTag = new CompoundTag();
        for (Map.Entry<UUID, TroopRelation> entry : troopRelations.entrySet()) {
            relationsTag.putInt(entry.getKey().toString(), entry.getValue().getId());
        }
        tag.put("TroopRelations", relationsTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TroopId")) {
            setTroopId(tag.getUUID("TroopId"));
        }
        setTroopRank(TroopRank.fromId(tag.getInt("TroopRank")));
        territoryCenterX = tag.getInt("TerritoryCenterX");
        territoryCenterZ = tag.getInt("TerritoryCenterZ");
        territoryRadius = tag.getInt("TerritoryRadius");
        barterCooldown = tag.getInt("BarterCooldown");
        isExaminingItem = tag.getBoolean("IsExaminingItem");
        examineTicks = tag.getInt("ExamineTicks");

        // Load relations
        if (tag.contains("TroopRelations")) {
            CompoundTag relationsTag = tag.getCompound("TroopRelations");
            for (String key : relationsTag.getAllKeys()) {
                try {
                    UUID troopId = UUID.fromString(key);
                    TroopRelation relation = TroopRelation.fromId(relationsTag.getInt(key));
                    troopRelations.put(troopId, relation);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    // Custom AI Goals
    public static class SitGoal extends Goal {
        private final ChimpanzeeEntity chimp;

        public SitGoal(ChimpanzeeEntity chimp) {
            this.chimp = chimp;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return chimp.isSitting();
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
            return chimp.isClimbing() && chimp.horizontalCollision;
        }

        @Override
        public void tick() {
            // Assist climbing movement
            if (chimp.getDeltaMovement().y < 0.1) {
                chimp.setDeltaMovement(chimp.getDeltaMovement().multiply(1, 0, 1).add(0, 0.1, 0));
            }
        }
    }

    public static class DefendTroopGoal extends Goal {
        private final ChimpanzeeEntity chimp;
        private final double speedModifier;
        private LivingEntity targetThreat;

        public DefendTroopGoal(ChimpanzeeEntity chimp, double speedModifier) {
            this.chimp = chimp;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (chimp.getTroopRank() == TroopRank.JUVENILE) return false;

            List<ChimpanzeeEntity> troopMembers = chimp.getTroopMembers(16.0D);
            for (ChimpanzeeEntity member : troopMembers) {
                if (member.getLastHurtByMob() != null) {
                    targetThreat = member.getLastHurtByMob();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            chimp.setTarget(targetThreat);
        }
    }

    public static class FollowTroopGoal extends Goal {
        private final ChimpanzeeEntity chimp;
        private ChimpanzeeEntity alpha;
        private final double speedModifier;
        private final float minDist;
        private final float maxDist;

        public FollowTroopGoal(ChimpanzeeEntity chimp, double speed, float minDist, float maxDist) {
            this.chimp = chimp;
            this.speedModifier = speed;
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (chimp.getTroopRank() == TroopRank.ALPHA || chimp.getTroopRank() == TroopRank.JUVENILE) {
                return false;
            }

            List<ChimpanzeeEntity> troopMembers = chimp.getTroopMembers(32.0D);
            for (ChimpanzeeEntity member : troopMembers) {
                if (member.getTroopRank() == TroopRank.ALPHA) {
                    alpha = member;
                    return chimp.distanceTo(alpha) > minDist;
                }
            }
            return false;
        }

        @Override
        public void tick() {
            if (alpha != null && chimp.distanceTo(alpha) > minDist) {
                chimp.getNavigation().moveTo(alpha, speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return alpha != null && !alpha.isDeadOrDying() && chimp.distanceTo(alpha) > minDist && chimp.distanceTo(alpha) < maxDist;
        }
    }

    public static class PatrolTerritoryGoal extends RandomStrollGoal {
        private final ChimpanzeeEntity chimp;

        public PatrolTerritoryGoal(ChimpanzeeEntity chimp, double speed) {
            super(chimp, speed);
            this.chimp = chimp;
        }

        @Override
        public boolean canUse() {
            return chimp.getTroopRank() == TroopRank.ALPHA && super.canUse();
        }

        @Override
        protected Vec3 getPosition() {
            // Patrol around territory center
            int x = chimp.territoryCenterX + chimp.random.nextInt(chimp.territoryRadius * 2) - chimp.territoryRadius;
            int z = chimp.territoryCenterZ + chimp.random.nextInt(chimp.territoryRadius * 2) - chimp.territoryRadius;
            int y = chimp.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            return new Vec3(x, y, z);
        }
    }

    public static class DefendAgainstEnemyTroopsGoal extends NearestAttackableTargetGoal<ChimpanzeeEntity> {
        private final ChimpanzeeEntity chimp;

        public DefendAgainstEnemyTroopsGoal(ChimpanzeeEntity chimp) {
            super(chimp, ChimpanzeeEntity.class, 10, true, false, null);
            this.chimp = chimp;
        }

        @Override
        public boolean canUse() {
            if (chimp.getTroopRank() == TroopRank.JUVENILE) return false;

            boolean result = super.canUse();
            if (result && target != null) {
                Optional<UUID> targetTroopId = ((ChimpanzeeEntity) target).getTroopId();
                if (targetTroopId.isPresent()) {
                    TroopRelation relation = chimp.getTroopRelation(targetTroopId.get());
                    return relation == TroopRelation.ENEMY;
                }
            }
            return false;
        }
    }

    public static class ChimpTroopData implements SpawnGroupData {
        public final UUID troopId;

        public ChimpTroopData(UUID troopId) {
            this.troopId = troopId;
        }
    }
}