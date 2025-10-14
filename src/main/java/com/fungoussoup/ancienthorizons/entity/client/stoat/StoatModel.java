package com.fungoussoup.ancienthorizons.entity.client.stoat;

import com.fungoussoup.ancienthorizons.entity.custom.mob.StoatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class StoatModel<T extends StoatEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("ancienthorizons", "stoat"), "main");
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart pawleft;
    private final ModelPart pawright;
    private final ModelPart legright;
    private final ModelPart legleft;

    public StoatModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.pawleft = root.getChild("pawleft");
        this.pawright = root.getChild("pawright");
        this.legright = root.getChild("legright");
        this.legleft = root.getChild("legleft");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(14, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, -0.7418F, 0.0F, 0.0F));

        PartDefinition pawleft = partdefinition.addOrReplaceChild("pawleft", CubeListBuilder.create().texOffs(0, 13).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.75F, 22.0F, -4.0F));

        PartDefinition pawright = partdefinition.addOrReplaceChild("pawright", CubeListBuilder.create().texOffs(6, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.75F, 22.0F, -4.0F));

        PartDefinition legright = partdefinition.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.75F, 21.0F, 4.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(5, 5).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.75F, 21.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (!entity.isSleeping()){
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.pawleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.pawright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
            this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.legleft.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        } else {
            final float bodySleepAngle = 0.5F;   // tilt body forward
            final float headSleepPitch = 0.7F;   // head resting pitch
            final float limbTuckAngle  = 1.2F;   // limbs tucked

            // Body and head
            this.body.xRot = bodySleepAngle;
            this.head.xRot = headSleepPitch;
            this.head.yRot = 0F;

            // Limbs tucked under
            this.pawleft.xRot  = limbTuckAngle;
            this.pawright.xRot = limbTuckAngle;
            this.legright.xRot = limbTuckAngle * 0.8F;
            this.legleft.xRot  = limbTuckAngle * 0.8F;

            // Tail curled and subtle breathing
            this.tail.xRot = -0.5F + Mth.sin(ageInTicks * 0.1F) * 0.05F;
        }
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
        pawleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        pawright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}