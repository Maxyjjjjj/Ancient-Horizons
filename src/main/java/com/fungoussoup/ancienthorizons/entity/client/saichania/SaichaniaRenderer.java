package com.fungoussoup.ancienthorizons.entity.client.saichania;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SaichaniaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SaichaniaRenderer extends MobRenderer<SaichaniaEntity, SaichaniaModel<SaichaniaEntity>> {
    public SaichaniaRenderer(EntityRendererProvider.Context context) {
        super(context, new SaichaniaModel<>(context.bakeLayer(SaichaniaModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(SaichaniaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Handle baby scaling
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SaichaniaEntity saichaniaEntity) {
        return ResourceLocation.fromNamespaceAndPath(
                AncientHorizons.MOD_ID, "textures/entity/saichania/saichania.png");
    }
}
