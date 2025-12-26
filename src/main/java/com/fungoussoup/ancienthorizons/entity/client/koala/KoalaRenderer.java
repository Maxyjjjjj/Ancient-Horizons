package com.fungoussoup.ancienthorizons.entity.client.koala;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.KoalaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KoalaRenderer extends MobRenderer<KoalaEntity, KoalaModel<KoalaEntity>> {
    public KoalaRenderer(EntityRendererProvider.Context context) {
        super(context, new KoalaModel<>(context.bakeLayer(KoalaModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public void render(KoalaEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(KoalaEntity koalaEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/koala/koala.png");
    }
}
