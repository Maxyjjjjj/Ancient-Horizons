package com.fungoussoup.ancienthorizons.entity.client.abstract_passerine;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.passerine.ReedlingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ReedlingRenderer extends MobRenderer<ReedlingEntity, AbstractPasserineModel<ReedlingEntity>> {

    public ReedlingRenderer(EntityRendererProvider.Context context) {
        super(context, new AbstractPasserineModel<>(context.bakeLayer(AbstractPasserineModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(ReedlingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ReedlingEntity reedling) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/passerine/reedling.png");
    }
}
