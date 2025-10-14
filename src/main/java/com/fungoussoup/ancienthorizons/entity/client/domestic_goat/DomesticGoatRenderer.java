package com.fungoussoup.ancienthorizons.entity.client.domestic_goat;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.DomesticGoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DomesticGoatRenderer extends MobRenderer<DomesticGoatEntity, DomesticGoatModel<DomesticGoatEntity>> {
    private static final ResourceLocation BUCK = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/domestic_goat/domestic_goat_buck.png");
    private static final ResourceLocation DOE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/domestic_goat/domestic_goat_doe.png");

    public DomesticGoatRenderer(EntityRendererProvider.Context context) {
        super(context, new DomesticGoatModel<>(context.bakeLayer(DomesticGoatModel.LAYER_LOCATION)), 0.75f);
    }

    public ResourceLocation getTextureLocation(DomesticGoatEntity entity) {
        return entity.isBuck() ? BUCK : DOE;
    }

    @Override
    public void render(DomesticGoatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
