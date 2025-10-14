package com.fungoussoup.ancienthorizons.entity.client.brown_bear;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BrownBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BrownBearRenderer extends MobRenderer<BrownBearEntity, BrownBearModel<BrownBearEntity>> {

    public static final ResourceLocation AGGRESSIVE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/aggressive_brown_bear.png");
    public static final ResourceLocation LAZY = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/lazy_brown_bear.png");
    public static final ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/brown_bear.png");
    public static final ResourceLocation WEAK = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/weak_brown_bear.png");
    public static final ResourceLocation WINNIE_THE_POOH = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/winnie_the_pooh_bear.png");
    public static final ResourceLocation WORRIED = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/brown_bear/worried_brown_bear.png");

    public BrownBearRenderer(EntityRendererProvider.Context context) {
        super(context, new BrownBearModel<>(context.bakeLayer(BrownBearModel.LAYER_LOCATION)), 1.2f);
    }

    @Override
    public void render(BrownBearEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(BrownBearEntity entity) {
        BrownBearEntity.Gene gene = entity.getGene(); // <- get gene from entity
        return switch (gene) {
            case WINNIETHEPOOH -> WINNIE_THE_POOH;
            case AGGRESSIVE -> AGGRESSIVE;
            case LAZY -> LAZY;
            case WORRIED -> WORRIED;
            case WEAK -> WEAK;
            default -> NORMAL;
        };
    }
}