package com.fungoussoup.ancienthorizons.entity.client.penguin;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PenguinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PenguinModel<T extends PenguinEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "penguin"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingright;
    private final ModelPart tail;
    private final ModelPart legleft;
    private final ModelPart legleft2;

    public PenguinModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.headbase = this.body.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.wingleft = this.body.getChild("wingleft");
        this.wingright = this.body.getChild("wingright");
        this.tail = this.body.getChild("tail");
        this.legleft = this.base.getChild("legleft");
        this.legleft2 = this.base.getChild("legleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition headbase = body.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, -1.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-1.0F, -2.25F, -5.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0262F, 0.0F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 28).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(16, 21).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -8.0F, 0.0F));

        PartDefinition wingright = body.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -8.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(24, 14).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 5.0F, 0.0F));

        PartDefinition legleft2 = base.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(26, 21).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PenguinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.headbase.yRot = headYaw * ((float) Math.PI / 180f);
        this.headbase.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return base;
    }
}
