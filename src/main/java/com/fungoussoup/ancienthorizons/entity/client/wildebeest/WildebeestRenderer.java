package com.fungoussoup.ancienthorizons.entity.client.wildebeest;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.WildebeestEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WildebeestRenderer extends MobRenderer<WildebeestEntity, WildebeestModel<WildebeestEntity>> {
    public WildebeestRenderer(EntityRendererProvider.Context context) {
        super(context, new WildebeestModel<>(context.bakeLayer(WildebeestModel.LAYER_LOCATION)), 0.9f);
    }

    @Override
    public void render(WildebeestEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WildebeestEntity wildebeestEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/wildebeest/wildebeest.png");
    }
}
