package com.fungoussoup.ancienthorizons.entity.client.fisher;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.FisherEntity;
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

public class FisherModel<T extends FisherEntity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "fisher"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart body3;
    private final ModelPart pawleftback;
    private final ModelPart pawrightback;
    private final ModelPart tail;
    private final ModelPart body2;
    private final ModelPart pawleftfront;
    private final ModelPart pawrighttfront;
    private final ModelPart headbase;
    final ModelPart head;

    public FisherModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.body3 = this.body.getChild("body3");
        this.pawleftback = this.body3.getChild("pawleftback");
        this.pawrightback = this.body3.getChild("pawrightback");
        this.tail = this.body3.getChild("tail");
        this.body2 = this.body.getChild("body2");
        this.pawleftfront = this.body2.getChild("pawleftfront");
        this.pawrighttfront = this.body2.getChild("pawrighttfront");
        this.headbase = this.body2.getChild("headbase");
        this.head = this.headbase.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 18.5F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body3 = body.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 15).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 0.0F));

        PartDefinition pawleftback = body3.addOrReplaceChild("pawleftback", CubeListBuilder.create().texOffs(30, 10).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.25F, 5.0F, 6.0F));

        PartDefinition pawrightback = body3.addOrReplaceChild("pawrightback", CubeListBuilder.create().texOffs(30, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.25F, 5.0F, 6.0F));

        PartDefinition tail = body3.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.0F));

        PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(24, 15).addBox(-2.5F, 0.0F, -7.0F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 0.0F));

        PartDefinition pawleftfront = body2.addOrReplaceChild("pawleftfront", CubeListBuilder.create().texOffs(30, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.25F, 5.0F, -5.0F));

        PartDefinition pawrighttfront = body2.addOrReplaceChild("pawrighttfront", CubeListBuilder.create().texOffs(30, 5).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.25F, 5.0F, -5.0F));

        PartDefinition headbase = body2.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, -6.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 27).addBox(-2.5F, -3.5F, -4.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.025F))
                .texOffs(20, 27).addBox(-1.5F, -1.5F, -6.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 31).addBox(-5.0F, -2.75F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 31).addBox(3.0F, -2.75F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        // First translate to the base position
        this.base.translateAndRotate(poseStack);

        // Then to the body
        this.body.translateAndRotate(poseStack);

        // Then to the front body section
        this.body2.translateAndRotate(poseStack);

        // Choose which front paw based on the arm
        if (humanoidArm == HumanoidArm.RIGHT) {
            // Translate to right front paw (which is actually the left paw in model space)
            this.pawleftfront.translateAndRotate(poseStack);

            // Fine-tune position for the fishing rod
            // Move slightly forward and up from the paw center
            poseStack.translate(0.0F, -0.5F, -0.5F);

            // Rotate to make the fishing rod point outward naturally
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(45.0F));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(15.0F));
        } else {
            // Translate to left front paw (which is actually the right paw in model space)
            this.pawrighttfront.translateAndRotate(poseStack);

            // Fine-tune position for the fishing rod
            poseStack.translate(0.0F, -0.5F, -0.5F);

            // Rotate to make the fishing rod point outward naturally
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(45.0F));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-15.0F));
        }
    }

    @Override
    public ModelPart root() {
        return this.base;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        headYaw = Mth.clamp(headYaw,-30f,30f);
        headPitch = Mth.clamp(headPitch,-25f,45);

        this.headbase.yRot = headYaw * ((float)Math.PI / 180f);
        this.headbase.xRot = headPitch * ((float)Math.PI / 180f);

        if (!entity.isFishing()){
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.pawleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawrighttfront.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
            this.pawrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
