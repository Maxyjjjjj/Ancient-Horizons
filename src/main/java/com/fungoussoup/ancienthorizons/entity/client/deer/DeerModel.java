package com.fungoussoup.ancienthorizons.entity.client.deer;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.DeerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DeerModel<T extends DeerEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "deer"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart[] antlers;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legrightback;
    private final ModelPart legleftback;

    public DeerModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.head = this.body.getChild("head");
        this.antlers = new ModelPart[]{this.head.getChild("antlers")};
        this.legleftfront = this.base.getChild("legleftfront");
        this.legrightfront = this.base.getChild("legrightfront");
        this.legrightback = this.base.getChild("legrightback");
        this.legleftback = this.base.getChild("legleftback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0f, 0f, 0f));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -9.0F, 10.0F, 10.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, -12.0F, -2.0F, 6.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(56, 15).addBox(-3.0F, 1.0F, -2.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(56, 9).addBox(3.0F, -11.0F, 1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 12).addBox(-6.0F, -11.0F, 1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-2.0F, -10.0F, -6.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -9.0F));

        PartDefinition antlers = head.addOrReplaceChild("antlers", CubeListBuilder.create()
                .texOffs(56, 7).addBox(1.0F, -13.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(0.5F, -18.0F, 0.0F, 9.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(56, 8).addBox(-3.0F, -13.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(32, 28).addBox(-9.5F, -18.0F, 0.0F, 9.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition legleftfront = base.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(50, 40).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 13.0F, -7.0F));

        PartDefinition legrightfront = base.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(50, 55).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 13.0F, -7.0F));

        PartDefinition legrightback = base.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(22, 40).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 10.0F, 9.0F));

        PartDefinition legleftback = base.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(36, 40).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        this.toggleInvisibleParts(entity);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.legrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

        float partialTick = 0;

        if (entity.getEatAnimationTick() > 0){
            this.head.y = 6.0F + entity.getHeadEatPositionScale(partialTick) * 9.0F;
            this.head.xRot = entity.getHeadEatAngleScale(partialTick);
        }
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot = headYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);
    }

    private void toggleInvisibleParts(T entity) {
        boolean flag = entity.hasAntlers();

        for (ModelPart modelpart : this.antlers) {
            modelpart.visible = flag;
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
