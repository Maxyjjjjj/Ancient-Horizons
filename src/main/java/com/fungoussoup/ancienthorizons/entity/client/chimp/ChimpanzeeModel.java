package com.fungoussoup.ancienthorizons.entity.client.chimp;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ChimpanzeeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ChimpanzeeModel<T extends ChimpanzeeEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "chimpanzee"), "main");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart legleft;
    private final ModelPart legleft2;

    public ChimpanzeeModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.armleft = root.getChild("armleft");
        this.armright = root.getChild("armright");
        this.legleft = root.getChild("legleft");
        this.legleft2 = root.getChild("legleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -10.0F, -2.5F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(21, 0).addBox(-2.5F, -4.0F, -4.5F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 29).addBox(3.5F, -6.0F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 5).addBox(-6.5F, -6.0F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition armleft = partdefinition.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(8, 29).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 7.0F, 0.0F));

        PartDefinition armright = partdefinition.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 7.0F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(26, 22).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, 0.0F));

        PartDefinition legleft2 = partdefinition.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(26, 11).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 16.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ChimpanzeeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        this.armright.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.armleft.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;

        this.armright.zRot = 0.0F;
        this.armleft.zRot = 0.0F;

        if (entity.isSitting()) {
            this.armright.xRot = -((float)Math.PI / 5F);
            this.armleft.xRot = -((float)Math.PI / 5F);
            this.legleft.xRot = -1.4137167F;
            this.legleft.yRot = ((float)Math.PI / 10F);
            this.legleft2.xRot = -1.4137167F;
            this.legleft2.yRot = -((float)Math.PI / 10F);
        }  else if (entity.isClimbing()) {
            // Non-player climbing pose
            float climbSwing = ageInTicks * 0.6662F;
            this.armright.xRot = Mth.cos(climbSwing) * 0.5F;
            this.armleft.xRot = Mth.cos(climbSwing + (float)Math.PI) * 0.5F;
            this.legleft.xRot = Mth.cos(climbSwing + (float)Math.PI) * 0.5F;
            this.legleft2.xRot = Mth.cos(climbSwing) * 0.5F;

            // Slightly look down while climbing
            this.head.xRot = 0.25F;
        } else {
        this.legleft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.legleft2.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        }
        if (entity.isUsingItem()) {
            armright.xRot = -Mth.PI / 2;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        armleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        armright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }

}