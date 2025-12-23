package com.fungoussoup.ancienthorizons.entity.client.latenivenatrix;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.LatenivenatrixEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LatenivenatrixRenderer extends MobRenderer<LatenivenatrixEntity, LatenivenatrixModel<LatenivenatrixEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/latenivenatrix/latenivenatrix.png");

    private static final ResourceLocation TEXTURE_ANGRY = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/latenivenatrix/latenivenatrix_angry.png");

    public LatenivenatrixRenderer(EntityRendererProvider.Context context) {
        super(context, new LatenivenatrixModel<>(context.bakeLayer(LatenivenatrixModel.LAYER_LOCATION)), 0.8F);

        // Add render layers
        this.addLayer(new LatenivenatrixEyesLayer(this));
        this.addLayer(new LatenivenatrixCollarLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatenivenatrixEntity entity) {
        if (entity.isAggressive()) {
            return TEXTURE_ANGRY;
        } else {
            return TEXTURE;
        }
    }

    @Override
    public void render(LatenivenatrixEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        // Baby scaling
        if (entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            this.shadowRadius = 0.4F;
        } else {
            this.shadowRadius = 0.8F;
        }

        // Apply attack animation if attacking
        if (entity.isAggressive() && model instanceof LatenivenatrixModel<?> latenivenatrixModel) {
            float attackTime = entity.getAttackAnim(partialTicks);
            latenivenatrixModel.applyAttackAnimation(attackTime);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected float getBob(LatenivenatrixEntity entity, float partialTicks) {
        // Custom bob for dancing - more pronounced
        if (entity.isDancing()) {
            return entity.tickCount + partialTicks;
        }
        return super.getBob(entity, partialTicks);
    }

    /**
     * Glowing eyes render layer - makes eyes glow at night (nocturnal hunter)
     */
    static class LatenivenatrixEyesLayer extends RenderLayer<LatenivenatrixEntity, LatenivenatrixModel<LatenivenatrixEntity>> {
        private static final ResourceLocation EYES_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                AncientHorizons.MOD_ID, "textures/entity/latenivenatrix/latenivenatrix_eyes.png");

        public LatenivenatrixEyesLayer(LatenivenatrixRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           LatenivenatrixEntity entity, float limbSwing, float limbSwingAmount,
                           float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

            // Eyes glow in darkness (full brightness)
            renderColoredCutoutModel(
                    this.getParentModel(),
                    EYES_TEXTURE,
                    poseStack,
                    buffer,
                    15728640, // Full bright light value
                    entity,
                    1.0F, 1.0F, 1.0F // RGB white
            );
        }

        private void renderColoredCutoutModel(LatenivenatrixModel<LatenivenatrixEntity> parentModel, ResourceLocation eyesTexture, PoseStack poseStack, MultiBufferSource buffer, int i, LatenivenatrixEntity entity, float red, float green, float blue) {
        }
    }

    private static class LatenivenatrixCollarLayer extends RenderLayer<LatenivenatrixEntity, LatenivenatrixModel<LatenivenatrixEntity>> {
        private static final ResourceLocation LATEN_COLLAR_LOCATION = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/latenivenatrix/latenivenatrix_collar.png");

        public LatenivenatrixCollarLayer(RenderLayerParent<LatenivenatrixEntity, LatenivenatrixModel<LatenivenatrixEntity>> renderer, EntityModelSet modelSet) {
            super(renderer);
        }

        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LatenivenatrixEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (livingEntity.isTame() && !livingEntity.isInvisible()) {
                int i = livingEntity.getCollarColor().getTextureDiffuseColor();
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(LATEN_COLLAR_LOCATION));
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
            }
        }
    }
}
