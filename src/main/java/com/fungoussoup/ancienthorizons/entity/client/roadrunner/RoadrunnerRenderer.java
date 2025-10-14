package com.fungoussoup.ancienthorizons.entity.client.roadrunner;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RoadrunnerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RoadrunnerRenderer extends MobRenderer<RoadrunnerEntity, RoadrunnerModel<RoadrunnerEntity>> {
    private static final ResourceLocation ROADRUNNER_TEXTURE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/roadrunner/roadrunner.png");
    private static final ResourceLocation MEEP_MEEP = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/roadrunner/meepmeep.png");

    public RoadrunnerRenderer(EntityRendererProvider.Context context) {
        super(context, new RoadrunnerModel<>(context.bakeLayer(RoadrunnerModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public void render(RoadrunnerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RoadrunnerEntity roadrunner) {
        String name = ChatFormatting.stripFormatting(roadrunner.getName().getString());
        if ("Meep Meep".equalsIgnoreCase(name)) {
            return MEEP_MEEP;
        } else {
            return ROADRUNNER_TEXTURE;
        }
    }
}