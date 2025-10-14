package com.fungoussoup.ancienthorizons.entity.client.sauropod;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.sauropoda.DiplodocusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import static com.fungoussoup.ancienthorizons.entity.client.sauropod.DiplodocusAnimations.*;

public class DiplodocusModel<T extends DiplodocusEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "diplodocus"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart neck3;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart[] saddleParts;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public DiplodocusModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.neck2 = this.neck.getChild("neck2");
        this.neck3 = this.neck2.getChild("neck3");
        this.headbase = this.neck3.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail = this.body.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.tail5 = this.tail4.getChild("tail5");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.saddleParts = new ModelPart[]{this.body.getChild("saddle")};
        this.left_leg = this.base.getChild("left_leg");
        this.right_leg = this.base.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, -24.0F, 16.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -16.0F, -46.0F, 26.0F, 32.0F, 60.0F, new CubeDeformation(0.0F))
                .texOffs(172, 0).addBox(0.0F, -24.0F, -46.0F, 0.0F, 8.0F, 60.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(298, 36).addBox(-7.0F, -7.0F, -32.0F, 14.0F, 15.0F, 36.0F, new CubeDeformation(0.0F))
                .texOffs(206, 331).addBox(0.0F, -14.0F, -32.0F, 0.0F, 7.0F, 36.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -7.0F, -46.0F, -0.6981F, 0.0F, 0.0F));

        PartDefinition neck2 = neck.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(110, 309).addBox(-6.0F, -6.0F, -32.0F, 12.0F, 13.0F, 36.0F, new CubeDeformation(0.0F))
                .texOffs(278, 336).addBox(0.0F, -11.0F, -32.0F, 0.0F, 5.0F, 36.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, -32.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(322, 251).addBox(-5.0F, -5.0F, -30.0F, 10.0F, 11.0F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(350, 336).addBox(0.0F, -8.0F, -30.0F, 0.0F, 3.0F, 34.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, -32.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition headbase = neck3.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -30.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(386, 170).addBox(-5.5F, -4.0F, -14.0F, 11.0F, 13.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(386, 209).addBox(-3.5F, 0.0F, -26.0F, 7.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(80, 395).addBox(-3.5F, 7.0F, -26.0F, 7.0F, 1.0F, 12.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(296, 170).addBox(-3.5F, 0.0F, -12.0F, 7.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(168, 358).addBox(-3.5F, -1.0F, -12.0F, 7.0F, 1.0F, 12.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 7.0F, -14.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(298, 87).addBox(-7.0F, -7.0F, 0.0F, 14.0F, 14.0F, 28.0F, new CubeDeformation(0.0F))
                .texOffs(168, 374).addBox(0.0F, -14.0F, 0.0F, 0.0F, 7.0F, 28.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, -9.0F, 14.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(322, 296).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 12.0F, 28.0F, new CubeDeformation(0.0F))
                .texOffs(224, 377).addBox(0.0F, -12.0F, 0.0F, 0.0F, 6.0F, 28.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 28.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(172, 68).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 7.0F, 55.0F, new CubeDeformation(0.0F))
                .texOffs(124, 251).addBox(0.0F, -7.0F, 0.0F, 0.0F, 3.0F, 55.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, -2.0F, 28.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(0, 184).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 55.0F, new CubeDeformation(0.0F))
                .texOffs(0, 292).addBox(0.0F, -4.0F, 0.0F, 0.0F, 2.0F, 55.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, -2.0F, 55.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(114, 188).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 55.0F, new CubeDeformation(0.0F))
                .texOffs(228, 188).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 55.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 55.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(338, 170).addBox(-11.0F, -1.0F, -6.0F, 12.0F, 49.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(12.5F, 0.0F, -32.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 349).addBox(-1.0F, -1.0F, -6.0F, 12.0F, 49.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.5F, 0.0F, -32.0F));

        PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(296, 130).addBox(-13.0F, -24.0F, -17.0F, 26.0F, 8.0F, 32.0F, new CubeDeformation(-0.175F))
                .texOffs(292, 0).addBox(-14.0F, -26.0F, -18.0F, 28.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(172, 130).addBox(-14.0F, -50.0F, -18.0F, 28.0F, 24.0F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(0, 247).addBox(-14.0F, -61.0F, -18.0F, 28.0F, 11.0F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(338, 231).addBox(-11.0F, -28.0F, -15.0F, 22.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(386, 197).addBox(-11.0F, -35.0F, -10.0F, 22.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(338, 231).addBox(-11.0F, -28.0F, 1.0F, 22.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(386, 197).addBox(-11.0F, -35.0F, 6.0F, 22.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(234, 251).addBox(-14.0F, -24.0F, -3.0F, 28.0F, 64.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 92).addBox(-13.0F, -16.0F, -31.0F, 26.0F, 32.0F, 60.0F, new CubeDeformation(0.05F))
                .texOffs(48, 390).addBox(-9.0F, -33.0F, -24.0F, 7.0F, 17.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(48, 390).addBox(1.0F, -33.0F, -24.0F, 7.0F, 17.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -15.0F));

        PartDefinition cube_r1 = saddle.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 358).mirror().addBox(-22.0F, -12.0F, -28.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.05F)).mirror(false), PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, 0.0F, 0.0F, -0.5672F));

        PartDefinition cube_r2 = saddle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 358).addBox(5.0F, -12.0F, -28.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, 0.0F, 0.0F, 0.5672F));

        PartDefinition left_leg = base.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(112, 358).addBox(-11.0F, -1.0F, -8.0F, 12.0F, 21.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(280, 377).addBox(-9.5F, 20.0F, -5.0F, 10.0F, 28.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(12.5F, 0.0F, 4.0F));

        PartDefinition right_leg = base.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(350, 373).addBox(-1.0F, -1.0F, -8.0F, 12.0F, 21.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(382, 87).addBox(-0.5F, 20.0F, -5.0F, 10.0F, 28.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.5F, 0.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.toggleInvisibleParts(entity);

        // 4-Segmented head turning
        // Distribute the head rotation across neck segments and head
        float headYawRad = netHeadYaw * ((float)Math.PI / 180F);
        float headPitchRad = headPitch * ((float)Math.PI / 180F);

        // Divide rotation across 4 segments: neck, neck2, neck3, and headbase
        float yawPerSegment = headYawRad * 0.25F;
        float pitchPerSegment = headPitchRad * 0.25F;

        this.neck.yRot += yawPerSegment;
        this.neck.xRot += pitchPerSegment;

        this.neck2.yRot += yawPerSegment;
        this.neck2.xRot += pitchPerSegment;

        this.neck3.yRot += yawPerSegment;
        this.neck3.xRot += pitchPerSegment;

        this.headbase.yRot += yawPerSegment;
        this.headbase.xRot += pitchPerSegment;

        this.animateWalk(DIPLODOCUS_WALK, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(entity.tailWhipAnimationState, TAIL_WHIP, 1f);
    }

    private void toggleInvisibleParts(T entity) {
        boolean flag = entity.isSaddled();

        for (ModelPart modelpart : this.saddleParts) {
            modelpart.visible = flag;
        }
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