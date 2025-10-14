package com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda;

import com.fungoussoup.ancienthorizons.entity.interfaces.HurtableMultipart;
import com.fungoussoup.ancienthorizons.entity.util.AnacondaPartIndex;
import com.fungoussoup.ancienthorizons.misc.AHBlockPos;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class AnacondaPartEntity extends LivingEntity implements HurtableMultipart {
    private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BODY_TYPE = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> SWELL = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.FLOAT);
    public EntityDimensions multipartSize;
    private float strangleProgess;
    private float prevSwell;
    private float prevStrangleProgess;
    private int headEntityId = -1;
    private double prevHeight = 0;
    private static final EntityDataAccessor<Boolean> BABY = SynchedEntityData.defineId(AnacondaPartEntity.class, EntityDataSerializers.BOOLEAN);

    public AnacondaPartEntity(EntityType<? extends LivingEntity> entityType, LivingEntity parent) {
        super(entityType, parent.level());
        this.setParent(parent);
    }

    public AnacondaPartEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
        multipartSize = entityType.getDimensions();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHILD_UUID, Optional.empty());
        builder.define(PARENT_UUID, Optional.empty());
        builder.define(BODYINDEX, 0);
        builder.define(BODY_TYPE, AnacondaPartIndex.BODY.ordinal());
        builder.define(TARGET_YAW, 0F);
        builder.define(SWELL, 0F);
        builder.define(BABY, false);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL)  || super.isInvulnerableTo(source);
    }

    public boolean isNoGravity() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        prevStrangleProgess = strangleProgess;
        prevSwell = this.getSwell();
        this.setDeltaMovement(Vec3.ZERO);
        if (this.tickCount > 1) {
            final Entity parent = getParent();
            refreshDimensions();
            if (!this.level().isClientSide) {
                if (parent == null) {
                    this.remove(RemovalReason.DISCARDED);
                }
                if (parent != null) {
                    if (parent instanceof final LivingEntity livingEntityParent) {
                        if (livingEntityParent.hurtTime > 0 || livingEntityParent.deathTime > 0) {
                            this.hurtTime = livingEntityParent.hurtTime;
                            this.deathTime = livingEntityParent.deathTime;
                        }
                    }
                    if (parent.isRemoved()) {
                        this.remove(RemovalReason.DISCARDED);
                    }
                } else if (tickCount > 20) {
                    remove(RemovalReason.DISCARDED);
                }
                if (this.getSwell() > 0) {
                    final float swellInc = 0.25F;
                    if (parent instanceof AnacondaEntity || parent instanceof AnacondaPartEntity && ((AnacondaPartEntity) parent).getSwell() == 0) {
                        this.setSwell(this.getSwell() - swellInc);
                    }
                }
            }
        }
    }

    public Vec3 tickMultipartPosition(int headId, AnacondaPartIndex parentIndex, Vec3 parentPosition,
                                      float parentXRot, float ourYRot, boolean doUpdate) {
        this.headEntityId = headId;

        final double segmentLength = parentIndex.getBackOffset() * 2.0 * this.getScale();

        Vec3 prevPosition = this.position();

        Vec3 offsetVec = calcOffsetVec(-segmentLength, 0, ourYRot);

        Vec3 targetPosition = parentPosition.add(offsetVec);

        float yRotDiff = Mth.wrapDegrees(ourYRot - this.getYRot());

        float rotationLag = 0.15F;
        this.setYRot(this.getYRot() + yRotDiff * rotationLag);

        float xRotDiff = Mth.wrapDegrees(parentXRot - this.getXRot());
        this.setXRot(this.getXRot() + xRotDiff * 0.2F);

        if (doUpdate) {
            double lerpFactor = 0.25;

            double x = Mth.lerp(lerpFactor, prevPosition.x, targetPosition.x);
            double y = Mth.lerp(lerpFactor, prevPosition.y, targetPosition.y);
            double z = Mth.lerp(lerpFactor, prevPosition.z, targetPosition.z);

            this.setPos(x, y, z);
        }
        return this.position();
    }


    private Vec3 calcOffsetVec(double distance, float xRot, float yRot) {
        float xRotRad = xRot * Mth.DEG_TO_RAD;
        float yRotRad = yRot * Mth.DEG_TO_RAD;

        double dX = distance * Mth.sin(yRotRad) * Mth.cos(xRotRad);
        double dY = -distance * Mth.sin(xRotRad);
        double dZ = distance * Mth.cos(yRotRad) * Mth.cos(xRotRad);

        double x = distance * -Mth.sin(yRotRad);
        double z = distance * Mth.cos(yRotRad);

        return new Vec3(x, 0, z);
    }
    public double getLowPartHeight(double x, double yIn, double z) {
        if (isFluidAt(x, yIn, z))
            return 0.0D;

        double checkAt = 0D;
        while (checkAt > -3D && !isOpaqueBlockAt(x,yIn + checkAt, z)) {
            checkAt -= 0.2D;
        }

        return checkAt;
    }

    public double getHighPartHeight(double x, double yIn, double z) {
        if (isFluidAt(x, yIn, z))
            return 0.0D;

        double checkAt = 0D;
        while (checkAt <= 3D) {
            if (isOpaqueBlockAt(x, yIn + checkAt, z)) {
                checkAt += 0.2D;
            } else {
                break;
            }
        }

        return checkAt;
    }


    public boolean isOpaqueBlockAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            final double d = 1D;
            final Vec3 vec3 = new Vec3(x, y, z);
            final AABB axisAlignedBB = AABB.ofSize(vec3, d, 1.0E-6D, d);
            return this.level().getBlockStates(axisAlignedBB).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
                BlockPos blockpos = AHBlockPos.fromVec3(vec3);
                return p_185969_.isSuffocating(this.level(), blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisAlignedBB), BooleanOp.AND);
            });
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean isFluidAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            return !level().getFluidState(AHBlockPos.fromCoords(x, y, z)).isEmpty();
        }
    }

    public boolean hurtHeadId(DamageSource source, float f) {
        if (headEntityId != -1) {
            Entity e = level().getEntity(headEntityId);
            if (e instanceof AnacondaEntity) {
                return e.hurt(source, f);
            }
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return hurtHeadId(source, damage);
    }

    public void pushEntities() {
        final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        final Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> !entity.is(parent) && !(entity instanceof AnacondaPartEntity) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        return this.getParent() == null ? super.interact(player, hand) : this.getParent().interact(player, hand);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        if (parent.deathTime > 0)
            this.deathTime = parent.deathTime;

        if (parent.hurtTime > 0)
            this.hurtTime = parent.hurtTime;
    }

    public Entity getParent() {
        if (!this.level().isClientSide) {
            final UUID id = getParentId();
            if (id != null) {
                return ((ServerLevel) level()).getEntity(id);
            }
        }

        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUUID());
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getChild() {
        if (!this.level().isClientSide) {
            final UUID id = getChildId();
            if (id != null) {
                return ((ServerLevel) level()).getEntity(id);
            }
        }

        return null;
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.putUUID("ParentUUID", this.getParentId());
        }
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
        compound.putInt("BodyModel", getPartType().ordinal());
        compound.putInt("BodyIndex", getBodyIndex());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ParentUUID")) {
            this.setParentId(compound.getUUID("ParentUUID"));
        }
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
        this.setPartType(AnacondaPartIndex.fromOrdinal(compound.getInt("BodyModel")));
        this.setBodyIndex(compound.getInt("BodyIndex"));
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        Entity parent = this.getParent();
        return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
    }

    public int getBodyIndex() {
        return this.entityData.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.entityData.set(BODYINDEX, index);
    }

    public AnacondaPartIndex getPartType() {
        return AnacondaPartIndex.fromOrdinal(this.entityData.get(BODY_TYPE));
    }

    public void setPartType(AnacondaPartIndex index) {
        this.entityData.set(BODY_TYPE, index.ordinal());
    }

    public void setTargetYaw(float f) {
        this.entityData.set(TARGET_YAW, f);
    }

    public void setSwell(float f) {
        this.entityData.set(SWELL, f);
    }

    public float getSwell(){
        return Math.min(this.entityData.get(SWELL), 5);
    }


    public float getSwellLerp(float partialTick) {
        return this.prevSwell + (Math.max(this.getSwell(), 0) - this.prevSwell) * partialTick;
    }


    @Override
    public float getYRot() {
        return super.getYRot();
    }

    public void setStrangleProgress(float f){
        this.strangleProgess = f;
    }

    public float getStrangleProgress(float partialTick){
        return this.prevStrangleProgess + (this.strangleProgess - this.prevStrangleProgess) * partialTick;
    }

    public void copyDataFrom(AnacondaEntity anaconda) {
        this.entityData.set(BABY, anaconda.isBaby());
    }

    @Override
    public boolean isBaby(){
        return this.entityData.get(BABY);
    }
}