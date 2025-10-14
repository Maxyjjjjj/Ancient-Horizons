package com.fungoussoup.ancienthorizons.entity.client.velociraptor;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.VelociraptorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class VelociraptorRenderer extends MobRenderer<VelociraptorEntity, VelociraptorModel<VelociraptorEntity>> {
    public VelociraptorRenderer(EntityRendererProvider.Context context) {
        super(context, new VelociraptorModel<>(context.bakeLayer(VelociraptorModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(VelociraptorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VelociraptorEntity velociraptorEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/velociraptor/velociraptor.png");
    }
}