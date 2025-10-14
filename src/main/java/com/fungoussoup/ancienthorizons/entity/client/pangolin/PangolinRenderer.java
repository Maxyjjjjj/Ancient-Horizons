package com.fungoussoup.ancienthorizons.entity.client.pangolin;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PangolinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PangolinRenderer extends MobRenderer<PangolinEntity, PangolinModel<PangolinEntity>> {

    public PangolinRenderer(EntityRendererProvider.Context context) {
        super(context, new PangolinModel<>(context.bakeLayer(PangolinModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(PangolinEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PangolinEntity pangolin) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/pangolin/pangolin.png");
    }
}
