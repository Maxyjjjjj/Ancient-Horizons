package com.fungoussoup.ancienthorizons.entity.client.carcharodon;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.WhiteSharkEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WhiteSharkModel<T extends WhiteSharkEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "white_shark"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart findorsal;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart finleft;
    private final ModelPart finright;
    private final ModelPart tail;

    public WhiteSharkModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.findorsal = this.body.getChild("findorsal");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.finleft = this.body.getChild("finleft");
        this.finright = this.body.getChild("finright");
        this.tail = this.base.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -21.0F, 16.0F, 18.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition findorsal = body.addOrReplaceChild("findorsal", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, -4.0F));

        PartDefinition cube_r1 = findorsal.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -10.0F, 0.0F, 2.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 0).addBox(-8.0F, -10.0F, -16.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(44, 85).addBox(-6.0F, 0.0F, -14.0F, 12.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -21.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(84, 69).addBox(-7.0F, 0.0F, -15.0F, 14.0F, 6.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(82, 91).addBox(-6.0F, -3.0F, -14.0F, 12.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition finleft = body.addOrReplaceChild("finleft", CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -14.0F, 0.3491F, 0.0F, -0.7854F));

        PartDefinition finright = body.addOrReplaceChild("finright", CubeListBuilder.create().texOffs(15, 12).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 8.0F, -14.0F, 0.3491F, 0.0F, 0.7854F));

        PartDefinition tail = base.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, -8.0F, 0.0F, 9.0F, 15.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(58, 37).addBox(0.5F, -13.0F, 20.0F, 0.0F, 27.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 11.0F, 19.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(WhiteSharkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.5F;
        this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.5F;

        // Swimming animation - tail wagging
        float swimSpeed = 0.3F;
        float swimIntensity = 0.8F;

        // Check if shark is moving
        boolean isMoving = limbSwingAmount > 0.01F;

        if (isMoving) {
            // Increase animation speed when moving
            swimSpeed = 0.5F;
            swimIntensity = 1.2F * limbSwingAmount;
        }

        // Tail movement - side to side wagging
        float tailSwing = Mth.cos(ageInTicks * swimSpeed) * swimIntensity;
        this.tail.yRot = tailSwing * 0.4F;

        // Body undulation - creates wave motion through body
        float bodyUndulation = Mth.sin(ageInTicks * swimSpeed) * swimIntensity * 0.15F;
        this.body.yRot = bodyUndulation;

        // Slight body roll during swimming
        this.body.zRot = Mth.sin(ageInTicks * swimSpeed * 0.5F) * 0.05F * limbSwingAmount;

        // Dorsal fin sway - follows body movement
        this.findorsal.zRot = -bodyUndulation * 0.3F;

        // Pectoral fins - rowing motion when swimming
        float finBeat = Mth.sin(ageInTicks * swimSpeed * 1.5F) * 0.2F;

        if (isMoving) {
            // Active swimming - rowing motion
            this.finleft.zRot = -0.7854F + finBeat * limbSwingAmount;
            this.finright.zRot = 0.7854F - finBeat * limbSwingAmount;

            // Slight forward/backward rotation
            this.finleft.xRot = 0.3491F + Mth.cos(ageInTicks * swimSpeed * 1.5F) * 0.15F * limbSwingAmount;
            this.finright.xRot = 0.3491F + Mth.cos(ageInTicks * swimSpeed * 1.5F) * 0.15F * limbSwingAmount;
        } else {
            // Idle - gentle stabilizing motion
            this.finleft.zRot = -0.7854F + Mth.sin(ageInTicks * 0.1F) * 0.05F;
            this.finright.zRot = 0.7854F - Mth.sin(ageInTicks * 0.1F) * 0.05F;
        }

        // Jaw animation - subtle breathing/feeding motion
        float jawOpen = Mth.sin(ageInTicks * 0.15F) * 0.05F;
        this.jaw.xRot = Math.max(0.0F, jawOpen);

        // Attack animation - open jaw wider when targeting
        if (entity.getTarget() != null) {
            float attackProgress = Mth.sin(ageInTicks * 0.8F) * 0.3F;
            this.jaw.xRot = Math.max(0.0F, attackProgress);

            // More aggressive swimming motion
            this.tail.yRot = Mth.cos(ageInTicks * 0.7F) * 1.5F;
            this.body.yRot = Mth.sin(ageInTicks * 0.7F) * 0.25F;
        }

        // Pitch adjustment for diving/surfacing
        float pitch = entity.getXRot();
        this.body.xRot = pitch * ((float)Math.PI / 180F) * 0.3F;

        // Counter-rotate head slightly to keep it level
        this.head.xRot -= this.body.xRot * 0.5F;
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
