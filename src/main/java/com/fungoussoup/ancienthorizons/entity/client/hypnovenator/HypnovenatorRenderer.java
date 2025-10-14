package com.fungoussoup.ancienthorizons.entity.client.hypnovenator;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HypnovenatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HypnovenatorRenderer extends MobRenderer<HypnovenatorEntity, HypnovenatorModel<HypnovenatorEntity>> {
    public HypnovenatorRenderer(EntityRendererProvider.Context context) {
        super(context, new HypnovenatorModel<>(context.bakeLayer(HypnovenatorModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(HypnovenatorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HypnovenatorEntity hypnovenatorEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/hypnovenator/hypnovenator.png");
    }
}