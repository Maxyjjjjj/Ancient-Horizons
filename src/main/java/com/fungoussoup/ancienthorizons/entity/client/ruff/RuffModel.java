package com.fungoussoup.ancienthorizons.entity.client.ruff;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.RuffEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RuffModel<T extends RuffEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "ruff"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingleft2;
    private final ModelPart legleft;
    private final ModelPart legleft2;

    public RuffModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.neck2 = this.neck.getChild("neck2");
        this.head = this.neck2.getChild("head");
        this.wingleft = this.body.getChild("wingleft");
        this.wingleft2 = this.body.getChild("wingleft2");
        this.legleft = this.base.getChild("legleft");
        this.legleft2 = this.base.getChild("legleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -1.35F, 3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -3.5F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(22, 5).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.5F));

        PartDefinition neck2 = neck.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(20, 27).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.288F, 0.0F, 0.0F));

        PartDefinition cube_r2 = neck2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 20).addBox(-1.5F, -3.25F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.25F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition head = neck2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 10).addBox(-1.0F, -1.75F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 14).addBox(-0.5F, -1.0F, -5.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.75F, 0.5F, 0.6109F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(0, 11).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(0.0F, -2.0F, 5.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.5F, -2.5F, 0.0F, 0.0436F, 0.0F));

        PartDefinition wingleft2 = body.addOrReplaceChild("wingleft2", CubeListBuilder.create().texOffs(12, 11).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(10, 20).addBox(0.0F, -2.0F, 5.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.5F, -2.5F, 0.0F, -0.0436F, 0.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(24, 27).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -3.0F, 0.0F));

        PartDefinition legleft2 = base.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(0, 28).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.neck.xRot = headPitch * ((float) Math.PI / 180F);
        this.neck.yRot = netHeadYaw * ((float) Math.PI / 180F);
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleft2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return base;
    }
}
