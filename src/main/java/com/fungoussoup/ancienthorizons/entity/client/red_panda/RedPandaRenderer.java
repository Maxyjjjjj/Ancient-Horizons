package com.fungoussoup.ancienthorizons.entity.client.red_panda;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.client.saichania.SaichaniaModel;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RedPandaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RedPandaRenderer extends MobRenderer<RedPandaEntity, RedPandaModel<RedPandaEntity>>{
    public RedPandaRenderer(EntityRendererProvider.Context context) {
        super(context, new RedPandaModel<>(context.bakeLayer(RedPandaModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(RedPandaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RedPandaEntity redPandaEntity) {
        return null;
    }
}