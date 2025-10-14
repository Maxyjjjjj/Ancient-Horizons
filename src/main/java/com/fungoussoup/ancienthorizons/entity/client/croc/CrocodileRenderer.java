package com.fungoussoup.ancienthorizons.entity.client.croc;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.CrocodileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrocodileRenderer extends MobRenderer<CrocodileEntity, CrocodileModel<CrocodileEntity>> {
    public CrocodileRenderer(EntityRendererProvider.Context context) {
        super(context, new CrocodileModel<>(context.bakeLayer(CrocodileModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(CrocodileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrocodileEntity crocodileEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/crocodile/crocodile.png");
    }
}