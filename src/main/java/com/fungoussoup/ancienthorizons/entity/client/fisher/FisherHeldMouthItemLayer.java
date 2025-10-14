package com.fungoussoup.ancienthorizons.entity.client.fisher;

import com.fungoussoup.ancienthorizons.entity.custom.mob.FisherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FisherHeldMouthItemLayer extends RenderLayer<FisherEntity, FisherModel<FisherEntity>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public FisherHeldMouthItemLayer(RenderLayerParent<FisherEntity, FisherModel<FisherEntity>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, FisherEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag1 = livingEntity.isBaby();
        poseStack.pushPose();
        if (!livingEntity.isFishing()) {
            if (flag1) {
                float f = 0.75F;
                poseStack.scale(0.75F, 0.75F, 0.75F);
                poseStack.translate(0.0F, 0.5F, 0.209375F);
            }

            poseStack.translate(this.getParentModel().head.x / 16.0F, this.getParentModel().head.y / 16.0F, (this.getParentModel()).head.z / 16.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
            if (livingEntity.isBaby()) {
                poseStack.translate(0.06F, 0.26F, -0.5F);
            } else {
                poseStack.translate(0.06F, 0.27F, -0.5F);
            }

            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));


            ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
            this.itemInHandRenderer.renderItem(livingEntity, itemstack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }
}
