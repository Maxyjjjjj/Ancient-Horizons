package com.fungoussoup.ancienthorizons.entity.client.hare;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HareEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HareRenderer extends MobRenderer<HareEntity, HareModel<HareEntity>> {
    private static final ResourceLocation TEMPERATE_TEXTURE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/hare/hare_brown.png");
    private static final ResourceLocation COLD_TEXTURE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/hare/hare_white.png");

    public HareRenderer(EntityRendererProvider.Context context) {
        super(context, new HareModel<>(context.bakeLayer(HareModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public void render(HareEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HareEntity hare) {
        if (hare.getVariant() == HareEntity.Type.BROWN) {
            return TEMPERATE_TEXTURE;
        } else {
            return COLD_TEXTURE;
        }
    }
}
