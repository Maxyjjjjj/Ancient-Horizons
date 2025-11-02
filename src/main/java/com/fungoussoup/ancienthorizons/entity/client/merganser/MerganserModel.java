package com.fungoussoup.ancienthorizons.entity.client.merganser;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MerganserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MerganserModel<T extends MerganserEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "merganser"), "main");
    private final ModelPart body;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart wingleft;
    private final ModelPart wingleft2;
    private final ModelPart legleft;
    private final ModelPart legleft2;

    public MerganserModel(ModelPart root) {
        this.body = root.getChild("body");
        this.headbase = this.body.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.tail = this.body.getChild("tail");
        this.wingleft = this.body.getChild("wingleft");
        this.wingleft2 = this.body.getChild("wingleft2");
        this.legleft = root.getChild("legleft");
        this.legleft2 = root.getChild("legleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 1.0F));

        PartDefinition headbase = body.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, -8.0F, -1.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 29).addBox(0.0F, -10.0F, -1.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(28, 5).addBox(-0.5F, -7.0F, -5.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 12).addBox(-0.5F, -6.0F, -5.5F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 29).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(28, 0).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, -5.0F));

        PartDefinition cube_r2 = wingleft.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, -1.0F, 0.0F, 0.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0044F, 0.0F));

        PartDefinition wingleft2 = body.addOrReplaceChild("wingleft2", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, -5.0F));

        PartDefinition cube_r3 = wingleft2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 15).addBox(0.0F, -1.0F, 0.0F, 0.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -0.0044F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(22, 34).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offset(1.0F, 20.0F, 1.0F));

        PartDefinition legleft2 = partdefinition.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(34, 29).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offset(-1.0F, 20.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(MerganserEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.headbase.yRot = headYaw * ((float) Math.PI / 180f);
        this.headbase.xRot = headPitch * ((float) Math.PI / 180f);

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
