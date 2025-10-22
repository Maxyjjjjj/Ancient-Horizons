package com.fungoussoup.ancienthorizons.entity.client.merganser;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MerganserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MerganserRenderer extends MobRenderer<MerganserEntity, MerganserModel<MerganserEntity>> {
    public MerganserRenderer(EntityRendererProvider.Context context) {
        super(context, new MerganserModel<>(context.bakeLayer(MerganserModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public void render(MerganserEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MerganserEntity merganserEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/merganser/merganser.png");
    }
}
