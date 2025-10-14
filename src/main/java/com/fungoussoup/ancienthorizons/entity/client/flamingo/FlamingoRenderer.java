package com.fungoussoup.ancienthorizons.entity.client.flamingo;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.FlamingoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FlamingoRenderer extends MobRenderer<FlamingoEntity, FlamingoModel<FlamingoEntity>> {

    private static final ResourceLocation FLAMINGO_WHITE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/flamingo/flamingo_white.png");

    private static final ResourceLocation FLAMINGO_PALE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/flamingo/flamingo_pale.png");

    private static final ResourceLocation FLAMINGO_PINK = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/flamingo/flamingo_pink.png");

    private static final ResourceLocation FLAMINGO_RED = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/flamingo/flamingo_red.png");

    public FlamingoRenderer(EntityRendererProvider.Context context) {
        super(context, new FlamingoModel<>(context.bakeLayer(FlamingoModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public void render(FlamingoEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FlamingoEntity flamingo) {
        if (flamingo.getVariant() == FlamingoEntity.VARIANT_WHITE){
            return FLAMINGO_WHITE;
        } else if (flamingo.getVariant() == FlamingoEntity.VARIANT_PALE) {
            return FLAMINGO_PALE;
        } else if (flamingo.getVariant() == FlamingoEntity.VARIANT_PINK) {
            return FLAMINGO_PINK;
        } else {
            return FLAMINGO_RED;
        }
    }
}
