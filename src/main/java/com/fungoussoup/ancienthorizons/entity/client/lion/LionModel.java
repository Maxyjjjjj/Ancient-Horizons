package com.fungoussoup.ancienthorizons.entity.client.lion;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.client.tiger.TigerAnimations;
import com.fungoussoup.ancienthorizons.entity.custom.mob.LionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LionModel<T extends LionEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "lion"), "main");
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart head;
    private final ModelPart muzzle;
    private final ModelPart jaw;
    private final ModelPart tongue;
    private final ModelPart eyesangry;
    private final ModelPart eyesclosed;
    private final ModelPart earleft;
    private final ModelPart earleft2;
    private final ModelPart pawleftfront;
    private final ModelPart clawsleftfront;
    private final ModelPart pawrightfront;
    private final ModelPart clawsrightfront;
    private final ModelPart pawrightback;
    private final ModelPart clawsrightback;
    private final ModelPart pawleftback;
    private final ModelPart clawsleftback;
    private final ModelPart tail;

    public LionModel(ModelPart root) {
        this.body = root.getChild("body");
        this.belly = this.body.getChild("belly");
        this.head = this.body.getChild("head");
        this.muzzle = this.head.getChild("muzzle");
        this.jaw = this.head.getChild("jaw");
        this.tongue = this.jaw.getChild("tongue");
        this.eyesangry = this.head.getChild("eyesangry");
        this.eyesclosed = this.head.getChild("eyesclosed");
        this.earleft = this.head.getChild("earleft");
        this.earleft2 = this.head.getChild("earleft2");
        this.pawleftfront = this.body.getChild("pawleftfront");
        this.clawsleftfront = this.pawleftfront.getChild("clawsleftfront");
        this.pawrightfront = this.body.getChild("pawrightfront");
        this.clawsrightfront = this.pawrightfront.getChild("clawsrightfront");
        this.pawrightback = this.body.getChild("pawrightback");
        this.clawsrightback = this.pawrightback.getChild("clawsrightback");
        this.pawleftback = this.body.getChild("pawleftback");
        this.clawsleftback = this.pawleftback.getChild("clawsleftback");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -18.0F, 11.0F, 11.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 11.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 58).addBox(-5.0F, -5.0F, -7.0F, 10.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(52, 32).addBox(-7.0F, -5.5F, -5.0F, 14.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -7.0F));

        PartDefinition muzzle = head.addOrReplaceChild("muzzle", CubeListBuilder.create().texOffs(0, 74).addBox(-3.5F, 0.0F, -5.0F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(74, 81).addBox(-3.0F, 3.0F, -4.75F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.05F)), PartPose.offset(0.5F, -2.0F, -7.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(74, 73).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 82).addBox(-2.5F, -2.0F, -4.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 1.0F, -7.0F));

        PartDefinition tongue = jaw.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(34, 66).addBox(-2.5F, -0.5F, -4.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition eyesangry = head.addOrReplaceChild("eyesangry", CubeListBuilder.create().texOffs(84, 13).addBox(1.5F, -1.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(84, 16).addBox(-4.5F, -1.0F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -6.05F));

        PartDefinition eyesclosed = head.addOrReplaceChild("eyesclosed", CubeListBuilder.create().texOffs(30, 74).addBox(2.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(30, 75).addBox(-4.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, -6.05F));

        PartDefinition earleft = head.addOrReplaceChild("earleft", CubeListBuilder.create().texOffs(22, 74).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -5.0F, -1.5F));

        PartDefinition earleft2 = head.addOrReplaceChild("earleft2", CubeListBuilder.create().texOffs(22, 77).addBox(-1.5F, -2.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -5.0F, -1.5F));

        PartDefinition pawleftfront = body.addOrReplaceChild("pawleftfront", CubeListBuilder.create().texOffs(64, 0).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(3.0F, 5.0F, -4.0F));

        PartDefinition clawsleftfront = pawleftfront.addOrReplaceChild("clawsleftfront", CubeListBuilder.create().texOffs(20, 82).addBox(-2.5F, 0.0F, -0.025F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.025F));

        PartDefinition pawrightfront = body.addOrReplaceChild("pawrightfront", CubeListBuilder.create().texOffs(64, 16).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(-3.0F, 5.0F, -4.0F));

        PartDefinition clawsrightfront = pawrightfront.addOrReplaceChild("clawsrightfront", CubeListBuilder.create().texOffs(84, 4).addBox(-2.5F, 0.0F, -0.025F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.025F));

        PartDefinition pawrightback = body.addOrReplaceChild("pawrightback", CubeListBuilder.create().texOffs(34, 73).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(-3.0F, 5.0F, 12.0F));

        PartDefinition clawsrightback = pawrightback.addOrReplaceChild("clawsrightback", CubeListBuilder.create().texOffs(84, 7).addBox(-2.5F, 0.0F, -0.025F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.025F));

        PartDefinition pawleftback = body.addOrReplaceChild("pawleftback", CubeListBuilder.create().texOffs(54, 73).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(3.0F, 5.0F, 12.0F));

        PartDefinition clawsleftback = pawleftback.addOrReplaceChild("clawsleftback", CubeListBuilder.create().texOffs(84, 10).addBox(-2.5F, 0.0F, -0.025F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, -3.025F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(34, 58).addBox(-1.5F, -0.5F, 19.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 14.0F, -1.0036F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(LionEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.applyHeadRotation(netHeadYaw, headPitch);

        if (!entity.isRunning() && !entity.isSleeping()) {
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.pawleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
            this.pawrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        }

        this.animate(entity.angryAnimationState, TigerAnimations.TIGER_ANGRY, ageInTicks, 1f);
        this.animate(entity.sleepAnimationState, TigerAnimations.TIGER_SLEEP, ageInTicks, 1f);
        this.animate(entity.yawnAnimationState, TigerAnimations.TIGER_YAWN, ageInTicks, 1f);
        this.animate(entity.sitAnimationState, TigerAnimations.TIGER_SIT, ageInTicks, 1f);

        if (entity.isSleeping()) {
            // Example sleeping pose adjustments — tweak as needed for your model
            this.body.y = 14.0F;
            this.body.zRot = -42.5F * Mth.DEG_TO_RAD;
        }

        if (entity.isSitting()) {
            body.xRot = -37.5F * Mth.DEG_TO_RAD;
            body.y = 6;
        }
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw,-30f,30f);
        headPitch = Mth.clamp(headPitch,-25f,45);

        this.head.yRot = headYaw * ((float)Math.PI / 180f);
        this.head.xRot = headPitch * ((float)Math.PI / 180f);
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
