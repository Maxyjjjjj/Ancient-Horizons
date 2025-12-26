package com.fungoussoup.ancienthorizons.entity.client.koala;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.KoalaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class KoalaModel<T extends KoalaEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "koala"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart legleft;
    private final ModelPart legright;

    public KoalaModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.head = this.body.getChild("head");
        this.armleft = this.body.getChild("armleft");
        this.armright = this.body.getChild("armright");
        this.legleft = this.base.getChild("legleft");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -2.5F, -4.5F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.025F))
                .texOffs(12, 28).addBox(-1.0F, 0.5F, -5.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 24).addBox(2.0F, -3.5F, -2.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 9).addBox(-5.0F, -3.5F, -2.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.5F));

        PartDefinition armleft = body.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(22, 14).addBox(-2.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(3.0F, 1.0F, -4.25F));

        PartDefinition armright = body.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(-3.0F, 1.0F, -4.25F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(22, 23).addBox(-2.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(3.0F, 1.0F, 2.25F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(-3.0F, 1.0F, 2.25F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // 1. Head Rotation (Standard)
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        // 2. Manual Walk Animation (Only if NOT sleeping)
        if (!entity.isSleeping()) {
            float walkSpeed = 0.6662F;
            float walkDegree = 1.0F; // Slightly lower degree for a slow koala walk

            // Diagonal gait: Front Left + Back Right move together
            float swing = (float)Math.cos(limbSwing * walkSpeed);
            float invSwing = (float)Math.cos(limbSwing * walkSpeed + (float)Math.PI);

            this.armleft.xRot = swing * walkDegree * limbSwingAmount;
            this.legright.xRot = swing * walkDegree * limbSwingAmount;

            this.armright.xRot = invSwing * walkDegree * limbSwingAmount;
            this.legleft.xRot = invSwing * walkDegree * limbSwingAmount;
        }

        // 3. Apply Exported Keyframe Animations
        // These will override the "resetPose" positions for the specific parts they animate
        this.animate(entity.sitAnimationState, KoalaAnimations.KSIT, ageInTicks);
        this.animate(entity.climbAnimationState, KoalaAnimations.KCLIMB, ageInTicks);
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