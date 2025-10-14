package com.fungoussoup.ancienthorizons.entity.client.penguin;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PenguinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PenguinRenderer extends MobRenderer<PenguinEntity, PenguinModel<PenguinEntity>> {

    public PenguinRenderer(EntityRendererProvider.Context context) {
        super(context, new PenguinModel<>(context.bakeLayer(PenguinModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public void render(PenguinEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PenguinEntity penguinEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/penguin/penguin.png");
    }
}
