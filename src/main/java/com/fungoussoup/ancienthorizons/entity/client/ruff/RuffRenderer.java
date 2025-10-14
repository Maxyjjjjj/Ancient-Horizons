package com.fungoussoup.ancienthorizons.entity.client.ruff;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RuffEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RuffRenderer extends MobRenderer<RuffEntity, RuffModel<RuffEntity>> {
    private static final ResourceLocation INDEPENDENT = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/ruff/ruff_independent.png");
    private static final ResourceLocation SATELLITE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/ruff/ruff_satellite.png");
    private static final ResourceLocation NON_BREEDING = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/ruff/ruff_non_breeding.png");

    public RuffRenderer(EntityRendererProvider.Context context) {
        super(context, new RuffModel<>(context.bakeLayer(RuffModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(RuffEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(RuffEntity ruff) {
        if (ruff.getBreedingRole() == RuffEntity.BreedingRole.INDEPENDENT) {
            return INDEPENDENT;
        } else if (ruff.getBreedingRole() == RuffEntity.BreedingRole.SATELLITE) {
            return SATELLITE;
        } else {
            return NON_BREEDING;
        }
    }
}