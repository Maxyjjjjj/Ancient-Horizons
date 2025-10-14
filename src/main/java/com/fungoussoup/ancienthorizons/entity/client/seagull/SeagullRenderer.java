package com.fungoussoup.ancienthorizons.entity.client.seagull;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SeagullEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SeagullRenderer extends MobRenderer<SeagullEntity, SeagullModel<SeagullEntity>> {
    public SeagullRenderer(EntityRendererProvider.Context context) {
        super(context, new SeagullModel<>(context.bakeLayer(SeagullModel.LAYER_LOCATION)), 0.8f);
    }

    @Override
    public void render(SeagullEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SeagullEntity seagullEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/seagull/seagull.png");
    }
}
