package com.fungoussoup.ancienthorizons.entity.custom.mob.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

import java.util.Objects;

public abstract class HeadRidingEntity extends TamableAnimal {
    private static final int RIDE_COOLDOWN = 100;
    private int rideCooldownCounter;

    protected HeadRidingEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public boolean setEntityOnHead(ServerPlayer player) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("id", Objects.requireNonNull(this.getEncodeId()));
        this.saveWithoutId(compoundtag);
        if (player.setEntityOnShoulder(compoundtag)) {
            this.discard();
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        ++this.rideCooldownCounter;
        super.tick();
    }

    public boolean canSitOnHead() {
        return this.rideCooldownCounter > 100;
    }
}