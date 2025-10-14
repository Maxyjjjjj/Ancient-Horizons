package com.fungoussoup.ancienthorizons.entity.client.raccoon;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.client.bactrian_camel.BactrianCamelAnimation;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RaccoonEntity;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RaccoonModel<T extends RaccoonEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "raccoon"), "main");
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart tail;
    private final ModelPart head;

    public RaccoonModel(ModelPart root) {
        this.body = root.getChild("body");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.tail = root.getChild("tail");
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 11.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition leg0 = partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(30, 6).addBox(-0.005F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 18.0F, 6.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(28, 31).addBox(0.005F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 18.0F, 6.0F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(36, 31).addBox(-0.005F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 18.0F, -1.0F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 39).addBox(0.005F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 18.0F, -1.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, 1.0F, -3.25F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 7.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 18).addBox(-4.5F, -3.5F, -6.0F, 9.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(30, 14).addBox(2.5F, -5.5F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 14).addBox(-4.5F, -5.5F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 0).addBox(-2.0F, 0.5F, -9.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -3.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSleeping()) {
            this.head.xRot = headPitch * ((float) Math.PI / 180F);
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        }
        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.leg3.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.leg2.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        this.leg0.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.leg1.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;

        this.animate(entity.sleepAnimationState, RaccoonAnimations.RACCOON_SLEEP, ageInTicks, 1.0F);

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg0.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }

}