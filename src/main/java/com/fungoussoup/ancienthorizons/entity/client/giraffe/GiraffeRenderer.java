package com.fungoussoup.ancienthorizons.entity.client.giraffe;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.GiraffeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GiraffeRenderer extends MobRenderer<GiraffeEntity, GiraffeModel<GiraffeEntity>> {

    public GiraffeRenderer(EntityRendererProvider.Context context) {
        super(context, new GiraffeModel<>(context.bakeLayer(GiraffeModel.LAYER_LOCATION)), 1.2f);
    }

    @Override
    public void render(GiraffeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GiraffeEntity giraffeEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/giraffe/giraffe.png");
    }
}
