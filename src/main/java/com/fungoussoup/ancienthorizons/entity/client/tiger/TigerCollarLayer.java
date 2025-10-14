package com.fungoussoup.ancienthorizons.entity.client.tiger;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.TigerEntity;
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
public class TigerCollarLayer extends RenderLayer<TigerEntity, TigerModel<TigerEntity>> {
    private static final ResourceLocation TIGER_COLLAR_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/tiger/tiger_collar.png");

    public TigerCollarLayer(RenderLayerParent<TigerEntity, TigerModel<TigerEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TigerEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.isTame() && !livingEntity.isInvisible()) {
            int i = livingEntity.getCollarColor().getTextureDiffuseColor();
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TIGER_COLLAR_LOCATION));
            ((TigerModel<?>)this.getParentModel()).renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
        }
    }
}
