package com.fungoussoup.ancienthorizons.entity.client.saola;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SaolaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SaolaModel<T extends SaolaEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "saola"), "main");
    private final ModelPart body;
    private final ModelPart neck_base;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legrightback;
    private final ModelPart legleftback;
    private final ModelPart halo;

    public SaolaModel(ModelPart root) {
        this.body = root.getChild("body");
        this.neck_base = this.body.getChild("neck_base");
        this.neck = this.neck_base.getChild("neck");
        this.head = this.neck.getChild("head");
        this.horns = this.head.getChild("horns");
        this.legleftfront = root.getChild("legleftfront");
        this.legrightfront = root.getChild("legrightfront");
        this.legrightback = root.getChild("legrightback");
        this.legleftback = root.getChild("legleftback");
        this.halo = root.getChild("halo");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -7.0F, 8.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition neck_base = body.addOrReplaceChild("neck_base", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

        PartDefinition neck = neck_base.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(12, 32).addBox(-1.5F, -7.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -6.5F, -0.15F, -0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 22).addBox(-3.5F, -4.0F, 1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 24).addBox(1.5F, -4.0F, 1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 0).addBox(-2.0F, -3.75F, -3.75F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(11, 22).addBox(-1.0F, -2.75F, -3.75F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, -3.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition horns = head.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(-1.0F, -5.0F, 0.65F));

        PartDefinition cube_r4 = horns.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 22).addBox(-0.5F, 0.25F, -0.9F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(1.5F, 0.25F, -0.9F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, 0.0F, 0.0F));

        PartDefinition legleftfront = partdefinition.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(38, 29).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 15.0F, -5.0F));

        PartDefinition legrightfront = partdefinition.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(26, 32).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 15.0F, -5.0F));

        PartDefinition legrightback = partdefinition.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(0, 32).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 15.0F, 6.0F));

        PartDefinition legleftback = partdefinition.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 15.0F, 6.0F));

        PartDefinition halo = partdefinition.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(43, 48).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -10.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SaolaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

        this.neck_base.yRot = headYaw * ((float) Math.PI / 180f);
        this.neck_base.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        halo.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
