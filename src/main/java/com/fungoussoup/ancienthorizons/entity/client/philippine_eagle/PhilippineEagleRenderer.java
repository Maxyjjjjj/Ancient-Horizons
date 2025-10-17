package com.fungoussoup.ancienthorizons.entity.client.philippine_eagle;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PhilippineEagleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PhilippineEagleRenderer extends MobRenderer<PhilippineEagleEntity, PhilippineEagleModel<PhilippineEagleEntity>> {
    public PhilippineEagleRenderer(EntityRendererProvider.Context context) {
        super(context, new PhilippineEagleModel<>(context.bakeLayer(PhilippineEagleModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public void render(PhilippineEagleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PhilippineEagleEntity philippineEagleEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/philippine_eagle/philippine_eagle.png");
    }
}
