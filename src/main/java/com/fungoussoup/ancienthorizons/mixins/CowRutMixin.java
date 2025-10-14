package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.interfaces.Rutting;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Cow.class)
public class CowRutMixin extends Animal implements Rutting {

    @Unique
    private static final EntityDataAccessor<Boolean> IN_RUT = SynchedEntityData.defineId(CowRutMixin.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Integer> RUT_INTENSITY = SynchedEntityData.defineId(CowRutMixin.class, EntityDataSerializers.INT);

    protected CowRutMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_RUT, false);
        builder.define(RUT_INTENSITY, 0);
    }

    @Override
    public boolean ancient_Horizons$isInRut() {
        return this.entityData.get(IN_RUT);
    }

    @Override
    public void ancient_Horizons$setInRut(boolean rutting) {
        this.entityData.set(IN_RUT, rutting);
    }

    @Override
    public int ancient_Horizons$getRutIntensity() {
        return this.entityData.get(RUT_INTENSITY);
    }

    @Override
    public void ancient_Horizons$setRutIntensity(int value) {
        this.entityData.set(RUT_INTENSITY, value);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.COW_FOOD);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.ancient_Horizons$isInRut()) {
            return ancient_Horizons$getRuttingSound();
        } else {
            return SoundEvents.COW_AMBIENT;
        }
    }

    @Unique
    private SoundEvent ancient_Horizons$getRuttingSound() {
        return ModSoundEvents.COW_RUT;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return EntityType.COW.create(serverLevel);
    }
}

