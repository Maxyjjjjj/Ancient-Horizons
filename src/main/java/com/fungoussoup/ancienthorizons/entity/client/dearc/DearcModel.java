package com.fungoussoup.ancienthorizons.entity.client.dearc;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.DearcEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DearcModel<T extends DearcEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "dearc"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart wingleftbase;
    private final ModelPart winglefttip;
    private final ModelPart wingrightbase;
    private final ModelPart wingrighttip;
    private final ModelPart legright;
    private final ModelPart legleft;

    public DearcModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.headbase = this.neck.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.tail1 = this.body.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.wingleftbase = this.body.getChild("wingleftbase");
        this.winglefttip = this.wingleftbase.getChild("winglefttip");
        this.wingrightbase = this.body.getChild("wingrightbase");
        this.wingrighttip = this.wingrightbase.getChild("wingrighttip");
        this.legright = this.base.getChild("legright");
        this.legleft = this.base.getChild("legleft");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 19.5F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -6.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 2.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(10, 30).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition headbase = neck.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -1.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 2).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.025F))
                .texOffs(35, 11).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.125F))
                .texOffs(42, 8).addBox(-0.5F, -3.0F, -1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(36, 5).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 24).addBox(-0.5F, -0.75F, -6.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, 1.0F, 1.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 32).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.5F, 0.25F, -4.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(30, 26).addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-0.5F, 0.25F, -4.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -0.025F, -5.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(6, 36).addBox(-1.5F, 0.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.025F))
                .texOffs(18, 30).addBox(-1.0F, -0.9F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.5F, 0.0F, -1.0F));

        PartDefinition cube_r3 = jaw.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(30, 21).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.25F, -5.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition cube_r4 = jaw.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(30, 31).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-1.0F, 0.25F, -5.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(30, 13).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(30, 17).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.025F))
                .texOffs(0, 34).addBox(0.0F, -1.0F, 3.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition wingleftbase = body.addOrReplaceChild("wingleftbase", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, -0.5F, -1.0F, 0.0F, 6.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.5F, 0.0F, -5.0F, 0.1309F, 0.0F, -0.0436F));

        PartDefinition winglefttip = wingleftbase.addOrReplaceChild("winglefttip", CubeListBuilder.create().texOffs(14, 11).addBox(0.0F, 0.0F, -1.0F, 0.0F, 15.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, 2.3998F, 0.0F, 0.2618F));

        PartDefinition wingrightbase = body.addOrReplaceChild("wingrightbase", CubeListBuilder.create().texOffs(22, 0).addBox(0.0F, -0.5F, -1.0F, 0.0F, 6.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-1.5F, 0.0F, -5.0F, 0.1309F, 0.0F, 0.0436F));

        PartDefinition wingrighttip = wingrightbase.addOrReplaceChild("wingrighttip", CubeListBuilder.create().texOffs(22, 13).addBox(0.0F, 0.0F, -1.0F, 0.0F, 15.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, 2.3998F, 0.0F, -0.2618F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(10, 24).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.025F))
                .texOffs(10, 29).addBox(-0.5F, 4.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.025F)), PartPose.offset(-1.0F, 0.5F, 3.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(26, 32).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.025F))
                .texOffs(6, 34).addBox(-0.5F, 4.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.025F)), PartPose.offset(1.0F, 0.5F, 3.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(DearcEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float PI = (float) Math.PI;

        this.neck.yRot = netHeadYaw * (PI / 180F) * 0.5F;
        this.headbase.yRot = netHeadYaw * (PI / 180F) * 0.5F;
        this.headbase.xRot += headPitch * (PI / 180F) * 0.5F;

        this.legright.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legleft.xRot = Mth.cos(limbSwing * 0.6662F + PI) * 1.4F * limbSwingAmount;

        this.tail1.yRot = Mth.cos(ageInTicks * 0.08F) * 0.1F;
        this.tail2.yRot = Mth.cos(ageInTicks * 0.08F + 1.0F) * 0.2F;

        if (entity.isFlying()) {
            float wingFlap = Mth.cos(ageInTicks * 1.2F) * 1.2F;
            this.wingrightbase.xRot += wingFlap;
            this.wingrighttip.xRot += Mth.cos(ageInTicks * 1.2F) * 0.6F;
            this.wingleftbase.xRot += wingFlap;
            this.winglefttip.xRot += Mth.cos(ageInTicks * 1.2F) * 0.6F;
        }
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
