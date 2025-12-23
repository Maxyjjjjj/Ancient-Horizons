package com.fungoussoup.ancienthorizons.entity.client.latenivenatrix;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.LatenivenatrixEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class LatenivenatrixModel<T extends LatenivenatrixEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "latenivenatrix"), "main");

    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart headphones;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart tail;
    private final ModelPart legleft;
    private final ModelPart kneeleft;
    private final ModelPart footleft;
    private final ModelPart legright;
    private final ModelPart kneeright;
    private final ModelPart footright;

    public LatenivenatrixModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.belly = this.body.getChild("belly");
        this.neck = this.belly.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.headphones = this.head.getChild("headphones");
        this.armleft = this.belly.getChild("armleft");
        this.armright = this.belly.getChild("armright");
        this.tail = this.belly.getChild("tail");
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

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 77).addBox(-5.0F, -5.0F, -6.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(40, 77).addBox(-5.0F, -5.0F, -16.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neck = belly.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(82, 39).addBox(-3.0F, -13.0F, -2.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 0.0F, -16.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(80, 101).addBox(-3.0F, 0.975F, -3.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(40, 96).addBox(-3.0F, -3.05F, -6.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(100, 85).addBox(-2.0F, -1.0F, -12.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(100, 100).addBox(-1.5F, 0.975F, -11.9F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 1.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 97).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -1.0F, -6.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 106).addBox(-3.0F, -0.025F, -3.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.025F))
                .texOffs(106, 39).addBox(-3.0F, -2.025F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(100, 93).addBox(-2.0F, -0.025F, -9.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(100, 100).addBox(-1.5F, -1.025F, -8.9F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -3.0F));

        PartDefinition headphones = head.addOrReplaceChild("headphones", CubeListBuilder.create().texOffs(36, 106).addBox(-3.5F, -2.0F, -1.0F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(80, 106).addBox(3.5F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(106, 49).addBox(3.0F, 1.0F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(106, 49).mirror().addBox(-5.0F, 1.0F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(80, 106).mirror().addBox(-4.5F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -3.0F, -2.0F));

        PartDefinition armleft = belly.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(64, 96).addBox(-1.5F, -1.0F, 0.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(80, 77).addBox(0.25F, -1.0F, 0.0F, 0.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, -15.0F));

        PartDefinition cube_r2 = armleft.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(106, 60).addBox(-1.0F, 2.0F, -1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(54, 106).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(36, 97).addBox(-1.0F, 2.0F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 7.5F, 2.0F, -0.3054F, 0.0F, 0.2618F));

        PartDefinition armright = belly.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(80, 77).mirror().addBox(-0.25F, -1.0F, 0.0F, 0.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 96).mirror().addBox(-0.5F, -1.0F, 0.0F, 2.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, 0.0F, -15.0F));

        PartDefinition cube_r3 = armright.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(36, 97).mirror().addBox(0.0F, 2.0F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(106, 60).mirror().addBox(0.0F, 2.0F, -1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(54, 106).mirror().addBox(0.0F, 0.0F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.75F, 7.5F, 2.0F, -0.3054F, 0.0F, -0.2618F));

        PartDefinition tail = belly.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 39).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 35.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, 1.0F, 2.0F, 16.0F, 1.0F, 38.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 4.0F));

        PartDefinition legleft = body.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(82, 58).addBox(-3.75F, -2.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(100, 76).addBox(-3.75F, 8.0F, -2.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.025F)), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition kneeleft = legleft.addOrReplaceChild("kneeleft", CubeListBuilder.create().texOffs(22, 97).addBox(-2.5F, 0.0F, -3.0F, 4.0F, 9.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(-1.25F, 11.0F, 4.0F));

        PartDefinition footleft = kneeleft.addOrReplaceChild("footleft", CubeListBuilder.create().texOffs(106, 43).addBox(-1.25F, -1.0F, -5.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(106, 66).addBox(0.75F, 0.0F, -4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, 8.0F, -2.0F));

        PartDefinition cube_r4 = footleft.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(106, 55).addBox(-1.75F, -0.4627F, -2.1566F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -1.0036F, 0.0F, 0.0F));

        PartDefinition cube_r5 = footleft.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(106, 70).addBox(-1.75F, -3.1302F, -2.9245F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r6 = footleft.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(106, 70).addBox(-0.5F, 0.0F, -2.25F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-1.25F, -3.3736F, -3.1492F, 0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r7 = footleft.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(106, 55).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 0.0F, -1.0F, -1.0036F, 0.0F, 0.0F));

        PartDefinition legright = body.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(82, 58).mirror().addBox(-0.25F, -2.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.025F)).mirror(false)
                .texOffs(100, 76).mirror().addBox(-0.25F, 8.0F, -2.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition kneeright = legright.addOrReplaceChild("kneeright", CubeListBuilder.create().texOffs(22, 97).mirror().addBox(-1.5F, 0.0F, -3.0F, 4.0F, 9.0F, 3.0F, new CubeDeformation(-0.025F)).mirror(false), PartPose.offset(1.25F, 11.0F, 4.0F));

        PartDefinition footright = kneeright.addOrReplaceChild("footright", CubeListBuilder.create().texOffs(106, 43).mirror().addBox(-0.75F, -1.0F, -5.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(106, 66).mirror().addBox(-1.75F, 0.0F, -4.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.25F, 8.0F, -2.0F));

        PartDefinition cube_r8 = footright.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(106, 70).mirror().addBox(-0.5F, 0.0F, -2.25F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.025F)).mirror(false), PartPose.offsetAndRotation(1.25F, -3.3736F, -3.1492F, 0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r9 = footright.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(106, 55).mirror().addBox(0.75F, -0.4627F, -2.1566F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -1.0036F, 0.0F, 0.0F));

        PartDefinition cube_r10 = footright.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(106, 70).mirror().addBox(0.75F, -3.1302F, -2.9245F, 1.0F, 1.0F, 3.0F, new CubeDeformation(-0.025F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.5236F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset all rotations to default
        this.resetToDefaultPose();

        // HEADPHONES VISIBILITY: Only show when dancing
        this.headphones.visible = entity.isDancing();

        // HEAD LOOK
        this.applyHeadRotation(netHeadYaw, headPitch);

        // ANIMATION PRIORITY SYSTEM
        if (entity.isDancing()) {
            // Dancing animation overrides everything
            this.animateDance(ageInTicks);
        } else if (entity.isStalking() || entity.getPose() == Pose.CROUCHING) {
            // Stealth/stalking pose
            this.animateStealth(limbSwing, limbSwingAmount, ageInTicks);
        } else if (entity.isAggressive() || entity.isInSittingPose()) {
            // Combat stance or sitting
            if (entity.isInSittingPose()) {
                this.animateSitting();
            } else {
                this.animateCombat(limbSwing, limbSwingAmount, ageInTicks);
            }
        } else {
            // Normal walking/idle
            this.animateWalk(limbSwing, limbSwingAmount, ageInTicks);
        }

        // IDLE ANIMATIONS (always apply subtle movements)
        this.animateIdle(ageInTicks);
    }

    private void resetToDefaultPose() {
        // Reset body parts
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.belly.xRot = 0.0F;
        this.belly.yRot = 0.0F;

        this.neck.xRot = 0.0F;
        this.neck.yRot = 0.0F;
        this.neck.zRot = 0.0F;

        this.head.xRot = 0.0F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;

        this.jaw.xRot = 0.0F;

        this.tail.xRot = 0.0F;
        this.tail.yRot = 0.0F;

        this.armleft.xRot = 0.0F;
        this.armleft.yRot = 0.0F;
        this.armleft.zRot = 0.0F;

        this.armright.xRot = 0.0F;
        this.armright.yRot = 0.0F;
        this.armright.zRot = 0.0F;

        this.legleft.xRot = 0.0F;
        this.legleft.yRot = 0.0F;
        this.legleft.zRot = 0.0F;

        this.legright.xRot = 0.0F;
        this.legright.yRot = 0.0F;
        this.legright.zRot = 0.0F;

        this.kneeleft.xRot = 0.0F;
        this.kneeright.xRot = 0.0F;

        this.footleft.xRot = 0.0F;
        this.footright.xRot = 0.0F;
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        // Distribute head rotation across neck and head
        float yawRad = netHeadYaw * ((float)Math.PI / 180F);
        float pitchRad = headPitch * ((float)Math.PI / 180F);

        // Neck takes 60% of rotation
        this.neck.yRot = yawRad * 0.6F;
        this.neck.xRot = pitchRad * 0.6F;

        // Head takes remaining 40%
        this.head.yRot = yawRad * 0.4F;
        this.head.xRot = pitchRad * 0.4F;
    }

    // ==================== WALKING ANIMATION ====================
    private void animateWalk(float limbSwing, float limbSwingAmount, float ageInTicks) {
        // Limit limb swing for smooth animation
        limbSwingAmount = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);

        // LEG WALKING - Raptor-style bipedal movement
        float legSwing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float legSwingOffset = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        this.legleft.xRot = legSwing;
        this.legright.xRot = legSwingOffset;

        // Knee bending
        this.kneeleft.xRot = Math.max(0.0F, -legSwing * 0.5F);
        this.kneeright.xRot = Math.max(0.0F, -legSwingOffset * 0.5F);

        // Foot angle for ground contact
        this.footleft.xRot = legSwing * 0.3F;
        this.footright.xRot = legSwingOffset * 0.3F;

        // BODY BOBBING - Up and down movement while walking
        this.body.y = Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.5F * limbSwingAmount;

        // BODY SWAY - Side to side
        this.body.zRot = Mth.cos(limbSwing * 0.6662F) * 0.05F * limbSwingAmount;
        this.body.yRot = Mth.sin(limbSwing * 0.6662F) * 0.08F * limbSwingAmount;

        // TAIL SWAYING - Counter-balance movement
        this.tail.yRot = -Mth.sin(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
        this.tail.xRot = Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.1F * limbSwingAmount;

        // ARMS - Small counter-swing
        this.armleft.xRot = -legSwing * 0.3F;
        this.armright.xRot = -legSwingOffset * 0.3F;

        // NECK - Slight forward lean when running
        this.neck.xRot = -limbSwingAmount * 0.2F;
    }

    // ==================== IDLE ANIMATION ====================
    private void animateIdle(float ageInTicks) {
        // Subtle breathing animation
        float breathe = Mth.cos(ageInTicks * 0.09F) * 0.02F;
        this.belly.yRot = breathe;
        this.belly.xRot = breathe * 0.5F;

        // Slight head movements (curious behavior)
        float headBob = Mth.sin(ageInTicks * 0.05F) * 0.03F;
        this.head.xRot += headBob;

        // Tail gentle sway
        float tailSway = Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.tail.yRot += tailSway;
    }

    // ==================== DANCE ANIMATION ====================
    private void animateDance(float ageInTicks) {
        // ENERGETIC HEAD BOPPING
        float headBop = Mth.sin(ageInTicks * 0.5F) * 0.3F;
        this.head.xRot = headBop;
        this.head.zRot = Mth.cos(ageInTicks * 0.4F) * 0.15F;

        // NECK GROOVING
        this.neck.xRot = Mth.sin(ageInTicks * 0.5F + 0.5F) * 0.2F;
        this.neck.zRot = Mth.cos(ageInTicks * 0.3F) * 0.1F;

        // BODY BOUNCING
        this.body.y = Math.abs(Mth.sin(ageInTicks * 0.5F)) * 2.0F;
        this.body.zRot = Mth.sin(ageInTicks * 0.5F) * 0.1F;

        // BELLY WIGGLE
        this.belly.yRot = Mth.sin(ageInTicks * 0.6F) * 0.15F;

        // ARM FLAPPING - Like excited wings
        this.armleft.zRot = 0.3F + Mth.sin(ageInTicks * 0.8F) * 0.5F;
        this.armright.zRot = -0.3F - Mth.sin(ageInTicks * 0.8F) * 0.5F;

        this.armleft.xRot = Mth.cos(ageInTicks * 0.8F) * 0.3F;
        this.armright.xRot = Mth.cos(ageInTicks * 0.8F) * 0.3F;

        // LEG HOPPING
        float legBounce = Mth.sin(ageInTicks * 0.5F);
        this.legleft.xRot = legBounce * 0.3F;
        this.legright.xRot = -legBounce * 0.3F;

        this.kneeleft.xRot = Math.max(0.0F, -legBounce * 0.4F);
        this.kneeright.xRot = Math.max(0.0F, legBounce * 0.4F);

        // TAIL PARTY MODE - Wild swishing
        this.tail.yRot = Mth.sin(ageInTicks * 0.7F) * 0.5F;
        this.tail.xRot = Mth.cos(ageInTicks * 0.6F) * 0.2F;

        // JAW OPEN (like singing along)
        this.jaw.xRot = Math.abs(Mth.sin(ageInTicks * 0.8F)) * 0.3F;
    }

    // ==================== COMBAT ANIMATION ====================
    private void animateCombat(float limbSwing, float limbSwingAmount, float ageInTicks) {
        // Use walking animation as base
        animateWalk(limbSwing, limbSwingAmount, ageInTicks);

        // AGGRESSIVE STANCE - Lower body, forward lean
        this.body.xRot = -0.15F;
        this.body.y += 2.0F; // Lower to ground

        // NECK EXTENDED FORWARD - Predatory posture
        this.neck.xRot -= 0.3F;

        // HEAD SLIGHTLY DOWN - Ready to strike
        this.head.xRot -= 0.2F;

        // JAW OPENING - Threatening display
        float jawOpen = Math.abs(Mth.sin(ageInTicks * 0.3F)) * 0.2F;
        this.jaw.xRot = jawOpen;

        // ARMS READY - Slightly raised
        this.armleft.xRot -= 0.3F;
        this.armright.xRot -= 0.3F;
        this.armleft.zRot = 0.2F;
        this.armright.zRot = -0.2F;

        // TAIL RAISED - Alert posture
        this.tail.xRot = -0.2F;

        // TENSE BREATHING - Faster, more pronounced
        float tenseBreathe = Mth.cos(ageInTicks * 0.15F) * 0.03F;
        this.belly.xRot += tenseBreathe;
    }

    // ==================== STEALTH ANIMATION ====================
    private void animateStealth(float limbSwing, float limbSwingAmount, float ageInTicks) {
        // CROUCHED BODY - Very low to ground
        this.body.xRot = 0.3F; // Tilted back
        this.body.y = 8.0F; // Much lower

        // NECK LOW AND EXTENDED
        this.neck.xRot = -0.5F;
        this.neck.y = 2.0F;

        // HEAD FORWARD - Focused
        this.head.xRot = -0.1F;

        // LEGS BENT - Crouching pose
        this.legleft.xRot = 0.5F;
        this.legright.xRot = 0.5F;

        this.kneeleft.xRot = 0.8F;
        this.kneeright.xRot = 0.8F;

        // TAIL HORIZONTAL - Balance
        this.tail.xRot = -0.5F;

        // ARMS CLOSE TO BODY
        this.armleft.zRot = 0.1F;
        this.armright.zRot = -0.1F;

        // SLOW STALKING MOVEMENT (if moving)
        if (limbSwingAmount > 0.01F) {
            float stalkSwing = Mth.cos(limbSwing * 0.4F) * 0.5F * limbSwingAmount;
            this.legleft.xRot += stalkSwing;
            this.legright.xRot -= stalkSwing;

            // Smooth weight shifting
            this.body.zRot = Mth.sin(limbSwing * 0.4F) * 0.03F * limbSwingAmount;

            // Tail stabilization
            this.tail.yRot = -Mth.sin(limbSwing * 0.4F) * 0.2F * limbSwingAmount;
        }

        // SUBTLE BREATHING - Controlled, quiet
        float stealthBreathe = Mth.sin(ageInTicks * 0.06F) * 0.01F;
        this.belly.yRot = stealthBreathe;
    }

    // ==================== SITTING ANIMATION ====================
    private void animateSitting() {
        // BODY LOWERED - Resting position
        this.body.xRot = 0.4F;
        this.body.y = 8.0F;

        // LEGS FOLDED UNDER
        this.legleft.xRot = 1.2F;
        this.legright.xRot = 1.2F;

        this.kneeleft.xRot = -1.0F;
        this.kneeright.xRot = -1.0F;

        this.footleft.xRot = 0.3F;
        this.footright.xRot = 0.3F;

        // NECK UPRIGHT - Alert even when sitting
        this.neck.xRot = -0.3F;

        // TAIL CURLED AROUND
        this.tail.xRot = 0.2F;
        this.tail.yRot = 0.3F;

        // ARMS RELAXED
        this.armleft.xRot = 0.5F;
        this.armright.xRot = 0.5F;
        this.armleft.zRot = 0.15F;
        this.armright.zRot = -0.15F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return base;
    }

    // UTILITY METHODS

    /**
     * Smooth lerp between two angles
     */
    private float lerpAngle(float current, float target, float delta) {
        float diff = target - current;
        while (diff < -(float)Math.PI) diff += (float)Math.PI * 2.0F;
        while (diff >= (float)Math.PI) diff -= (float)Math.PI * 2.0F;
        return current + diff * delta;
    }

    /**
     * Apply attack animation (can be called from renderer when entity attacks)
     */
    public void applyAttackAnimation(float attackTime) {
        if (attackTime > 0.0F) {
            // Lunge forward
            this.body.xRot = -0.3F * attackTime;
            this.neck.xRot = -0.5F * attackTime;
            this.head.xRot = -0.2F * attackTime;

            // Jaw wide open
            this.jaw.xRot = 0.8F * attackTime;

            // Arms reaching
            this.armleft.xRot = -1.0F * attackTime;
            this.armright.xRot = -1.0F * attackTime;
            this.armleft.zRot = 0.3F * attackTime;
            this.armright.zRot = -0.3F * attackTime;
        }
    }
}