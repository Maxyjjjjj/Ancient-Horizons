package com.fungoussoup.ancienthorizons.entity.client.wildebeest;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.WildebeestEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WildebeestModel<T extends WildebeestEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "wildebeest"), "main");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart legfrontleft;
    private final ModelPart legfrontright;
    private final ModelPart legbackright;
    private final ModelPart legbackleft;

    public WildebeestModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.legfrontleft = root.getChild("legfrontleft");
        this.legfrontright = root.getChild("legfrontright");
        this.legbackright = root.getChild("legbackright");
        this.legbackleft = root.getChild("legbackleft");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -11.0F, 10.0F, 14.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(4, 72).addBox(-4.0F, 8.0F, -10.0F, 8.0F, 5.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(56, 36).addBox(-2.5F, -8.0F, -16.0F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(59, 65).addBox(-2.5F, 1.0F, -16.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-2.0F, -8.0F, -11.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 60).addBox(0.0F, -10.0F, -11.0F, 0.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 36).addBox(-3.5F, -1.0F, -5.0F, 7.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(64, 17).addBox(3.5F, 0.0F, -2.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 20).addBox(-6.5F, 0.0F, -2.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 9).addBox(0.5F, -2.0F, -3.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 23).addBox(4.5F, -4.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 27).addBox(-5.5F, -4.0F, -3.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 13).addBox(-5.5F, -2.0F, -3.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-2.5F, 8.0F, -5.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -16.0F));

        PartDefinition legfrontleft = partdefinition.addOrReplaceChild("legfrontleft", CubeListBuilder.create().texOffs(32, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 8.0F, -9.0F));

        PartDefinition legfrontright = partdefinition.addOrReplaceChild("legfrontright", CubeListBuilder.create().texOffs(48, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, -9.0F));

        PartDefinition legbackright = partdefinition.addOrReplaceChild("legbackright", CubeListBuilder.create().texOffs(16, 50).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(-3.0F, 8.0F, 11.0F));

        PartDefinition legbackleft = partdefinition.addOrReplaceChild("legbackleft", CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(3.0F, 8.0F, 11.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return body;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legfrontleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legfrontright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.legbackright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legbackleft.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot = headYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legfrontleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legfrontright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legbackright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legbackleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
