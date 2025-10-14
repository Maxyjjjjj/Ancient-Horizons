package com.fungoussoup.ancienthorizons.entity.client.seagull;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SeagullEntity;
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

import static com.fungoussoup.ancienthorizons.entity.client.seagull.SeagullAnimations.SEAGULL_FLY;

public class SeagullModel<T extends SeagullEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "seagull"), "main");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingleft2;
    private final ModelPart legleft;
    private final ModelPart legleft2;
    private AnimationDefinition currentAnimation;
    private float ageInTicks = 0.0F;

    public SeagullModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.wingleft = root.getChild("wingleft");
        this.wingleft2 = root.getChild("wingleft2");
        this.legleft = root.getChild("legleft");
        this.legleft2 = root.getChild("legleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -5.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 16).addBox(-2.0F, -7.0F, -1.5F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 6).addBox(-1.0F, -5.0F, -5.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -4.5F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition wingleft = partdefinition.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(11, 20).addBox(0.0F, -1.0F, -1.0F, 1.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 15.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r1 = wingleft.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 0).addBox(0.5F, 0.25F, 7.0F, 0.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition wingleft2 = partdefinition.addOrReplaceChild("wingleft2", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 15.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r2 = wingleft2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(11, 9).addBox(-0.5F, 0.25F, 7.0F, 0.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(28, 30).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 20.0F, 0.0F));

        PartDefinition legleft2 = partdefinition.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(0, 30).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 20.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SeagullEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        if (entity.onGround()) {
            this.applyWalkingAnimation(limbSwing, limbSwingAmount);
        } else {
            this.applyFlyingAnimation(ageInTicks);
        }
    }

    private void applyFlyingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        this.currentAnimation = SEAGULL_FLY;
    }

    private void applyWalkingAnimation(float limbSwing, float limbSwingAmount) {
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

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
        wingleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        wingleft2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
