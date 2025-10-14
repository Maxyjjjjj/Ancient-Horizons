package com.fungoussoup.ancienthorizons.entity.client.fisher;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.FisherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FisherRenderer extends MobRenderer<FisherEntity, FisherModel<FisherEntity>> {
    public FisherRenderer(EntityRendererProvider.Context context) {
        super(context, new FisherModel<>(context.bakeLayer(FisherModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new FisherHeldMouthItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public void render(FisherEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FisherEntity fisherEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/fisher/fisher.png");
    }
}
