package com.fungoussoup.ancienthorizons.entity.client.hare;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HareEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HareModel<T extends HareEntity> extends AgeableListModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "hare"), "main");

    private final ModelPart body;
    private final ModelPart frontLegLeft;
    private final ModelPart backLegRight;
    private final ModelPart backLegRight2;
    private final ModelPart frontLegRight;
    private final ModelPart head;
    private final ModelPart tail;

    public HareModel(ModelPart root) {
        this.body = root.getChild("body");
        this.frontLegLeft = root.getChild("frontLegLeft");
        this.backLegRight = root.getChild("backLegRight");
        this.backLegRight2 = root.getChild("backLegRight2");
        this.frontLegRight = root.getChild("frontLegRight");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -5.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition frontLegLeft = partdefinition.addOrReplaceChild("frontLegLeft", CubeListBuilder.create(), PartPose.offset(2.25F, 19.0F, -4.0F));

        PartDefinition frontLegLeft_r1 = frontLegLeft.addOrReplaceChild("frontLegLeft_r1", CubeListBuilder.create().texOffs(12, 23).addBox(2.0F, -5.0F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 5.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition backLegRight = partdefinition.addOrReplaceChild("backLegRight", CubeListBuilder.create(), PartPose.offset(-2.25F, 18.0F, 4.0F));

        PartDefinition cube_r1 = backLegRight.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 30).addBox(-1.0F, 1.0F, -0.25F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition frontLegLeft_r2 = backLegRight.addOrReplaceChild("frontLegLeft_r2", CubeListBuilder.create().texOffs(18, 15).addBox(2.0F, -4.0F, -2.5F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 2.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition backLegRight2 = partdefinition.addOrReplaceChild("backLegRight2", CubeListBuilder.create(), PartPose.offset(2.25F, 18.0F, 4.0F));

        PartDefinition cube_r2 = backLegRight2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 1.0F, -0.25F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition frontLegLeft_r3 = backLegRight2.addOrReplaceChild("frontLegLeft_r3", CubeListBuilder.create().texOffs(0, 23).addBox(2.0F, -4.0F, -2.5F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 2.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition frontLegRight = partdefinition.addOrReplaceChild("frontLegRight", CubeListBuilder.create(), PartPose.offset(-2.25F, 19.0F, -4.0F));

        PartDefinition frontLegRight_r1 = frontLegRight.addOrReplaceChild("frontLegRight_r1", CubeListBuilder.create().texOffs(20, 23).addBox(-4.0F, -5.0F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 5.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 15).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 28).addBox(-1.5F, 0.0F, -5.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 30).addBox(-2.5F, -7.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 31).addBox(0.5F, -7.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, -4.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(28, 23).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 5.0F, 0.3491F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot = headYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);

        float maxLimbRotation = 0.5F;

        this.frontLegLeft.xRot = Mth.cos(limbSwing * 0.6662F) * 1.0F * limbSwingAmount * maxLimbRotation;
        this.frontLegRight.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.0F * limbSwingAmount * maxLimbRotation;
        this.backLegRight.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * maxLimbRotation;
        this.backLegRight2.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * maxLimbRotation;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.frontLegLeft, this.frontLegRight, this.backLegRight, this.backLegRight2, this.tail);
    }
}