package com.fungoussoup.ancienthorizons.entity.client.hypnovenator;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HypnovenatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.fungoussoup.ancienthorizons.entity.client.hypnovenator.HypnovenatorAnimations.HYPNOTIZE;

public class HypnovenatorModel<T extends HypnovenatorEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "hypnovenator"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neckbase;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart armleft;
    private final ModelPart armright;
    private final ModelPart haunchleft;
    private final ModelPart legleft;
    private final ModelPart haunchright;
    private final ModelPart legright;

    public HypnovenatorModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neckbase = this.body.getChild("neckbase");
        this.neck = this.neckbase.getChild("neck");
        this.head = this.neck.getChild("head");
        this.tail = this.body.getChild("tail");
        this.armleft = this.body.getChild("armleft");
        this.armright = this.body.getChild("armright");
        this.haunchleft = this.base.getChild("haunchleft");
        this.legleft = this.haunchleft.getChild("legleft");
        this.haunchright = this.base.getChild("haunchright");
        this.legright = this.haunchright.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 2.0F));

        PartDefinition neckbase = body.addOrReplaceChild("neckbase", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -6.0F));

        PartDefinition neck = neckbase.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(24, 26).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 7).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.025F))
                .texOffs(22, 34).addBox(-0.5F, 0.75F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.25F, 0.25F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(24, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 2.0F));

        PartDefinition armleft = body.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -0.5F, -4.5F));

        PartDefinition armright = body.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(8, 27).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -0.5F, -4.5F));

        PartDefinition haunchleft = base.addOrReplaceChild("haunchleft", CubeListBuilder.create().texOffs(16, 27).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(30, 11).addBox(-0.5F, 3.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 1.5F));

        PartDefinition legleft = haunchleft.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(32, 26).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 2.0F));

        PartDefinition haunchright = base.addOrReplaceChild("haunchright", CubeListBuilder.create().texOffs(30, 0).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 34).addBox(-0.5F, 3.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 1.5F));

        PartDefinition legright = haunchright.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(32, 32).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return base;
    }

    @Override
    public void setupAnim(HypnovenatorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;

        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.neckbase.yRot = headYaw * ((float) Math.PI / 180f);
        this.neckbase.xRot = headPitch * ((float) Math.PI / 180f);

        if (entity.getHypnosisAnimationTimeout() > 0) {
            this.animate(entity.hypnosisAnimationState, HYPNOTIZE, 1);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
