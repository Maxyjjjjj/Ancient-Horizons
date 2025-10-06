package com.fungoussoup.ancienthorizons.entity.client.tiger;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.util.TigerVariant;
import com.fungoussoup.ancienthorizons.entity.custom.mob.TigerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TigerRenderer extends MobRenderer<TigerEntity, TigerModel<TigerEntity>> {
    private static final ResourceLocation TIGER_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/tiger/tiger.png");
    private static final ResourceLocation TIGER_WHITE_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/tiger/tiger_white.png");
    private static final ResourceLocation TIGER_GOLDEN_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/tiger/tiger_golden.png");
    private static final ResourceLocation TIGER_BLUE_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/tiger/tiger_maltese.png");
    private static final ResourceLocation TIGER_LEGENDS_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/tiger/tiger_legends.png");

    public ResourceLocation getTextureLocation(TigerEntity entity) {
        TigerVariant variant = entity.getVariant();
        String name = ChatFormatting.stripFormatting(entity.getName().getString());
        if ("Legends".equals(name)) {
            return TIGER_LEGENDS_LOCATION;
        } else if (variant == TigerVariant.WHITE) {
            return TIGER_WHITE_LOCATION;
        } else if (variant == TigerVariant.GOLDEN) {
            return TIGER_GOLDEN_LOCATION;
        } else if (variant == TigerVariant.BLUE) {
            return TIGER_BLUE_LOCATION;
        } else {
            return TIGER_LOCATION;
        }
    }

    public TigerRenderer(EntityRendererProvider.Context context) {
        super(context, new TigerModel<>(context.bakeLayer(TigerModel.LAYER_LOCATION)), 0.75f);
        this.addLayer(new TigerCollarLayer(this, context.getModelSet()));
    }

    @Override
    public void render(TigerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
