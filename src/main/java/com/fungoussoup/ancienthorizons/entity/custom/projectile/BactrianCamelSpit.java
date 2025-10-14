package com.fungoussoup.ancienthorizons.entity.custom.projectile;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BactrianCamelSpit extends LlamaSpit {
    public BactrianCamelSpit(EntityType<? extends BactrianCamelSpit> type, Level level) {
        super(type, level);
    }

    public BactrianCamelSpit(Level level, LivingEntity owner) {
        this(ModEntities.BACTRIAN_CAMEL_SPIT.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        // Add a particle effect so the spit is visible
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity owner = this.getOwner();
        // Damage the entity that was hit
        if (owner instanceof LivingEntity livingOwner) {
            pResult.getEntity().hurt(this.damageSources().mobProjectile(this, livingOwner), 1.0F);
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        // Discard the entity once it hits anything
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}

