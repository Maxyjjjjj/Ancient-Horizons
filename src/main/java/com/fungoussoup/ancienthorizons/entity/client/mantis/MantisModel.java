package com.fungoussoup.ancienthorizons.entity.client.mantis;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MantisEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.fungoussoup.ancienthorizons.entity.client.mantis.MantisAnimations.*;

public class MantisModel<T extends MantisEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "mantis"), "main");
    private final ModelPart abdomen;
    private final ModelPart breast;
    private final ModelPart raptorbaseleft;
    private final ModelPart raptormiddleleft;
    private final ModelPart raptorendleft;
    private final ModelPart raptorbaseleft2;
    private final ModelPart raptormiddleleft2;
    private final ModelPart raptorendleft2;
    private final ModelPart neck;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart mandibleleft;
    private final ModelPart mandibleright;
    private final ModelPart antennaleft;
    private final ModelPart antennaright;
    private final ModelPart legrightback;
    private final ModelPart legleftback;
    private final ModelPart legrightfront;
    private final ModelPart legleftfront;
    private float ageInTicks = 0.0F;
    private AnimationDefinition currentAnimation;

    public MantisModel(ModelPart root) {
        this.abdomen = root.getChild("abdomen");
        this.breast = this.abdomen.getChild("breast");
        this.raptorbaseleft = this.breast.getChild("raptorbaseleft");
        this.raptormiddleleft = this.raptorbaseleft.getChild("raptormiddleleft");
        this.raptorendleft = this.raptormiddleleft.getChild("raptorendleft");
        this.raptorbaseleft2 = this.breast.getChild("raptorbaseleft2");
        this.raptormiddleleft2 = this.raptorbaseleft2.getChild("raptormiddleleft2");
        this.raptorendleft2 = this.raptormiddleleft2.getChild("raptorendleft2");
        this.neck = this.breast.getChild("neck");
        this.headbase = this.neck.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.mandibleleft = this.head.getChild("mandibleleft");
        this.mandibleright = this.head.getChild("mandibleright");
        this.antennaleft = this.head.getChild("antennaleft");
        this.antennaright = this.head.getChild("antennaright");
        this.legrightback = root.getChild("legrightback");
        this.legleftback = root.getChild("legleftback");
        this.legrightfront = root.getChild("legrightfront");
        this.legleftfront = root.getChild("legleftfront");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition abdomen = partdefinition.addOrReplaceChild("abdomen", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition cube_r1 = abdomen.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 8.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition breast = abdomen.addOrReplaceChild("breast", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -2.5F));

        PartDefinition cube_r2 = breast.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -8.0F, -1.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition raptorbaseleft = breast.addOrReplaceChild("raptorbaseleft", CubeListBuilder.create().texOffs(0, 51).addBox(-1.5F, -1.0F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offset(2.0F, -4.0F, -4.0F));

        PartDefinition raptormiddleleft = raptorbaseleft.addOrReplaceChild("raptormiddleleft", CubeListBuilder.create().texOffs(14, 38).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(14, 47).addBox(-1.0F, 1.0F, -6.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -1.0F, -0.5672F, 0.0F, 0.0F));

        PartDefinition raptorendleft = raptormiddleleft.addOrReplaceChild("raptorendleft", CubeListBuilder.create().texOffs(26, 55).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -6.0F, 1.2654F, 0.0F, 0.0F));

        PartDefinition raptorbaseleft2 = breast.addOrReplaceChild("raptorbaseleft2", CubeListBuilder.create().texOffs(6, 51).addBox(-0.5F, -1.0F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offset(-2.0F, -4.0F, -4.0F));

        PartDefinition raptormiddleleft2 = raptorbaseleft2.addOrReplaceChild("raptormiddleleft2", CubeListBuilder.create().texOffs(32, 38).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(32, 47).addBox(-1.0F, 0.0F, -6.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 6.0F, -1.0F, -0.5672F, 0.0F, 0.0F));

        PartDefinition raptorendleft2 = raptormiddleleft2.addOrReplaceChild("raptorendleft2", CubeListBuilder.create().texOffs(24, 55).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 1.2654F, 0.0F, 0.0F));

        PartDefinition neck = breast.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(50, 49).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -5.0F));

        PartDefinition headbase = neck.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -3.5F, -1.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(50, 38).addBox(-3.0F, -1.0F, -2.5F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(50, 45).addBox(-2.0F, 3.0F, -2.5F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition mandibleleft = head.addOrReplaceChild("mandibleleft", CubeListBuilder.create().texOffs(16, 55).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 5.0F, -1.5F));

        PartDefinition mandibleright = head.addOrReplaceChild("mandibleright", CubeListBuilder.create().texOffs(20, 55).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 5.0F, -1.5F));

        PartDefinition antennaleft = head.addOrReplaceChild("antennaleft", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, -2.0F));

        PartDefinition cube_r3 = antennaleft.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 51).addBox(-0.5F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.5672F));

        PartDefinition antennaright = head.addOrReplaceChild("antennaright", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, -2.0F));

        PartDefinition cube_r4 = antennaright.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(14, 55).addBox(-0.5F, -5.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, -0.5672F));

        PartDefinition legrightback = partdefinition.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(0, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 15.0F, 2.0F, 0.0F, 0.7854F, -0.7854F));

        PartDefinition legleftback = partdefinition.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 15.0F, 2.0F, 0.0F, -0.7854F, 0.7854F));

        PartDefinition legrightfront = partdefinition.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(36, 30).addBox(-12.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 15.0F, -2.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition legleftfront = partdefinition.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(36, 34).addBox(-1.0F, -1.0F, -1.0F, 13.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 15.0F, -2.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(MantisEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (entity.isEating()) {
            this.applyEatingAnimation(ageInTicks);
        }
        if (entity.isGrabbing()) {
            this.applyCatchingAnimation(ageInTicks);
        }
        if (entity.isWaiting()) {
            this.applyWaitingAnimation(ageInTicks);
        }

        this.headbase.yRot += netHeadYaw * ((float)Math.PI / 180F);
        this.headbase.xRot += headPitch * ((float)Math.PI / 180F);

        // Subtly animate the antennae
        this.antennaleft.xRot += Mth.cos(ageInTicks * 0.4F) * 0.15F + 0.1F; // Subtle forward tilt
        this.antennaright.xRot += Mth.cos(ageInTicks * 0.4F + (float)Math.PI) * 0.15F + 0.1F;

        float walkAmplitude = 0.5F * limbSwingAmount;
        float walkSpeed = 0.6662F * 2.0F;

        this.legleftfront.zRot += Mth.cos(limbSwing * walkSpeed) * walkAmplitude;
        this.legrightback.zRot += Mth.cos(limbSwing * walkSpeed) * walkAmplitude;

        this.legrightfront.zRot += Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkAmplitude;
        this.legleftback.zRot += Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkAmplitude;

        this.abdomen.zRot = Mth.cos(limbSwing * walkSpeed) * 0.1F * limbSwingAmount;

        if (this.currentAnimation != CATCH) {
            float idleRaptorSway = Mth.cos(ageInTicks * 0.2F) * 0.05F;
            this.raptorbaseleft.xRot += idleRaptorSway;
            this.raptorbaseleft2.xRot -= idleRaptorSway;
        }
    }

    private void applyWaitingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(AMBUSH)) {
            this.currentAnimation = AMBUSH;
        }
    }

    private void applyCatchingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(CATCH)) {
            this.currentAnimation = CATCH;
        }
    }

    private void applyEatingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(EAT)) {
            this.currentAnimation = EAT;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        abdomen.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return abdomen;
    }
}
