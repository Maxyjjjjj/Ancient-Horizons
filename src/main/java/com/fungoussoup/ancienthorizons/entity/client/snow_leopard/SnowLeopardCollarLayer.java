package com.fungoussoup.ancienthorizons.entity.client.snow_leopard;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SnowLeopardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowLeopardCollarLayer extends RenderLayer<SnowLeopardEntity, SnowLeopardModel<SnowLeopardEntity>> {
    private static final ResourceLocation SNOW_LEOPARD_COLLAR_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/snow_leopard/snow_leopard_collar.png");

    public SnowLeopardCollarLayer(RenderLayerParent<SnowLeopardEntity, SnowLeopardModel<SnowLeopardEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SnowLeopardEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.isTame() && !livingEntity.isInvisible()) {
            int i = livingEntity.getCollarColor().getTextureDiffuseColor();
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(SNOW_LEOPARD_COLLAR_LOCATION));
            ((SnowLeopardModel<?>)this.getParentModel()).renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
        }
    }
}
