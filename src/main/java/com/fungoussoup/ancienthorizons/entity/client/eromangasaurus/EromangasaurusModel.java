package com.fungoussoup.ancienthorizons.entity.client.eromangasaurus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EromangasaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class EromangasaurusModel<T extends EromangasaurusEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "eromangasaurus"), "main");
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart neck3;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart flipperleftfront;
    private final ModelPart flipperrightfront;
    private final ModelPart flipperrightback;
    private final ModelPart flipperleftback;

    public EromangasaurusModel(ModelPart root) {
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.neck2 = this.neck.getChild("neck2");
        this.neck3 = this.neck2.getChild("neck3");
        this.headbase = this.neck3.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail = this.body.getChild("tail");
        this.flipperleftfront = this.body.getChild("flipperleftfront");
        this.flipperrightfront = this.body.getChild("flipperrightfront");
        this.flipperrightback = this.body.getChild("flipperrightback");
        this.flipperleftback = this.body.getChild("flipperleftback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, -14.0F, 14.0F, 14.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(60, 42).addBox(-4.0F, -4.0F, -20.0F, 8.0F, 8.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -14.0F));

        PartDefinition neck2 = neck.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(60, 70).addBox(-3.0F, -3.0F, -20.0F, 6.0F, 6.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -20.0F));

        PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(0, 72).addBox(-2.0F, -2.0F, -20.0F, 4.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -20.0F));

        PartDefinition headbase = neck3.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -20.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(84, 32).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.025F))
                .texOffs(14, 96).addBox(-1.5F, -1.0F, -8.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(28, 96).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -4.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 42).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 14.0F));

        PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 96).addBox(-1.0F, -11.0F, 16.0F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition flipperleftfront = body.addOrReplaceChild("flipperleftfront", CubeListBuilder.create().texOffs(84, 0).addBox(0.0F, -1.0F, -2.0F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 6.0F, -11.0F));

        PartDefinition flipperrightfront = body.addOrReplaceChild("flipperrightfront", CubeListBuilder.create().texOffs(84, 8).addBox(-14.0F, -1.0F, -2.0F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 6.0F, -11.0F));

        PartDefinition flipperrightback = body.addOrReplaceChild("flipperrightback", CubeListBuilder.create().texOffs(84, 16).addBox(-14.0F, -1.0F, -2.0F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 6.0F, 11.0F));

        PartDefinition flipperleftback = body.addOrReplaceChild("flipperleftback", CubeListBuilder.create().texOffs(84, 24).addBox(0.0F, -1.0F, -2.0F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 6.0F, 11.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(EromangasaurusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset rotations
        this.root().getAllParts().forEach(ModelPart::resetPose);

        boolean isInWater = entity.isInWater();

        if (isInWater) {
            // SWIMMING ANIMATIONS
            this.setupSwimmingAnim(limbSwingAmount, ageInTicks);
        } else {
            // LAND CRAWLING ANIMATIONS
            this.setupCrawlingAnim(limbSwing, limbSwingAmount);
        }

        // 4-Segmented head turning (works for both water and land)
        this.setupHeadTurning(netHeadYaw, headPitch);

        // Idle animations when not moving
        if (limbSwingAmount < 0.01F) {
            this.setupIdleAnim(ageInTicks, isInWater);
        }
    }

    private void setupSwimmingAnim(float limbSwingAmount, float ageInTicks) {
        float swimSpeed = 0.3F;
        float swimIntensity = 0.4F;

        // Body undulation for swimming
        float bodyWave = Mth.cos(ageInTicks * swimSpeed) * swimIntensity * limbSwingAmount;
        this.body.yRot = bodyWave * 0.1F;

        // Tail swimming motion - creates propulsion
        float tailWave = Mth.cos(ageInTicks * swimSpeed + 1.0F) * swimIntensity;
        this.tail.yRot = tailWave * 0.3F * limbSwingAmount;
        this.tail.xRot = Mth.sin(ageInTicks * swimSpeed * 0.5F) * 0.05F * limbSwingAmount;

        // Flipper animations - alternating rowing motion
        float flipperSpeed = 0.4F;
        float frontFlipperSwing = Mth.sin(ageInTicks * flipperSpeed) * limbSwingAmount;
        float backFlipperSwing = Mth.sin(ageInTicks * flipperSpeed + (float)Math.PI) * limbSwingAmount;

        // Front flippers
        this.flipperleftfront.zRot = -0.2F + frontFlipperSwing * 0.5F;
        this.flipperleftfront.xRot = Mth.cos(ageInTicks * flipperSpeed) * 0.3F * limbSwingAmount;

        this.flipperrightfront.zRot = 0.2F - frontFlipperSwing * 0.5F;
        this.flipperrightfront.xRot = Mth.cos(ageInTicks * flipperSpeed) * 0.3F * limbSwingAmount;

        // Back flippers - slightly offset from front
        this.flipperleftback.zRot = -0.2F + backFlipperSwing * 0.5F;
        this.flipperleftback.xRot = Mth.cos(ageInTicks * flipperSpeed + (float)Math.PI) * 0.3F * limbSwingAmount;

        this.flipperrightback.zRot = 0.2F - backFlipperSwing * 0.5F;
        this.flipperrightback.xRot = Mth.cos(ageInTicks * flipperSpeed + (float)Math.PI) * 0.3F * limbSwingAmount;

        // Subtle neck wave animation during swimming
        float neckWave = Mth.sin(ageInTicks * swimSpeed * 0.7F) * 0.05F * limbSwingAmount;
        this.neck.xRot += neckWave;
        this.neck2.xRot += neckWave * 0.8F;
        this.neck3.xRot += neckWave * 0.6F;
    }

    private void setupCrawlingAnim(float limbSwing, float limbSwingAmount) {
        // Body closer to ground and pitched slightly downward
        this.body.y = 19.0F;
        this.body.xRot = 0.1F;

        // Slow, labored crawling motion
        float crawlSpeed = 0.5F;

        // Body sways side to side while crawling
        float bodySway = Mth.cos(limbSwing * crawlSpeed) * 0.15F * limbSwingAmount;
        this.body.yRot = bodySway;
        this.body.zRot = bodySway * 0.5F;

        // Tail drags and sways behind
        this.tail.yRot = Mth.cos(limbSwing * crawlSpeed + 1.5F) * 0.2F * limbSwingAmount;
        this.tail.xRot = 0.1F; // Tail droops downward

        // Front flippers - pushing/pulling motion like a seal
        float frontFlipperCrawl = Mth.cos(limbSwing * crawlSpeed) * limbSwingAmount;

        this.flipperleftfront.zRot = -0.4F; // Angled downward
        this.flipperleftfront.xRot = -0.3F + frontFlipperCrawl * 0.6F;
        this.flipperleftfront.yRot = frontFlipperCrawl * 0.3F;

        this.flipperrightfront.zRot = 0.4F;
        this.flipperrightfront.xRot = -0.3F - frontFlipperCrawl * 0.6F;
        this.flipperrightfront.yRot = -frontFlipperCrawl * 0.3F;

        // Back flippers - dragging and pushing
        float backFlipperCrawl = Mth.cos(limbSwing * crawlSpeed + (float)Math.PI) * limbSwingAmount;

        this.flipperleftback.zRot = -0.3F;
        this.flipperleftback.xRot = 0.2F + backFlipperCrawl * 0.4F;
        this.flipperleftback.yRot = backFlipperCrawl * 0.2F;

        this.flipperrightback.zRot = 0.3F;
        this.flipperrightback.xRot = 0.2F - backFlipperCrawl * 0.4F;
        this.flipperrightback.yRot = -backFlipperCrawl * 0.2F;

        // Neck strains upward while crawling
        this.neck.xRot = -0.2F + Mth.sin(limbSwing * crawlSpeed) * 0.1F * limbSwingAmount;
        this.neck2.xRot = -0.15F;
        this.neck3.xRot = -0.1F;
    }

    private void setupHeadTurning(float netHeadYaw, float headPitch) {
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
    }

    private void setupIdleAnim(float ageInTicks, boolean isInWater) {
        if (isInWater) {
            // Swimming idle - gentle floating motion
            float floatMotion = Mth.sin(ageInTicks * 0.05F) * 0.5F;
            this.body.y += floatMotion;

            // Gentle neck swaying
            float neckSway = Mth.sin(ageInTicks * 0.08F) * 0.05F;
            this.neck.xRot += neckSway;
            this.neck2.xRot += neckSway * 0.8F;
            this.neck3.xRot += neckSway * 0.6F;

            // Flippers drift slowly
            float flipperDrift = Mth.sin(ageInTicks * 0.06F) * 0.1F;
            this.flipperleftfront.zRot = -0.1F + flipperDrift;
            this.flipperrightfront.zRot = 0.1F - flipperDrift;
            this.flipperleftback.zRot = -0.1F - flipperDrift * 0.5F;
            this.flipperrightback.zRot = 0.1F + flipperDrift * 0.5F;

            // Tail drifts gently
            this.tail.yRot = Mth.sin(ageInTicks * 0.04F) * 0.05F;
        } else {
            // Land idle - breathing and resting
            float breathe = Mth.sin(ageInTicks * 0.1F) * 0.05F;
            this.body.y = 19.0F + breathe * 0.5F;
            this.body.xRot = 0.15F;

            // Neck relaxed but breathing
            this.neck.xRot = -0.1F + breathe * 0.2F;
            this.neck2.xRot = -0.05F;

            // Flippers resting on ground
            this.flipperleftfront.zRot = -0.5F;
            this.flipperrightfront.zRot = 0.5F;
            this.flipperleftback.zRot = -0.4F;
            this.flipperrightback.zRot = 0.4F;

            this.flipperleftfront.xRot = -0.2F;
            this.flipperrightfront.xRot = -0.2F;
            this.flipperleftback.xRot = 0.1F;
            this.flipperrightback.xRot = 0.1F;

            // Tail resting
            this.tail.xRot = 0.2F;
        }

        // Jaw opening animation (subtle idle movement for both)
        this.jaw.xRot = Mth.abs(Mth.sin(ageInTicks * 0.08F)) * 0.1F;
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