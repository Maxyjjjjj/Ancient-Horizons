package com.fungoussoup.ancienthorizons.entity.client.roe_deer;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RoeDeerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RoeDeerRenderer extends MobRenderer<RoeDeerEntity, RoeDeerModel<RoeDeerEntity>> {
    public RoeDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new RoeDeerModel<>(context.bakeLayer(RoeDeerModel.LAYER_LOCATION)), 0.45f);
    }

    @Override
    public void render(RoeDeerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RoeDeerEntity roeDeerEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/roe_deer/roe_deer.png");
    }
}
