package com.fungoussoup.ancienthorizons.entity.client.beipiaosaurus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BeipiaosaurusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class BeipiaosaurusModel<T extends BeipiaosaurusEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "beipiaosaurus"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart armleft;
    private final ModelPart armleft2;
    private final ModelPart legleft;
    private final ModelPart kneeleft;
    private final ModelPart footleft;
    private final ModelPart legleft2;
    private final ModelPart kneeleft2;
    private final ModelPart footleft2;

    public BeipiaosaurusModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.neck2 = this.neck.getChild("neck2");
        this.head = this.neck2.getChild("head");
        this.tail = this.body.getChild("tail");
        this.armleft = this.body.getChild("armleft");
        this.armleft2 = this.body.getChild("armleft2");
        this.legleft = this.base.getChild("legleft");
        this.kneeleft = this.legleft.getChild("kneeleft");
        this.footleft = this.kneeleft.getChild("footleft");
        this.legleft2 = this.base.getChild("legleft2");
        this.kneeleft2 = this.legleft2.getChild("kneeleft2");
        this.footleft2 = this.kneeleft2.getChild("footleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -7.0F, 5.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 2.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -7.0F));

        PartDefinition neck2 = neck.addOrReplaceChild("neck2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r1 = neck2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -3.0F, -1.25F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition head = neck2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.25F, -2.5F, -0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(30, 24).addBox(-1.0F, -2.0F, -4.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 29).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition armleft = body.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(30, 8).addBox(-0.75F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 36).addBox(-0.75F, 5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 37).addBox(-0.75F, 5.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 8).addBox(-0.75F, 5.0F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.366F, -6.366F, 0.7854F, 0.0F, 0.0F));

        PartDefinition armleft2 = body.addOrReplaceChild("armleft2", CubeListBuilder.create().texOffs(30, 16).addBox(-0.25F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 37).addBox(-0.25F, 5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 37).addBox(-0.25F, 5.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 11).addBox(-0.25F, 5.0F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -0.366F, -6.366F, 0.7854F, 0.0F, 0.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(28, 29).addBox(-1.5F, -1.0F, -1.0F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -8.0F, 2.0F));

        PartDefinition kneeleft = legleft.addOrReplaceChild("kneeleft", CubeListBuilder.create().texOffs(32, 37).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 4.0F, 1.5F));

        PartDefinition footleft = kneeleft.addOrReplaceChild("footleft", CubeListBuilder.create().texOffs(16, 36).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition legleft2 = base.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, -1.0F, -1.0F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -8.0F, 2.0F));

        PartDefinition kneeleft2 = legleft2.addOrReplaceChild("kneeleft2", CubeListBuilder.create().texOffs(36, 37).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 4.0F, 1.5F));

        PartDefinition footleft2 = kneeleft2.addOrReplaceChild("footleft2", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        boolean isStealing = entity.getEntityData().get(BeipiaosaurusEntity.IS_STEALING);

        if (isStealing) {
            this.body.xRot = (float) (Math.PI / 4.0);
            this.body.y = -7.0F;
            this.neck.xRot = (float) (Math.PI / 4.0);
            this.neck2.xRot = (float) (-Math.PI / 4.0);

            this.armleft.xRot = (float) (Math.PI / 2.0) + Mth.cos(ageInTicks * 0.1F) * 0.1F;
            this.armleft2.xRot = (float) (Math.PI / 2.0) + Mth.cos(ageInTicks * 0.1F) * 0.1F;

            this.tail.xRot = (float) (-Math.PI / 6.0);
        } else {
            float swingScale = 1.0F;

            this.legleft.xRot = Mth.cos(limbSwing * 0.6662F) * swingScale * limbSwingAmount;
            this.legleft2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * swingScale * limbSwingAmount;

            float armSwingScale = 0.5F;
            this.armleft.xRot = Mth.cos(limbSwing * 0.6662F) * armSwingScale * limbSwingAmount + 0.7854F;
            this.armleft2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * armSwingScale * limbSwingAmount + 0.7854F;

            this.tail.yRot = Mth.cos(limbSwing * 0.3F) * 0.15F * limbSwingAmount;

            this.neck.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.25F;
            this.neck2.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.25F;
            this.head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.5F;

            this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.5F - 0.5236F;
            this.neck2.xRot += headPitch * ((float)Math.PI / 180F) * 0.25F;

            if (entity.isPassenger()) {
                this.legleft.xRot = Mth.cos(ageInTicks * 0.3F) * 0.1F;
                this.legleft2.xRot = Mth.cos(ageInTicks * 0.3F + (float)Math.PI) * 0.1F;
                this.body.xRot = -0.1F;
            }
        }
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        // First translate to the base position
        this.base.translateAndRotate(poseStack);

        // Then to the body
        this.body.translateAndRotate(poseStack);

        // Choose which front paw based on the arm
        if (humanoidArm == HumanoidArm.RIGHT) {
            // Translate to right front paw (which is actually the left paw in model space)
            this.armleft.translateAndRotate(poseStack);

            // Fine-tune position for the fishing rod
            // Move slightly forward and up from the paw center
            poseStack.translate(0.0F, -0.5F, -0.5F);

            // Rotate to make the fishing rod point outward naturally
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(45.0F));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(15.0F));
        } else {
            // Translate to left front paw (which is actually the right paw in model space)
            this.armleft2.translateAndRotate(poseStack);

            // Fine-tune position for the fishing rod
            poseStack.translate(0.0F, -0.5F, -0.5F);

            // Rotate to make the fishing rod point outward naturally
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(45.0F));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-15.0F));
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