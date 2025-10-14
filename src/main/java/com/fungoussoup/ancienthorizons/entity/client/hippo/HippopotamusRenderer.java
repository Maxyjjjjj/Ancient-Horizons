package com.fungoussoup.ancienthorizons.entity.client.hippo;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HippopotamusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HippopotamusRenderer extends MobRenderer<HippopotamusEntity, HippopotamusModel<HippopotamusEntity>> {
    public HippopotamusRenderer(EntityRendererProvider.Context context) {
        super(context, new HippopotamusModel<>(context.bakeLayer(HippopotamusModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(HippopotamusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HippopotamusEntity hippopotamusEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/hippopotamus/hippopotamus.png");
    }
}
