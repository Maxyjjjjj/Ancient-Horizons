package com.fungoussoup.ancienthorizons.entity.client.pheasant;

import com.fungoussoup.ancienthorizons.entity.custom.mob.PheasantEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PheasantModel<T extends PheasantEntity> extends AgeableListModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("ancienthorizons", "pheasant"), "main");
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart legleft;
    private final ModelPart legright;
    private final ModelPart wingleft;
    private final ModelPart wingright;

    public PheasantModel(ModelPart root) {
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.tail = this.body.getChild("tail");
        this.legleft = root.getChild("legleft");
        this.legright = root.getChild("legright");
        this.wingleft = root.getChild("wingleft");
        this.wingright = root.getChild("wingright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 4).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -4.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 12).addBox(-1.0F, -1.5F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.75F, 4.25F, 0.48F, 0.0F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, -0.5F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 19.5F, 0.0F));

        PartDefinition legright = partdefinition.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(20, 17).addBox(-2.0F, -0.5F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 19.5F, 0.0F));

        PartDefinition wingleft = partdefinition.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(10, 17).addBox(0.0F, -1.0F, 0.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 15.0F, -3.0F));

        PartDefinition wingright = partdefinition.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 15.0F, -3.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PheasantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.neck.xRot = headPitch * ((float) Math.PI / 180F);
        this.neck.yRot = netHeadYaw * ((float) Math.PI / 180F);

        this.wingright.zRot = ageInTicks;
        this.wingleft.zRot = -ageInTicks;

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        wingleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        wingright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, neck, tail, wingleft, wingright, legleft, legright);
    }
}