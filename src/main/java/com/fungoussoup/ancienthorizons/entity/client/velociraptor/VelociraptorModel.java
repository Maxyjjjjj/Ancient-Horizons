package com.fungoussoup.ancienthorizons.entity.client.velociraptor;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.VelociraptorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VelociraptorModel<T extends VelociraptorEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "velociraptor"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck_base;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart wingleft;
    private final ModelPart wingright;
    private final ModelPart haunchleft;
    private final ModelPart legleft;
    private final ModelPart footleft;
    private final ModelPart haunchright;
    private final ModelPart legright;
    private final ModelPart footright;

    public VelociraptorModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck_base = this.body.getChild("neck_base");
        this.neck = this.neck_base.getChild("neck");
        this.head = this.neck.getChild("head");
        this.tail = this.body.getChild("tail");
        this.wingleft = this.body.getChild("wingleft");
        this.wingright = this.body.getChild("wingright");
        this.haunchleft = this.base.getChild("haunchleft");
        this.legleft = this.haunchleft.getChild("legleft");
        this.footleft = this.legleft.getChild("footleft");
        this.haunchright = this.base.getChild("haunchright");
        this.legright = this.haunchright.getChild("legright");
        this.footright = this.legright.getChild("footright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.5F, -7.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 2.0F));

        PartDefinition neck_base = body.addOrReplaceChild("neck_base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -7.0F));

        PartDefinition neck = neck_base.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(30, 26).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 20).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(26, 8).addBox(-1.0F, -0.5F, -6.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-1.5F, 0.5F, 1.0F, 3.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 2.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(34, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -6.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition wingright = body.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(30, 33).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -6.0F, -0.3054F, 0.0F, 0.0F));

        PartDefinition haunchleft = base.addOrReplaceChild("haunchleft", CubeListBuilder.create().texOffs(22, 26).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 2.0F));

        PartDefinition legleft = haunchleft.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(18, 35).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.5F));

        PartDefinition footleft = legleft.addOrReplaceChild("footleft", CubeListBuilder.create().texOffs(10, 35).addBox(0.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 35).addBox(0.0F, -2.0F, -2.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 4.0F, 0.0F));

        PartDefinition haunchright = base.addOrReplaceChild("haunchright", CubeListBuilder.create().texOffs(26, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -1.0F, 2.0F));

        PartDefinition legright = haunchright.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(16, 35).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 5.0F, 0.5F));

        PartDefinition footright = legright.addOrReplaceChild("footright", CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 35).addBox(0.0F, -2.0F, -2.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return base;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;

        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.neck.yRot = headYaw * ((float) Math.PI / 180f);
        this.neck.xRot = headPitch * ((float) Math.PI / 180f);
    }
}