package com.fungoussoup.ancienthorizons.entity.client.elephant;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ElephantEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ElephantModel<T extends ElephantEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "elephant"), "main");
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart head;
    private final ModelPart trunk;
    private final ModelPart trunk2;
    private final ModelPart trunk3;
    private final ModelPart earleft;
    private final ModelPart earleft2;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legrightback;
    private final ModelPart legleftback;

    public ElephantModel(ModelPart root) {
        this.body = root.getChild("body");
        this.belly = this.body.getChild("belly");
        this.head = this.body.getChild("head");
        this.trunk = this.head.getChild("trunk");
        this.trunk2 = this.trunk.getChild("trunk2");
        this.trunk3 = this.trunk2.getChild("trunk3");
        this.earleft = this.head.getChild("earleft");
        this.earleft2 = this.head.getChild("earleft2");
        this.legleftfront = this.body.getChild("legleftfront");
        this.legrightfront = this.body.getChild("legrightfront");
        this.legrightback = this.body.getChild("legrightback");
        this.legleftback = this.body.getChild("legleftback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -26.0F, 0.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -12.0F, -24.0F, 24.0F, 30.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 78).addBox(-10.0F, -6.0F, -14.0F, 20.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, -24.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(148, 40).addBox(-10.0F, 19.0F, -10.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(140, 143).addBox(-11.0F, 7.0F, -11.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(153, 64).addBox(-10.0F, 30.0F, -20.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(146, 25).addBox(6.0F, 30.0F, -20.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(147, 1).addBox(6.0F, 19.0F, -10.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(113, 143).addBox(5.0F, 7.0F, -11.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition trunk = head.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(79, 142).addBox(-5.0F, -4.0F, -4.0F, 10.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, -14.0F));

        PartDefinition trunk2 = trunk.addOrReplaceChild("trunk2", CubeListBuilder.create().texOffs(166, 80).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition trunk3 = trunk2.addOrReplaceChild("trunk3", CubeListBuilder.create().texOffs(166, 98).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -0.5F));

        PartDefinition earleft = head.addOrReplaceChild("earleft", CubeListBuilder.create().texOffs(80, 120).addBox(-2.0F, -5.0F, 0.0F, 20.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -3.0F, -7.0F, 0.0F, -0.3491F, -0.3054F));

        PartDefinition earleft2 = head.addOrReplaceChild("earleft2", CubeListBuilder.create().texOffs(124, 120).addBox(-18.0F, -5.0F, 0.0F, 20.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -3.0F, -7.0F, 0.0F, 0.3491F, 0.3054F));

        PartDefinition legleftfront = body.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(86, 78).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 32.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 18.0F, -19.0F));

        PartDefinition legrightfront = body.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(126, 78).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 32.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 18.0F, -19.0F));

        PartDefinition legrightback = body.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(0, 114).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 32.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 18.0F, 19.0F));

        PartDefinition legleftback = body.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(40, 120).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 32.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 18.0F, 19.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(ElephantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.legrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

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
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
