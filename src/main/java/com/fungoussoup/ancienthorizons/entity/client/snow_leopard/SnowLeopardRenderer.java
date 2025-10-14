package com.fungoussoup.ancienthorizons.entity.client.snow_leopard;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SnowLeopardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SnowLeopardRenderer extends MobRenderer<SnowLeopardEntity, SnowLeopardModel<SnowLeopardEntity>> {

    private static final ResourceLocation DEFAULT = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/snow_leopard/snow_leopard.png");
    private static final ResourceLocation BLUE_EYES = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/snow_leopard/snow_leopard_blue_eyes.png");
    private static final ResourceLocation TAI_LUNG = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/snow_leopard/tai_lung.png");

    public SnowLeopardRenderer(EntityRendererProvider.Context context) {
        super(context, new SnowLeopardModel<>(context.bakeLayer(SnowLeopardModel.LAYER_LOCATION)), 0.75f);
        this.addLayer(new SnowLeopardCollarLayer(this, context.getModelSet()));
        this.addLayer(new SnowLeopardArmourLayer(this, context.getModelSet()));
    }

    @Override
    public void render(SnowLeopardEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,  MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SnowLeopardEntity entity) {
        String s = ChatFormatting.stripFormatting(entity.getName().getString());
        if ("Tai Lung".equals(s)) {
            return TAI_LUNG;
        } else {
            return entity.isBlueEyes() ? BLUE_EYES : DEFAULT;
        }
    }
}
