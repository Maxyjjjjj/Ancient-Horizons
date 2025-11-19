package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ZebraEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public class HorseMateWithZebra {
    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    private void canMateWithZebraMixin(Animal otherAnimal, CallbackInfoReturnable<Boolean> cir) {
        if (otherAnimal instanceof ZebraEntity) {
            cir.setReturnValue(true); // Allow mating with zebras
        }
    }

    @Inject(method = "getBreedOffspring", at = @At("HEAD"), cancellable = true)
    private void getBreedOffspringMixin(ServerLevel level, AgeableMob otherParent, CallbackInfoReturnable<AgeableMob> cir) {
        EntityType<? extends AbstractHorse> entitytype = otherParent instanceof Donkey ? EntityType.MULE : otherParent instanceof ZebraEntity ? ModEntities.ZORSE.get() : EntityType.HORSE;
        AgeableMob baby = entitytype.create(level);
        cir.setReturnValue(baby);
    }
}
