package com.fungoussoup.ancienthorizons.entity.client.earthworm;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EarthwormEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EarthwormRenderer extends MobRenderer<EarthwormEntity, EarthwormModel<EarthwormEntity>> {
    public EarthwormRenderer(EntityRendererProvider.Context context) {
        super(context, new EarthwormModel<>(context.bakeLayer(EarthwormModel.LAYER_LOCATION)), 0f);
    }

    @Override
    public void render(EarthwormEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EarthwormEntity earthwormEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/earthworm/earthworm.png");
    }
}
