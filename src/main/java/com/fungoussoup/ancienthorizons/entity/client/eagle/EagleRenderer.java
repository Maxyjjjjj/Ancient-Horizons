package com.fungoussoup.ancienthorizons.entity.client.eagle;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EagleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EagleRenderer extends MobRenderer<EagleEntity, EagleModel<EagleEntity>> {
    public EagleRenderer(EntityRendererProvider.Context context) {
        super(context, new EagleModel<>(context.bakeLayer(EagleModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(EagleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EagleEntity eagleEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/eagle/eagle.png");
    }
}
