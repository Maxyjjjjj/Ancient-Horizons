package com.fungoussoup.ancienthorizons.entity.client.beluga_sturgeon;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BelugaSturgeonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BelugaSturgeonModel<T extends BelugaSturgeonEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "beluga_sturgeon"), "main");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart finsleft;
    private final ModelPart finsright;
    private final ModelPart tail;
    private final ModelPart finsleft2;
    private final ModelPart finsright2;

    public BelugaSturgeonModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.finsleft = this.body.getChild("finsleft");
        this.finsright = this.body.getChild("finsright");
        this.tail = this.body.getChild("tail");
        this.finsleft2 = this.tail.getChild("finsleft2");
        this.finsright2 = this.tail.getChild("finsright2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -12.0F, 12.0F, 12.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 0).addBox(-6.0F, -6.0F, -8.0F, 12.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(-4.0F, -2.0F, -16.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -12.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.5F, -11.0F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition finsleft = body.addOrReplaceChild("finsleft", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, -1.0F, 0.0F, 5.0F, 22.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(6.0F, 6.0F, -9.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition finsright = body.addOrReplaceChild("finsright", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, -1.0F, 0.0F, 5.0F, 22.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-6.0F, 6.0F, -9.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 36).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(48, 9).addBox(0.0F, -8.0F, 2.0F, 0.0F, 3.0F, 9.0F, new CubeDeformation(0.025F))
                .texOffs(0, 0).addBox(0.0F, -7.0F, 12.0F, 0.0F, 15.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 0.0F, 12.0F));

        PartDefinition finsleft2 = tail.addOrReplaceChild("finsleft2", CubeListBuilder.create().texOffs(0, 37).addBox(0.0F, 0.0F, -1.0F, 0.0F, 5.0F, 14.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(3.0F, 5.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition finsright2 = tail.addOrReplaceChild("finsright2", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, 0.0F, -1.0F, 0.0F, 5.0F, 14.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-3.0F, 5.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head turning - vanilla-like smooth head movement
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Swimming animation - similar to vanilla fish
        float swimSpeed = 1.0F;
        float swimIntensity = 0.6F;

        if (entity.isInWater()) {
            // Main body gentle swaying motion
            this.body.yRot = Mth.sin(ageInTicks * 0.1F) * 0.05F;

            // Tail wagging motion - primary propulsion
            float tailSwing = Mth.sin(ageInTicks * 0.4F * swimSpeed) * swimIntensity;
            this.tail.yRot = tailSwing * 0.3F;

            // Side fins movement - balanced between realism and vanilla
            float finSwing = Mth.sin(ageInTicks * 0.3F) * 0.4F;
            this.finsleft.zRot = -0.2618F + finSwing * 0.3F;
            this.finsright.zRot = 0.2618F - finSwing * 0.3F;

            // Rear fins follow tail movement but more subtle
            float rearFinSwing = tailSwing * 0.5F;
            this.finsleft2.zRot = -0.2618F + rearFinSwing * 0.2F;
            this.finsright2.zRot = 0.2618F - rearFinSwing * 0.2F;

            // Subtle up-down body movement while swimming
            this.body.x = Mth.cos(ageInTicks * 0.15F) * 0.5F;

        } else {
            // On land/out of water - minimal movement, vanilla-like flopping
            float flopIntensity = 1F;

            this.body.yRot = Mth.sin(ageInTicks * 0.8F) * 0.2F * flopIntensity;
            this.tail.yRot = Mth.sin(ageInTicks * 0.6F) * 0.4F * flopIntensity;

            // Fins flap desperately when out of water
            float flapSpeed = ageInTicks * 1.2F;
            this.finsleft.zRot = -0.2618F + Mth.sin(flapSpeed) * 0.6F * flopIntensity;
            this.finsright.zRot = 0.2618F - Mth.sin(flapSpeed) * 0.6F * flopIntensity;
            this.finsleft2.zRot = -0.2618F + Mth.sin(flapSpeed + 0.5F) * 0.4F * flopIntensity;
            this.finsright2.zRot = 0.2618F - Mth.sin(flapSpeed + 0.5F) * 0.4F * flopIntensity;
        }

        // Movement-based animation when swimming actively
        if (limbSwingAmount > 0.1F && entity.isInWater()) {
            float activeSwimMultiplier = limbSwingAmount * 1.5F;

            // More pronounced tail movement when actively swimming
            this.tail.yRot += Mth.sin(limbSwing * 0.8F) * activeSwimMultiplier * 0.4F;

            // Fins work harder during active swimming
            float activeFins = Mth.sin(limbSwing * 0.6F) * activeSwimMultiplier;
            this.finsleft.zRot += activeFins * 0.3F;
            this.finsright.zRot -= activeFins * 0.3F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}