package com.fungoussoup.ancienthorizons.entity.client.elephant;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ElephantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElephantRenderer extends MobRenderer<ElephantEntity, ElephantModel<ElephantEntity>> {
    public ElephantRenderer(EntityRendererProvider.Context context) {
        super(context, new ElephantModel<>(context.bakeLayer(ElephantModel.LAYER_LOCATION)), 1.5f);
    }

    @Override
    public void render(ElephantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElephantEntity elephantEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/elephant/elephant.png");
    }
}
