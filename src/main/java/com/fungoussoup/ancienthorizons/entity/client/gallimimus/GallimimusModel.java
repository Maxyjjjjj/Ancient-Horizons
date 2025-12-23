package com.fungoussoup.ancienthorizons.entity.client.gallimimus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.GallimimusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.fungoussoup.ancienthorizons.entity.client.gallimimus.GallimimusAnimations.GALLIMIMUS_RUN;
import static com.fungoussoup.ancienthorizons.entity.client.gallimimus.GallimimusAnimations.GALLIMIMUS_RECHARGE;

public class GallimimusModel<T extends GallimimusEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "gallimimus"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart[] saddleParts;
    private final ModelPart neckbase;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart[] ridingParts;
    private final ModelPart tail;
    private final ModelPart wingleft;
    private final ModelPart wingleft2;
    private final ModelPart haunchleft;
    private final ModelPart legleft;
    private final ModelPart footleft;
    private final ModelPart haunchleft2;
    private final ModelPart legleft2;
    private final ModelPart footleft2;

    public GallimimusModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.saddleParts = new ModelPart[]{this.body.getChild("saddle")};
        this.neckbase = this.body.getChild("neckbase");
        this.neck = this.neckbase.getChild("neck");
        this.head = this.neck.getChild("head");
        this.ridingParts = new ModelPart[]{this.head.getChild("bridle")};
        this.tail = this.body.getChild("tail");
        this.wingleft = this.body.getChild("wingleft");
        this.wingleft2 = this.body.getChild("wingleft2");
        this.haunchleft = this.base.getChild("haunchleft");
        this.legleft = this.haunchleft.getChild("legleft");
        this.footleft = this.legleft.getChild("footleft");
        this.haunchleft2 = this.base.getChild("haunchleft2");
        this.legleft2 = this.haunchleft2.getChild("legleft2");
        this.footleft2 = this.legleft2.getChild("footleft2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -11.0F, 10.0F, 12.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(0, 60).addBox(-5.0F, -6.0F, -3.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neckbase = body.addOrReplaceChild("neckbase", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -11.0F));

        PartDefinition neck = neckbase.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(72, 80).addBox(-2.0F, -17.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 60).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(88, 87).addBox(-1.0F, 1.0F, -6.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, -2.0F));

        PartDefinition bridle = head.addOrReplaceChild("bridle", CubeListBuilder.create().texOffs(64, 29).addBox(-2.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(55, 59).addBox(-2.25F, 0.0F, -0.5F, 0.0F, 6.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(55, 59).addBox(2.25F, 0.0F, -0.5F, 0.0F, 6.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(88, 93).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 3.0F, -3.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 34).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(52, 34).addBox(-2.0F, 2.0F, 3.0F, 4.0F, 3.0F, 19.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -4.0F, 11.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(64, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(38, 67).addBox(0.5F, 12.0F, -1.0F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -1.0F, -9.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingleft2 = body.addOrReplaceChild("wingleft2", CubeListBuilder.create().texOffs(0, 78).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(88, 77).addBox(-0.5F, 12.0F, -1.0F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -1.0F, -9.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition haunchleft = base.addOrReplaceChild("haunchleft", CubeListBuilder.create().texOffs(24, 78).addBox(-3.5F, -3.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.0F, 7.0F));

        PartDefinition legleft = haunchleft.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(84, 56).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 18.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(-1.5F, 7.0F, 3.0F));

        PartDefinition footleft = legleft.addOrReplaceChild("footleft", CubeListBuilder.create().texOffs(64, 23).addBox(-2.0F, -1.0F, -3.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 67).addBox(1.5F, -1.0F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(46, 71).addBox(0.0F, -1.0F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 75).addBox(-1.5F, -1.0F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition haunchleft2 = base.addOrReplaceChild("haunchleft2", CubeListBuilder.create().texOffs(48, 80).addBox(-0.5F, -3.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0F, 7.0F));

        PartDefinition legleft2 = haunchleft2.addOrReplaceChild("legleft2", CubeListBuilder.create().texOffs(88, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 18.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(1.5F, 7.0F, 3.0F));

        PartDefinition footleft2 = legleft2.addOrReplaceChild("footleft2", CubeListBuilder.create().texOffs(80, 23).addBox(-2.0F, -0.5F, -3.5F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(76, 29).addBox(1.5F, -0.5F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(80, 29).addBox(0.0F, -0.5F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(84, 29).addBox(-1.5F, -0.5F, -5.5F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(GallimimusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.toggleInvisibleParts(entity);

        // Check if recharging (takes priority over other animations)
        if (entity.isRecharging() && !entity.isGallimimusSprinting()) {
            // Manually apply recharge animation
            float time = ageInTicks * 0.05F; // Slow breathing effect
            float breathe = Mth.sin(time) * 0.025F;
            this.body.y += breathe * 8.0F;
            this.body.xRot += breathe;
            this.neck.xRot -= breathe * 0.5F;
            this.wingleft.zRot -= breathe * 2.0F;
            this.wingleft2.zRot += breathe * 2.0F;
        } else if (!entity.isGallimimusSprinting()) {
            // Normal walking animation
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.haunchleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.haunchleft2.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        } else {
            // Sprint animation
            animateWalk(GALLIMIMUS_RUN, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        }

        // Head look controls (always active)
        this.neck.xRot += headPitch * ((float)Math.PI / 180F);
        this.neck.yRot = netHeadYaw * ((float)Math.PI / 180F);
    }

    private void toggleInvisibleParts(GallimimusEntity entity) {
        boolean flag = entity.isSaddled();
        boolean flag1 = entity.isVehicle();

        for(ModelPart modelpart : this.saddleParts) {
            modelpart.visible = flag;
        }

        for(ModelPart modelpart1 : this.ridingParts) {
            modelpart1.visible = flag1 && flag;
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