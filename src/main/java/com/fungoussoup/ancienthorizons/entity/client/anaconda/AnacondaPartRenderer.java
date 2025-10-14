package com.fungoussoup.ancienthorizons.entity.client.anaconda;

import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaPartEntity;
import com.fungoussoup.ancienthorizons.entity.util.AnacondaPartIndex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class AnacondaPartRenderer extends LivingEntityRenderer<AnacondaPartEntity, AnacondaModel<AnacondaPartEntity>> {
    private final AnacondaModel<AnacondaPartEntity> bodyModel;
    private final AnacondaModel<AnacondaPartEntity> tailModel;
    private final AnacondaModel<AnacondaPartEntity> headModel;

    public AnacondaPartRenderer(EntityRendererProvider.Context context) {
        // Use body model as default
        super(context, new AnacondaModel<>(context.bakeLayer(AnacondaModel.LAYER_LOCATION)), 0.3F);

        // Create models for each part type
        this.bodyModel = new AnacondaModel<>(context.bakeLayer(AnacondaModel.BODY_LAYER_LOCATION));
        this.tailModel = new AnacondaModel<>(context.bakeLayer(AnacondaModel.TAIL_LAYER_LOCATION));
        this.headModel = new AnacondaModel<>(context.bakeLayer(AnacondaModel.HEAD_LAYER_LOCATION));
    }

    protected void setupRotations(AnacondaPartEntity entity, PoseStack stack, float pitchIn, float yawIn, float partialTickTime) {
        float newYaw = entity.yHeadRot;
        if (this.isShaking(entity)) {
            newYaw += (float)(Math.cos((double)entity.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - newYaw));
            stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(entity.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                stack.translate(0.0D, (double)(entity.getBbHeight() + 0.1F), 0.0D);
                stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    protected boolean shouldShowName(AnacondaPartEntity entity) {
        return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    @Override
    protected void scale(AnacondaPartEntity entity, PoseStack matrixStack, float partialTickTime) {
        // Switch to the appropriate model before scaling
        this.model = getModelForType(entity.getPartType());
        matrixStack.scale(entity.getScale(), entity.getScale(), entity.getScale());
    }

    private AnacondaModel<AnacondaPartEntity> getModelForType(AnacondaPartIndex partType) {
        return switch (partType) {
            case BODY -> bodyModel;
            case TAIL -> tailModel;
            case HEAD -> headModel;
        };
    }

    @Override
    public ResourceLocation getTextureLocation(AnacondaPartEntity anacondaPartEntity) {
        return AnacondaRenderer.TEXTURE;
    }
}