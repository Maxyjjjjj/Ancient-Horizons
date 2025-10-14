package com.fungoussoup.ancienthorizons.entity.client.chimp;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ChimpanzeeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChimpanzeeRenderer extends MobRenderer<ChimpanzeeEntity, ChimpanzeeModel<ChimpanzeeEntity>> {
    public ChimpanzeeRenderer(EntityRendererProvider.Context context) {
        super(context, new ChimpanzeeModel<>(context.bakeLayer(ChimpanzeeModel.LAYER_LOCATION)), 0.6f);
    }

    @Override
    public void render(ChimpanzeeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f,0.5f,0.5f);
        } else {
            poseStack.scale(1f,1f,1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ChimpanzeeEntity chimpanzeeEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/chimpanzee/chimpanzee.png");
    }
}