package com.fungoussoup.ancienthorizons.entity.client.maip;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MaipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MaipRenderer extends MobRenderer<MaipEntity, MaipModel<MaipEntity>> {

    private static final ResourceLocation MAIP_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/maip/maip.png");

    public MaipRenderer(EntityRendererProvider.Context context) {
        super(context, new MaipModel<>(context.bakeLayer(MaipModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(MaipEntity entity) {
        return MAIP_TEXTURE;
    }

    @Override
    public void render(MaipEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }
}
