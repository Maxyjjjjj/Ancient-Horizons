package com.fungoussoup.ancienthorizons.entity.client.hoatzin;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.HoatzinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HoatzinModel<T extends HoatzinEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "hoatzin"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart crest;
    private final ModelPart tail;
    private final ModelPart lwing;
    private final ModelPart rwing;
    private final ModelPart lleg;
    private final ModelPart rleg;

    public HoatzinModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.crest = this.head.getChild("crest");
        this.tail = this.body.getChild("tail");
        this.lwing = this.body.getChild("lwing");
        this.rwing = this.body.getChild("rwing");
        this.lleg = this.body.getChild("lleg");
        this.rleg = this.body.getChild("rleg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(9, 0).addBox(-2.0F, -8.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 7).addBox(-1.5F, -5.0F, -1.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(11, 4).addBox(-0.5F, -4.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

        PartDefinition crest = head.addOrReplaceChild("crest", CubeListBuilder.create().texOffs(16, 7).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(-7, 15).mirror().addBox(-3.0F, 0.0F, 0.0F, 6.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -7.0F, 3.0F));

        PartDefinition lwing = body.addOrReplaceChild("lwing", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, -1.0F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -7.0F, -2.0F));

        PartDefinition rwing = body.addOrReplaceChild("rwing", CubeListBuilder.create().texOffs(0, 3).mirror().addBox(-1.0F, -1.0F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, -7.0F, -2.0F));

        PartDefinition lleg = body.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(12, 15).mirror().addBox(-1.5F, 0.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.5F, -4.0F, 2.0F));

        PartDefinition rleg = body.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(12, 15).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -4.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 48, 32);
    }

    @Override
    public void setupAnim(HoatzinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        // Crest follows head movement slightly
        this.crest.xRot = this.head.xRot * 0.3F;

        if (entity.isFlying()) {
            // Flying pose
            this.body.xRot = -0.3F; // Tilt body forward slightly

            // Wing flapping animation when flying
            float flapSpeed = 1.5F;
            float flapAmount = 1.2F;
            this.lwing.zRot = Mth.cos(ageInTicks * flapSpeed) * flapAmount;
            this.rwing.zRot = -Mth.cos(ageInTicks * flapSpeed) * flapAmount;

            // Slight wing rotation for dynamic look
            this.lwing.xRot = -0.1F + Mth.cos(ageInTicks * flapSpeed) * 0.15F;
            this.rwing.xRot = -0.1F + Mth.cos(ageInTicks * flapSpeed) * 0.15F;

            // Tail spread and tilted up during flight
            this.tail.xRot = -0.2F;

            // Legs tucked in during flight
            this.lleg.xRot = -1.2F;
            this.rleg.xRot = -1.2F;

            // Subtle body bob during flight
            this.body.y = 1.0F + Mth.cos(ageInTicks * 0.3F) * 0.5F;

        } else if (!entity.onGround() && entity.shouldGlide()) {
            // Gliding pose (wings spread but not flapping)
            this.body.xRot = -0.4F;
            this.lwing.zRot = 1.8F; // Wings spread wide
            this.rwing.zRot = -1.8F;
            this.lwing.xRot = -0.3F;
            this.rwing.xRot = -0.3F;
            this.tail.xRot = -0.3F;
            this.lleg.xRot = -0.8F;
            this.rleg.xRot = -0.8F;

        } else {
            this.lleg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.rleg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}