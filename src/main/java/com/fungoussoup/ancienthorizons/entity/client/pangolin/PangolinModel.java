package com.fungoussoup.ancienthorizons.entity.client.pangolin;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.PangolinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PangolinModel<T extends PangolinEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "pangolin"), "main");
    private final ModelPart bodyb;
    private final ModelPart body;
    private final ModelPart base;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart noose;
    private final ModelPart tailb;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public PangolinModel(ModelPart root) {
        this.bodyb = root.getChild("bodyb");
        this.body = this.bodyb.getChild("body");
        this.base = this.body.getChild("base");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.noose = this.head.getChild("noose");
        this.tailb = this.body.getChild("tailb");
        this.tail1 = this.tailb.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.leg0 = this.bodyb.getChild("leg0");
        this.leg1 = this.bodyb.getChild("leg1");
        this.leg2 = this.bodyb.getChild("leg2");
        this.leg3 = this.bodyb.getChild("leg3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bodyb = partdefinition.addOrReplaceChild("bodyb", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 0.0F));

        PartDefinition body = bodyb.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition base = body.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(11, 10).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, -6.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(18, 24).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -3.0F));

        PartDefinition noose = head.addOrReplaceChild("noose", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, -3.0F));

        PartDefinition tailb = body.addOrReplaceChild("tailb", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 6.0F));

        PartDefinition tail1 = tailb.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(8, 8).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(10, 6).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 5.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(6, 11).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition leg0 = bodyb.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(14, 49).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 8).addBox(-1.5F, 3.0F, -1.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 2.0F, -3.5F));

        PartDefinition leg1 = bodyb.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(49, 27).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 8).addBox(-1.5F, 3.0F, -1.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 2.0F, -3.5F));

        PartDefinition leg2 = bodyb.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(52, 38).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 2.0F, 3.5F));

        PartDefinition leg3 = bodyb.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(52, 38).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 2.0F, 3.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(PangolinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.leg3.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.leg2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.leg0.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.leg1.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

        // Tail animation while walking
        float tailSwingSpeed = 0.5F;
        float tailSwingDegree = 0.5F;
        this.tailb.yRot = Mth.cos(ageInTicks * tailSwingSpeed) * tailSwingDegree;
        this.tail1.yRot = Mth.cos(ageInTicks * tailSwingSpeed + (float)Math.PI / 2) * tailSwingDegree;
        this.tail2.yRot = Mth.cos(ageInTicks * tailSwingSpeed + (float)Math.PI) * tailSwingDegree;
        this.tail3.yRot = Mth.cos(ageInTicks * tailSwingSpeed + 3 * (float)Math.PI / 2) * tailSwingDegree;

        // Hide animation
        if (entity.isHiding()) {
            this.animate(entity.hideAnimationState, PangolinAnimations.PANGOLIN_HIDE, ageInTicks, 1f);
        }
    }

    public ModelPart root() {
        return this.bodyb;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.neck.yRot = headYaw * ((float) Math.PI / 180f);
        this.neck.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bodyb.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

}
