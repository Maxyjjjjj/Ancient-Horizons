package com.fungoussoup.ancienthorizons.entity.client.saola;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SaolaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SaolaRenderer extends MobRenderer<SaolaEntity, SaolaModel<SaolaEntity>> {
    public SaolaRenderer(EntityRendererProvider.Context context) {
        super(context, new SaolaModel<>(context.bakeLayer(SaolaModel.LAYER_LOCATION)), 0.9f);
    }

    @Override
    public void render(SaolaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SaolaEntity saolaEntity) {
        String name = ChatFormatting.stripFormatting(saolaEntity.getName().getString());
        if ("Martha".equals(name)) {
            return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/saola/martha.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/saola/saola.png");
        }
    }
}