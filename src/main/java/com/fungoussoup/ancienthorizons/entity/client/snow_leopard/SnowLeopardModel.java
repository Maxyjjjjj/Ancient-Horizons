package com.fungoussoup.ancienthorizons.entity.client.snow_leopard;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SnowLeopardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SnowLeopardModel<T extends SnowLeopardEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "snow_leopard"), "main");
    public static final ModelLayerLocation ARMOR_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "snow_leopard"), "armor");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart pawleftfront;
    private final ModelPart pawleftback;
    private final ModelPart pawrightfront;
    private final ModelPart pawrightback;

    public SnowLeopardModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.pawleftfront = root.getChild("pawleftfront");
        this.pawrightfront = root.getChild("pawrightfront");
        this.pawrightback = root.getChild("pawrightback");
        this.pawleftback = root.getChild("pawleftback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 6.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.5F, -9.0F, 8.0F, 9.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(0, 27).addBox(-4.0F, 4.5F, -9.0F, 8.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -6.0F));

        PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 46).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 3.0F, -1.1345F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(28, 46).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 1.5F, 11.0F, 1.1345F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(52, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 9.0F, 0.48F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(52, 12).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(44, 60).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 60).addBox(2.0F, -5.0F, -4.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 64).addBox(-3.0F, -5.0F, -4.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -15.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(44, 64).addBox(-2.0F, -1.5F, -7.75F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition pawleftfront = partdefinition.addOrReplaceChild("pawleftfront", CubeListBuilder.create().texOffs(52, 24).addBox(-1.5F, -3.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 13.0F, -7.0F));

        PartDefinition pawrightfront = partdefinition.addOrReplaceChild("pawrightfront", CubeListBuilder.create().texOffs(52, 42).addBox(-2.5F, -3.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 13.0F, -7.0F));

        PartDefinition pawrightback = partdefinition.addOrReplaceChild("pawrightback", CubeListBuilder.create().texOffs(28, 58).addBox(-2.5F, -3.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 13.0F, 6.0F));

        PartDefinition pawleftback = partdefinition.addOrReplaceChild("pawleftback", CubeListBuilder.create().texOffs(0, 60).addBox(-1.5F, -3.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 13.0F, 6.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SnowLeopardEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        // reset poses for all known parts (body is the root of the body hierarchy)
        this.body.resetPose();
        this.head.resetPose();
        this.pawleftfront.resetPose();
        this.pawrightfront.resetPose();
        this.pawleftback.resetPose();
        this.pawrightback.resetPose();

        // Head follows look unless the leopard is "holding tail" (special pose)
        if (!entity.isHoldingTail()) {
            this.head.xRot = headPitch * ((float) Math.PI / 180F);
            this.head.yRot = headYaw * ((float) Math.PI / 180F);
        } else {
            animate(entity.holdTailAnimationState, SnowLeopardAnimations.HOLD_TAIL, ageInTicks, 1F);
        }
        if (!entity.isSitting()){
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.pawleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
            this.pawrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        } else {
            animate(entity.sitAnimationState, SnowLeopardAnimations.SNOW_LEOPARD_SIT, ageInTicks, 1f);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pawleftfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pawrightfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pawrightback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pawleftback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
