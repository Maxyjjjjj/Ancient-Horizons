package com.fungoussoup.ancienthorizons.entity.client.carcharodon;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.WhiteSharkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WhiteSharkRenderer extends MobRenderer<WhiteSharkEntity, WhiteSharkModel<WhiteSharkEntity>> {
    public WhiteSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteSharkModel<>(context.bakeLayer(WhiteSharkModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(WhiteSharkEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteSharkEntity whiteSharkEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/white_shark/white_shark.png");
    }
}
