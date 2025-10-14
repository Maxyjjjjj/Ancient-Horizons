package com.fungoussoup.ancienthorizons.entity.client.beluga_sturgeon;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BelugaSturgeonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BelugaSturgeonRenderer extends MobRenderer<BelugaSturgeonEntity, BelugaSturgeonModel<BelugaSturgeonEntity>> {
    public BelugaSturgeonRenderer(EntityRendererProvider.Context context) {
        super(context, new BelugaSturgeonModel<>(context.bakeLayer(BelugaSturgeonModel.LAYER_LOCATION)), 1);
    }

    @Override
    public void render(BelugaSturgeonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BelugaSturgeonEntity belugaSturgeonEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/beluga_sturgeon/beluga_sturgeon.png");
    }
}