package com.fungoussoup.ancienthorizons.entity.client.saichania;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SaichaniaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SaichaniaModel<T extends SaichaniaEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "saichania"), "main");

    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart armleft;
    private final ModelPart legleft;
    private final ModelPart armright;
    private final ModelPart legright;

    public SaichaniaModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail = this.body.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.armleft = this.base.getChild("armleft");
        this.legleft = this.base.getChild("legleft");
        this.armright = this.base.getChild("armright");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -7.0F, -25.0F, 16.0F, 13.0F, 30.0F, new CubeDeformation(0.0F))
                .texOffs(88, 57).addBox(0.5F, -8.5F, -24.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 0).addBox(0.5F, -8.5F, -16.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 7).addBox(0.5F, -8.5F, -8.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 14).addBox(0.5F, -8.5F, -0.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(78, 95).addBox(4.5F, -8.0F, -0.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 33).addBox(4.5F, -8.0F, -8.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 27).addBox(4.5F, -8.0F, -16.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 21).addBox(4.5F, -8.0F, -24.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(84, 101).addBox(8.0F, -6.5F, -24.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(72, 101).addBox(8.0F, -6.5F, -16.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(60, 97).addBox(8.0F, -6.5F, -8.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(48, 97).addBox(8.0F, -6.5F, -0.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 103).addBox(7.5F, -2.5F, -24.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(102, 86).addBox(7.5F, -2.5F, -16.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(102, 79).addBox(7.5F, -2.5F, -8.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(96, 101).addBox(7.5F, -2.5F, -0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(104, 57).addBox(7.25F, 0.5F, -24.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(36, 104).addBox(7.25F, 0.5F, -16.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(24, 104).addBox(7.25F, 0.5F, -8.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(12, 103).addBox(7.25F, 0.5F, -0.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(88, 57).addBox(-3.5F, -8.5F, -24.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 0).addBox(-3.5F, -8.5F, -16.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 7).addBox(-3.5F, -8.5F, -8.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 14).addBox(-3.5F, -8.5F, -0.5F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 21).addBox(-7.5F, -8.0F, -0.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 27).addBox(-7.5F, -8.0F, -8.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 33).addBox(-7.5F, -8.0F, -16.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(78, 95).addBox(-7.5F, -8.0F, -24.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(48, 97).addBox(-9.0F, -6.5F, -0.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(60, 97).addBox(-9.0F, -6.5F, -8.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(72, 101).addBox(-9.0F, -6.5F, -16.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(84, 101).addBox(-9.0F, -6.5F, -24.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(96, 101).addBox(-8.5F, -2.5F, -0.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(102, 79).addBox(-8.5F, -2.5F, -8.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(102, 86).addBox(-8.5F, -2.5F, -16.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 103).addBox(-8.5F, -2.5F, -24.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(12, 103).addBox(-8.25F, 0.5F, -0.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(24, 104).addBox(-8.25F, 0.5F, -8.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(36, 104).addBox(-8.25F, 0.5F, -16.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(104, 57).addBox(-8.25F, 0.5F, -24.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 10.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(48, 83).addBox(-3.5F, -2.5F, -8.0F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(88, 43).addBox(-3.5F, -2.5F, -3.0F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.2F))
                .texOffs(88, 50).addBox(-3.5F, -2.5F, -6.0F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -2.0F, -25.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(78, 83).addBox(-3.5F, -3.5F, -4.0F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.025F))
                .texOffs(58, 105).addBox(3.5F, -2.5F, -1.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(84, 64).addBox(-3.5F, -3.5F, -8.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(94, 95).addBox(-2.5F, -1.5F, -10.0F, 5.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(58, 105).addBox(-6.5F, -2.5F, -1.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -8.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 105).addBox(-1.5F, -6.5F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 105).addBox(-0.5F, -6.5F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(84, 73).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(84, 79).addBox(-2.5F, 0.0F, -6.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -4.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 43).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 5.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(46, 43).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(46, 64).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(-5.0F, -3.0F, 15.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

        PartDefinition armleft = base.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(0, 82).addBox(-5.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -14.0F, -9.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(24, 83).addBox(-5.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -14.0F, 11.0F));

        PartDefinition armright = base.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(24, 83).mirror().addBox(-1.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-8.0F, -14.0F, -9.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(0, 82).mirror().addBox(-1.0F, -1.0F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-8.0F, -14.0F, 11.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset rotations
        this.resetPose();

        boolean isDefending = entity.isDefending();
        boolean isGrazing = entity.isGrazing();
        int attackTick = entity.getAttackTick();

        // Apply defend pose animation if entity is defending
        if (entity.isDefending()) {
            applyDefendPose();
        } else {
            // Normal walking animation
            applyWalkingAnimation(limbSwing, limbSwingAmount);
        }

        // 3. GRAZING ANIMATION
        if (isGrazing) {
            this.neck.xRot = 0.5f; // Dip neck
            this.head.xRot = 0.3f; // Dip head
            this.body.xRot = 0.1f; // Slight forward tilt
            // Jaw nibble
            this.jaw.xRot = Math.abs(Mth.sin(ageInTicks * 0.3f)) * 0.2f;
        } else {
            // Standard Head Look (only if not grazing)
            this.neck.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.4f;
            this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.5f;
        }

        // 4. ATTACK ANIMATION (Tail Club Slam)
        if (attackTick > 0) {
            float attackProg = (10 - attackTick) / 10f; // 0 to 1
            float swing = Mth.sin(attackProg * (float)Math.PI); // Bell curve
            // Wind up and slam
            this.tail.yRot += swing * 1.5f;
            this.tail2.yRot += swing * 1.8f;
            this.tail3.yRot += swing * 2.0f;
            // Body twist into the swing
            this.body.yRot = swing * 0.2f;
        }

        // 5. DEFEND POSE (Overrides)
        if (isDefending) {
            applyDefendPose();
        }
    }

    private void resetPose() {
        this.base.zRot = 0;
        this.base.yRot = 0;
        this.body.y = -16.0F;
        this.body.xRot = 0;
        this.body.yRot = 0;
        this.neck.xRot = 0;
        this.neck.yRot = 0;
        this.head.xRot = 0;
        this.jaw.xRot = 0;
        this.tail.yRot = 0;
        this.tail2.yRot = 0;
        this.tail3.yRot = 0;
        // Reset limbs
        this.armleft.zRot = 0; this.armright.zRot = 0;
        this.legleft.zRot = 0; this.legright.zRot = 0;
        this.armleft.xRot = 0; this.armright.xRot = 0;
        this.legleft.xRot = 0; this.legright.xRot = 0;
    }

    private void applyDefendPose() {
        // Apply the defend pose animation from saichaniaAnimation
        this.body.y = -19.0F; // Lower body by 3 units (-16 - 3)
        this.armleft.zRot = -32.5F * ((float)Math.PI / 180F);
        this.armright.zRot = 32.5F * ((float)Math.PI / 180F);
        this.legleft.zRot = -32.5F * ((float)Math.PI / 180F);
        this.legright.zRot = 32.5F * ((float)Math.PI / 180F);
    }

    private void applyWalkingAnimation(float limbSwing, float limbSwingAmount) {
        // Quadruped walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;

        if (limbSwingAmount > 0.01f) {
            // Body rolls side to side as it steps
            float walkCycle = Mth.cos(limbSwing * walkSpeed);
            this.base.zRot = walkCycle * 0.05f * limbSwingAmount;
            this.body.xRot = Mth.sin(limbSwing * walkSpeed * 2.0f) * 0.05f * limbSwingAmount; // Breathing/bobbing

            // Quadruped Legs
            this.armleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.armright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
            this.legleft.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
            this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;

            // Tail sways opposite to the walk for balance
            float tailSwing = walkCycle * 0.2f * limbSwingAmount;
            this.tail.yRot += tailSwing;
            this.tail2.yRot += tailSwing * 1.2f;
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