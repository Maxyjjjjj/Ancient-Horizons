package com.fungoussoup.ancienthorizons.entity.client.dearc;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.DearcEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DearcRenderer extends MobRenderer<DearcEntity, DearcModel<DearcEntity>> {
    public static final ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/dearc/dearc.png");
    public static final ResourceLocation ARISTOCRAT = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/dearc/dearc_aristocrat.png");
    public DearcRenderer(EntityRendererProvider.Context context) {
        super(context, new DearcModel<>(context.bakeLayer(DearcModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(DearcEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(DearcEntity entity) {
        String name = ChatFormatting.stripFormatting(entity.getName().getString());
        if ("Aristocracy_".equals(name)) {
            return ARISTOCRAT;
        } else {
            return NORMAL;
        }
    }
}
