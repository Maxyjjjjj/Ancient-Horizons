package com.fungoussoup.ancienthorizons.entity.client.roe_deer;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RoeDeerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RoeDeerModel<T extends RoeDeerEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "roe_deer"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legleftback;
    private final ModelPart legrightback;

    public RoeDeerModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.legleftfront = this.base.getChild("legleftfront");
        this.legrightfront = this.base.getChild("legrightfront");
        this.legleftback = this.base.getChild("legleftback");
        this.legrightback = this.base.getChild("legrightback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -6.0F, 7.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-2.5F, -9.0F, -2.0F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -13.0F, -2.5F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(18, 26).addBox(-1.5F, -7.0F, -5.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 19).addBox(2.5F, -9.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 22).addBox(-5.5F, -9.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, -5.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 36).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 7.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition legleftfront = base.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(30, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, -4.5F));

        PartDefinition legrightfront = base.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(18, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 16.0F, -4.5F));

        PartDefinition legleftback = base.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 33).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, 5.5F));

        PartDefinition legrightback = base.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(8, 33).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 16.0F, 5.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(RoeDeerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        this.legrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;

    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot = headYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
