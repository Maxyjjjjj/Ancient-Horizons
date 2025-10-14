package com.fungoussoup.ancienthorizons.entity.client.pheasant;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PheasantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class PheasantRenderer extends MobRenderer<PheasantEntity, PheasantModel<PheasantEntity>> {
    public PheasantRenderer(EntityRendererProvider.Context context) {
        super(context, new PheasantModel<>(context.bakeLayer(PheasantModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PheasantEntity pheasantEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/pheasant/pheasant.png");
    }

    protected float getBob(PheasantEntity livingBase, float partialTicks) {
        float f = Mth.lerp(partialTicks, livingBase.oFlap, livingBase.flap);
        float f1 = Mth.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}
