package com.fungoussoup.ancienthorizons.entity.client.non_mob;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.TranquilizerDartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import com.mojang.math.Axis;

public class DartRenderer extends EntityRenderer<TranquilizerDartEntity> {
    private final DartModel<TranquilizerDartEntity> model;

    public DartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DartModel<>(context.bakeLayer(DartModel.LAYER_LOCATION));
    }

    @Override
    public void render(TranquilizerDartEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();

        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, 0.0F, 0.0F);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, 1, 1);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TranquilizerDartEntity entity) {
        DyeColor color = entity.getColor(); // Make sure this exists
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/dart/dart_" + color.getName() + ".png");
    }
}

