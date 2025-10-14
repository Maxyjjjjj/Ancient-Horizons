package com.fungoussoup.ancienthorizons.entity.client.gallimimus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.GallimimusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GallimimusRenderer extends MobRenderer<GallimimusEntity, GallimimusModel<GallimimusEntity>> {
    public GallimimusRenderer(EntityRendererProvider.Context context) {
        super(context, new GallimimusModel<>(context.bakeLayer(GallimimusModel.LAYER_LOCATION)), 1.4f);
    }

    @Override
    public void render(GallimimusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GallimimusEntity gallimimusEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/gallimimus/gallimimus.png");
    }
}
