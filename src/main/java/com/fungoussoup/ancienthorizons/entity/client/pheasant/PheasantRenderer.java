package com.fungoussoup.ancienthorizons.entity.client.pheasant;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PheasantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PheasantRenderer extends MobRenderer<PheasantEntity, PheasantModel<PheasantEntity>> {
    public PheasantRenderer(EntityRendererProvider.Context context) {
        super(context, new PheasantModel<>(context.bakeLayer(PheasantModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(PheasantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PheasantEntity pheasantEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/pheasant/pheasant.png");
    }
}
