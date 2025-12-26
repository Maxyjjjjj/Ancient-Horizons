package com.fungoussoup.ancienthorizons.entity.client.brown_bear;

import com.fungoussoup.ancienthorizons.entity.custom.mob.BrownBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.fungoussoup.ancienthorizons.entity.client.brown_bear.BrownBearAnimations.*;

public class BrownBearModel<T extends BrownBearEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "brown_bear"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart eyesclosed;
    private final ModelPart pawleft;
    private final ModelPart pawright;
    private final ModelPart legright;
    private final ModelPart legright2;
    private float ageInTicks = 0.0F;
    private AnimationDefinition currentAnimation;

    public BrownBearModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.belly = this.body.getChild("belly");
        this.headbase = this.body.getChild("headbase");
        this.head = this.body.getChild("head");
        this.eyesclosed = this.head.getChild("eyesclosed");
        this.pawleft = this.body.getChild("pawleft");
        this.pawright = this.body.getChild("pawright");
        this.legright = this.base.getChild("legright");
        this.legright2 = this.base.getChild("legright2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 11.0F));

        PartDefinition belly = body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-8.5F, -9.0F, -14.5F, 17.0F, 17.0F, 31.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -11.0F));

        PartDefinition headbase = body.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -25.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 79).addBox(-5.5F, -8.0F, -8.0F, 11.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(70, 91).addBox(1.5F, -10.0F, -6.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(80, 91).addBox(-4.5F, -10.0F, -6.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(70, 79).addBox(-2.5F, -2.0F, -14.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -25.0F));

        PartDefinition eyesclosed = head.addOrReplaceChild("eyesclosed", CubeListBuilder.create().texOffs(122, 0).addBox(-3.5F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(122, 3).addBox(1.5F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, -3.5F, -7.05F));

        PartDefinition pawleft = body.addOrReplaceChild("pawleft", CubeListBuilder.create().texOffs(0, 48).addBox(-7.0F, -1.0F, -4.0F, 8.0F, 23.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 0.0F, -20.0F));

        PartDefinition pawright = body.addOrReplaceChild("pawright", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -1.0F, -4.0F, 8.0F, 23.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 0.0F, -20.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(64, 48).addBox(-1.0F, -1.0F, -4.0F, 8.0F, 23.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 0.0F, 11.0F));

        PartDefinition legright2 = base.addOrReplaceChild("legright2", CubeListBuilder.create().texOffs(0, 79).addBox(-7.0F, -1.0F, -4.0F, 8.0F, 23.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 0.0F, 11.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(BrownBearEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.pawleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.pawright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legright2.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

        if (entity.isSleeping()){
            this.applySleepingAnimation(ageInTicks);
        }
    }

    private void applyCowerAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(cower)) {
            this.currentAnimation = cower;
        }
    }

    private void applySleepingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(sleep)) {
            this.currentAnimation = sleep;
        }
    }

    private void applySittingAnimation (float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(sit)) {
            this.currentAnimation = sit;
        }
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.headbase.yRot = headYaw * ((float) Math.PI / 180f);
        this.headbase.xRot = headPitch * ((float) Math.PI / 180f);
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
