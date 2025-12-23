package com.fungoussoup.ancienthorizons.entity.client.maip;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MaipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MaipModel<T extends MaipEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "maip"), "main");

    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart saddle;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart legleft;
    private final ModelPart kneeleft;
    private final ModelPart footleft;
    private final ModelPart legright;
    private final ModelPart kneeright;
    private final ModelPart footright;

    public MaipModel(ModelPart root) {
        this.body = root.getChild("body");
        this.belly = this.body.getChild("belly");
        this.saddle = this.belly.getChild("saddle");
        this.neck = this.belly.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.armleft = this.belly.getChild("armleft");
        this.armright = this.belly.getChild("armright");
        this.tail = this.belly.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.legleft = this.body.getChild("legleft");
        this.kneeleft = this.legleft.getChild("kneeleft");
        this.footleft = this.kneeleft.getChild("footleft");
        this.legright = this.body.getChild("legright");
        this.kneeright = this.legright.getChild("kneeright");
        this.footright = this.kneeright.getChild("footright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offset(0.0F, -16.0F, 11.0F));

        PartDefinition belly = body.addOrReplaceChild("belly",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -3.0F, -31.0F, 16.0F, 18.0F, 38.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition saddle = belly.addOrReplaceChild("saddle",
                CubeListBuilder.create().texOffs(0, 56).addBox(-8.0F, -3.0F, -30.0F, 16.0F, 18.0F, 38.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition neck = belly.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(108, 95).addBox(-4.0F, -24.0F, -6.0F, 8.0F, 30.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.0F, -31.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(80, 137).addBox(-4.0F, -6.0F, -8.0F, 8.0F, 10.0F, 14.0F, new CubeDeformation(0.025F))
                        .texOffs(124, 137).addBox(-3.0F, -5.0F, -23.0F, 6.0F, 6.0F, 15.0F, new CubeDeformation(0.0F))
                        .texOffs(148, 95).addBox(-2.5F, 1.0F, -22.5F, 5.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -18.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(0, 145).addBox(-3.0F, 0.0F, -15.0F, 6.0F, 3.0F, 15.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 145).addBox(-2.5F, -2.0F, -14.5F, 5.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.0F, -8.0F));

        PartDefinition armleft = belly.addOrReplaceChild("armleft",
                CubeListBuilder.create().texOffs(124, 158).addBox(-2.0F, -1.0F, -2.0F, 3.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(164, 76).addBox(-3.5F, 16.0F, 1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 9.0F, -26.0F));

        PartDefinition cube_r1 = armleft.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(104, 116).addBox(-5.0F, -4.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.025F))
                        .texOffs(164, 66).addBox(-5.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.025F)),
                PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r2 = armleft.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(106, 120).addBox(-4.5F, -2.0F, -1.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.025F))
                        .texOffs(148, 135).addBox(-4.5F, -1.0F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.025F)),
                PartPose.offsetAndRotation(0.0F, 16.0F, 1.5F, 0.0F, 0.0F, -0.3927F));

        PartDefinition armright = belly.addOrReplaceChild("armright",
                CubeListBuilder.create().texOffs(142, 158).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(164, 74).addBox(0.5F, 16.0F, 1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, 9.0F, -26.0F));

        PartDefinition cube_r3 = armright.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(158, 135).addBox(0.5F, -1.0F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.025F))
                        .texOffs(104, 120).addBox(4.5F, -2.0F, -1.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.025F)),
                PartPose.offsetAndRotation(0.0F, 16.0F, 1.5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r4 = armright.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(164, 70).addBox(0.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.025F))
                        .texOffs(104, 112).addBox(5.0F, -4.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.025F)),
                PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition tail = belly.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(108, 0).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 11.0F, 24.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 7.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(108, 35).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 7.0F, 24.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 24.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(108, 66).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 24.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 24.0F));

        PartDefinition legleft = body.addOrReplaceChild("legleft",
                CubeListBuilder.create().texOffs(0, 112).addBox(-5.0F, -1.0F, -7.0F, 6.0F, 19.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 0.0F, 0.0F));

        PartDefinition kneeleft = legleft.addOrReplaceChild("kneeleft",
                CubeListBuilder.create().texOffs(80, 112).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(-0.025F)),
                PartPose.offset(-2.0F, 18.0F, 6.0F));

        PartDefinition footleft = kneeleft.addOrReplaceChild("footleft",
                CubeListBuilder.create().texOffs(160, 158).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                        .texOffs(70, 161).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 163).addBox(1.5F, 1.0F, -9.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 163).addBox(-3.5F, 1.0F, -9.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 19.0F, -2.0F));

        PartDefinition legright = body.addOrReplaceChild("legright",
                CubeListBuilder.create().texOffs(40, 112).addBox(-1.0F, -1.0F, -7.0F, 6.0F, 19.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-8.0F, 0.0F, 0.0F));

        PartDefinition kneeright = legright.addOrReplaceChild("kneeright",
                CubeListBuilder.create().texOffs(148, 111).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(-0.025F)),
                PartPose.offset(2.0F, 18.0F, 3.0F));

        PartDefinition footright = kneeright.addOrReplaceChild("footright",
                CubeListBuilder.create().texOffs(106, 161).addBox(1.5F, 1.0F, -9.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 161).addBox(-3.5F, -1.0F, -4.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 161).addBox(-1.5F, 0.0F, -10.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 163).addBox(-3.5F, 1.0F, -9.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 19.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(MaipEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Saddle visibility
        this.saddle.visible = entity.isSaddled();

        // Head look
        if (!entity.isSitting()) {
            this.head.xRot += headPitch * Mth.DEG_TO_RAD;
            this.neck.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
            this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        }

        // Sitting pose
        if (entity.isSitting()) {
            this.belly.xRot = -0.5F;
            this.legleft.xRot = -1.3F;
            this.legright.xRot = -1.3F;
            this.kneeleft.xRot = 1.5F;
            this.kneeright.xRot = 1.5F;
            this.tail.xRot = 0.8F;
        } else {
            // Walking animation
            this.legleft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.legright.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

            this.kneeleft.xRot = Math.max(0.0F, Mth.sin(limbSwing * 0.6662F) * 1.2F * limbSwingAmount);
            this.kneeright.xRot = Math.max(0.0F, Mth.sin(limbSwing * 0.6662F + (float)Math.PI) * 1.2F * limbSwingAmount);

            // Tail sway
            this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
            this.tail2.yRot = Mth.cos(limbSwing * 0.6662F + 0.5F) * 0.3F * limbSwingAmount;
            this.tail3.yRot = Mth.cos(limbSwing * 0.6662F + 1.0F) * 0.3F * limbSwingAmount;

            // Body bob
            this.belly.y = Mth.cos(limbSwing * 0.6662F) * 0.5F * limbSwingAmount;
        }

        // Baby adjustments
        if (this.young) {
            this.belly.y += 8.0F;
            this.legleft.y += 8.0F;
            this.legright.y += 8.0F;
        }

        // Animations
        this.animate(entity.roarAnimationState, MaipAnimations.ROAR, ageInTicks);
        this.animate(entity.attackSmallAnimationState, MaipAnimations.ATTACK_S, ageInTicks);
        this.animate(entity.attackMediumAnimationState, MaipAnimations.ATTACK_M, ageInTicks);
        this.animate(entity.attackOneLargeAnimationState, MaipAnimations.ATTACK_L, ageInTicks);
        this.animate(entity.attackTwoLargeAnimationState, MaipAnimations.ATTACK_B, ageInTicks);
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