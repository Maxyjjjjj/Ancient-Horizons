package com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.azhdarchidae.CryodrakonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CryodrakonRenderer extends MobRenderer<CryodrakonEntity, CryodrakonModel<CryodrakonEntity>> {
    public CryodrakonRenderer(EntityRendererProvider.Context context) {
        super(context, new CryodrakonModel<>(context.bakeLayer(CryodrakonModel.LAYER_LOCATION)), 1f);
    }

    @Override
    public void render(CryodrakonEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CryodrakonEntity cryodrakonEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/large_pterosaur/cryodrakon/cryodrakon.png");
    }
}
