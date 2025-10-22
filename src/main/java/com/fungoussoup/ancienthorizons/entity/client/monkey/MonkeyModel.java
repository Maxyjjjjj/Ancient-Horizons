package com.fungoussoup.ancienthorizons.entity.client.monkey;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MonkeyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class MonkeyModel<T extends MonkeyEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "monkey"), "main");
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart legleft;
    private final ModelPart legright;

    public MonkeyModel(ModelPart root) {
        this.body = root.getChild("body");
        this.tail = this.body.getChild("tail");
        this.head = root.getChild("head");
        this.armleft = root.getChild("armleft");
        this.armright = root.getChild("armright");
        this.legleft = root.getChild("legleft");
        this.legright = root.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 3.0F));

        PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 24).addBox(-1.0F, -11.0F, 0.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.2689F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-3.5F, -6.0F, -3.0F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(14, 36).addBox(-1.5F, -3.0F, -4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 24).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition armleft = partdefinition.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(24, 0).addBox(0.0F, 0.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition armright = partdefinition.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(0, 26).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(14, 26).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 18.0F, -1.0F));

        PartDefinition legright = partdefinition.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(26, 14).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 18.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(MonkeyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head look
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Reset
        this.body.xRot = 0.0F;
        this.armleft.xRot = 0.0F;
        this.armright.xRot = 0.0F;
        this.legleft.xRot = 0.0F;
        this.legright.xRot = 0.0F;

        if (!entity.isSitting()) {
            // Walking
            this.armright.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.armleft.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.legright.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.legleft.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

            // Idle subtle motion
            this.armleft.zRot = Mth.cos(ageInTicks * 0.05F) * 0.05F - 0.05F;
            this.armright.zRot = -Mth.cos(ageInTicks * 0.05F) * 0.05F + 0.05F;
            this.tail.yRot = Mth.cos(ageInTicks * 0.1F) * 0.1F;
        } else {
            // Sitting pose (GSNM-style)
            this.body.xRot = 0.25F;
            this.head.xRot = -0.2F;
            this.armleft.xRot = -0.6F;
            this.armright.xRot = -0.6F;
            this.legleft.xRot = -1.3F;
            this.legright.xRot = -1.3F;
            this.tail.xRot = -1.8F;
        }

        // Holding animation
        if (entity.isHoldingItem() && !entity.isSitting()) {
            this.armright.xRot = -0.8F;
            this.armright.yRot = -0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        armleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        armright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        ModelPart part = arm == HumanoidArm.LEFT ? this.armleft : this.armright;
        this.root().translateAndRotate(poseStack);
        part.translateAndRotate(poseStack);
    }
}