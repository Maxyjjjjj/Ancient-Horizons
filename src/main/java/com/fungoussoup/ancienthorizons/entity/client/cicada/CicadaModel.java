package com.fungoussoup.ancienthorizons.entity.client.cicada;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.CicadaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CicadaModel<T extends CicadaEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "cicada"), "main");
    private final ModelPart body;
    private final ModelPart abdomen;
    private final ModelPart head;
    private final ModelPart legs1;
    private final ModelPart legleft1;
    private final ModelPart legleft2;
    private final ModelPart legs2;
    private final ModelPart legleft3;
    private final ModelPart legleft4;
    private final ModelPart legs3;
    private final ModelPart legleft5;
    private final ModelPart legleft6;

    public CicadaModel(ModelPart root) {
        this.body = root.getChild("body");
        this.abdomen = this.body.getChild("abdomen");
        this.head = this.abdomen.getChild("head");
        this.legs1 = this.body.getChild("legs1");
        this.legleft1 = this.legs1.getChild("legleft1");
        this.legleft2 = this.legs1.getChild("legleft2");
        this.legs2 = this.body.getChild("legs2");
        this.legleft3 = this.legs2.getChild("legleft3");
        this.legleft4 = this.legs2.getChild("legleft4");
        this.legs3 = this.body.getChild("legs3");
        this.legleft5 = this.legs3.getChild("legleft5");
        this.legleft6 = this.legs3.getChild("legleft6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition abdomen = body.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -1.0F));

        PartDefinition head = abdomen.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.5F, -1.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition legleft1 = legs1.addOrReplaceChild("legleft1", CubeListBuilder.create().texOffs(6, 7).addBox(0.0F, -0.75F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.75F, 0.0F));

        PartDefinition legleft2 = legs1.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -0.75F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.75F, 0.0F));

        PartDefinition legs2 = body.addOrReplaceChild("legs2", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 0.5F, -1.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition legleft3 = legs2.addOrReplaceChild("legleft3", CubeListBuilder.create().texOffs(6, 7).addBox(0.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition legleft4 = legs2.addOrReplaceChild("legleft4", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.5F, 0.0F));

        PartDefinition legs3 = body.addOrReplaceChild("legs3", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0F, 1.0F, 0.5F, 0.7854F, 0.0F, 0.0F));

        PartDefinition legleft5 = legs3.addOrReplaceChild("legleft5", CubeListBuilder.create().texOffs(6, 7).addBox(0.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition legleft6 = legs3.addOrReplaceChild("legleft6", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset all rotations to default
        this.resetToDefaultPose();

        // Head tracking - cicadas can move their heads to look around
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.5F;
        this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.3F;

        // Body breathing animation - subtle up and down movement
        float breathingCycle = Mth.sin(ageInTicks * 0.15F) * 0.02F;
        this.body.y += breathingCycle;

        // Abdomen gentle pulsing - cicadas have visible breathing
        float abdomenPulse = Mth.sin(ageInTicks * 0.2F) * 0.03F;
        this.abdomen.xRot += abdomenPulse;

        // Walking animation - realistic insect gait
        if (limbSwingAmount > 0.01F) {
            this.animateWalk(limbSwing, limbSwingAmount);
        }

        // Idle animations when not walking
        if (limbSwingAmount < 0.01F) {
            this.animateIdle(ageInTicks);
        }

        // Add some randomized leg twitching for realism
        this.addRandomTwitches(ageInTicks, entity);
    }

    private void resetToDefaultPose() {
        // Reset body parts to neutral positions
        this.body.y = 22.0F;
        this.head.yRot = 0.0F;
        this.head.xRot = 0.0F;
        this.abdomen.xRot = 0.0F;

        // Reset leg groups to default rotations
        this.legs1.xRot = -0.7854F;
        this.legs2.xRot = 0.7854F;
        this.legs3.xRot = 0.7854F;

        // Reset individual legs
        this.legleft1.zRot = 0.0F;
        this.legleft2.zRot = 0.0F;
        this.legleft3.zRot = 0.0F;
        this.legleft4.zRot = 0.0F;
        this.legleft5.zRot = 0.0F;
        this.legleft6.zRot = 0.0F;
    }

    private void animateWalk(float limbSwing, float limbSwingAmount) {
        // Cicadas use a tripod gait - alternating sets of 3 legs
        // This creates realistic insect movement

        float walkSpeed = 1.2F; // Adjust for cicada walking speed
        float legLift = 0.4F * limbSwingAmount; // How high legs lift
        float legSwing = 0.3F * limbSwingAmount; // How much legs swing

        // Body bobbing while walking - insects bob up and down
        this.body.y += Mth.sin(limbSwing * walkSpeed * 2.0F) * 0.5F * limbSwingAmount;

        // Slight body rotation for natural walking motion
        this.body.zRot = Mth.sin(limbSwing * walkSpeed) * 0.05F * limbSwingAmount;

        // Tripod gait: legs 1, 3, 6 vs legs 2, 4, 5
        // Right front, left middle, right rear vs left front, right middle, left rear

        float cycle1 = Mth.sin(limbSwing * walkSpeed) * legLift;
        float cycle2 = Mth.sin(limbSwing * walkSpeed + (float)Math.PI) * legLift;

        // First tripod (legs 1, 3, 6)
        this.legleft1.zRot = cycle1 + legSwing;
        this.legleft3.zRot = cycle1 + legSwing;
        this.legleft6.zRot = cycle1 - legSwing;

        // Second tripod (legs 2, 4, 5)
        this.legleft2.zRot = cycle2 - legSwing;
        this.legleft4.zRot = cycle2 - legSwing;
        this.legleft5.zRot = cycle2 + legSwing;

        // Add forward/backward leg movement
        float legForwardBack = Mth.cos(limbSwing * walkSpeed) * 0.1F * limbSwingAmount;
        this.legs1.yRot = legForwardBack;
        this.legs2.yRot = -legForwardBack * 0.5F;
        this.legs3.yRot = legForwardBack;
    }

    private void animateIdle(float ageInTicks) {
        // Subtle idle animations - cicadas are relatively still but not motionless

        // Occasional antenna/head movement (simulated through head movement)
        float headBob = Mth.sin(ageInTicks * 0.05F) * 0.02F;
        this.head.xRot += headBob;

        // Very subtle leg adjustments
        float legIdle = Mth.sin(ageInTicks * 0.03F) * 0.01F;
        this.legleft1.zRot += legIdle;
        this.legleft2.zRot -= legIdle;
        this.legleft3.zRot += legIdle * 0.5F;
        this.legleft4.zRot -= legIdle * 0.5F;

        // Abdomen very subtle movement
        this.abdomen.xRot += Mth.sin(ageInTicks * 0.08F) * 0.01F;
    }

    private void addRandomTwitches(float ageInTicks, T entity) {
        // Add some randomized movement based on entity's position for uniqueness
        int entitySeed = entity.getId(); // Use entity ID as seed for consistent randomness

        // Create pseudo-random twitches that are consistent per entity
        float twitch1 = Mth.sin(ageInTicks * 0.7F + entitySeed) * 0.005F;
        float twitch2 = Mth.sin(ageInTicks * 0.9F + entitySeed * 2) * 0.005F;
        float twitch3 = Mth.sin(ageInTicks * 1.1F + entitySeed * 3) * 0.005F;

        // Apply subtle twitches to different legs
        this.legleft1.zRot += twitch1;
        this.legleft3.zRot += twitch2;
        this.legleft5.zRot += twitch3;
        this.legleft2.zRot -= twitch1 * 0.5F;
        this.legleft4.zRot -= twitch2 * 0.5F;
        this.legleft6.zRot -= twitch3 * 0.5F;

        // Random head movements - cicadas look around
        if (Math.sin(ageInTicks * 0.1F + entitySeed) > 0.95F) {
            this.head.yRot += Mth.sin(ageInTicks + entitySeed) * 0.1F;
        }
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