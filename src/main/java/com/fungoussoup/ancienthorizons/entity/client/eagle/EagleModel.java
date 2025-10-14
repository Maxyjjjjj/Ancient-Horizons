package com.fungoussoup.ancienthorizons.entity.client.eagle;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EagleEntity;
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

import static com.fungoussoup.ancienthorizons.entity.client.eagle.EagleAnimations.*;

public class EagleModel<T extends EagleEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "eagle"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart head_base;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingright;
    private final ModelPart legleft;
    private final ModelPart legright;

    private AnimationDefinition currentAnimation;
    private float ageInTicks = 0.0F;

    public EagleModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.tail = this.body.getChild("tail");
        this.head_base = this.body.getChild("head_base");
        this.head = this.head_base.getChild("head");
        this.wingleft = this.body.getChild("wingleft");
        this.wingright = this.body.getChild("wingright");
        this.legleft = this.base.getChild("legleft");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(40, 0).addBox(-3.5F, -3.0F, -6.0F, 7.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 56).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 19).addBox(-5.0F, 0.0F, 1.0F, 10.0F, 0.0F, 9.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, 6.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition head_base = body.addOrReplaceChild("head_base", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -6.0F));

        PartDefinition head = head_base.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 56).addBox(-0.5F, -6.0F, -5.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 62).addBox(0.0F, -4.0F, -5.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(40, 45).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -1.0F, -1.0F, 0.0F, 8.0F, 20.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(3.5F, 3.0F, -4.0F, -0.1309F, 0.0F, -3.1416F));

        PartDefinition wingright = body.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(40, 28).addBox(0.0F, -1.0F, -1.0F, 1.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(0.5F, -1.0F, -1.0F, 0.0F, 8.0F, 20.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-3.5F, 3.0F, -4.0F, -0.1309F, 0.0F, -3.1416F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(16, 63).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.025F))
                .texOffs(32, 62).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -6.0F, 0.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(24, 63).addBox(-1.0F, 0.0F, -0.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.025F))
                .texOffs(48, 62).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void setupAnim(EagleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        if (!entity.isFlying()) {
            this.applyWalkingAnimation(limbSwing, limbSwingAmount);
            this.resetFlyingPose();
        } else {
            this.applyFlyingAnimation(ageInTicks, entity);
            // Legs remain in a neutral position while flying
            this.legright.xRot = 0.0F;
            this.legleft.xRot = 0.0F;
        }
        this.animate(entity.sitAnimationState, EAGLE_SIT, ageInTicks, 1f);
    }

    private void applyWalkingAnimation(float limbSwing, float limbSwingAmount) {
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    private void applyFlyingAnimation(float ageInTicks, EagleEntity entity) {
        this.ageInTicks = ageInTicks;
        if (entity.isCarryingPrey()){
            this.currentAnimation = EAGLE_FLY_CARRY;
        } else {
            this.currentAnimation = EAGLE_FLY;
        }

        float flyTime = ageInTicks * 0.6662F; // Wing flap speed
        float flapIntensity = 1.2F; // How intense the wing flapping is

        // Wing flapping animation - alternating up/down motion
        float wingFlapAngle = Mth.sin(flyTime) * flapIntensity;

        // Apply wing flapping with different phases for realism
        this.wingleft.zRot = -3.1416F + (wingFlapAngle * 0.8F);
        this.wingright.zRot = -3.1416F - (wingFlapAngle * 0.8F);

        // Add slight forward/backward wing motion for more realism
        this.wingleft.xRot = -0.1309F + (Mth.sin(flyTime + 0.5F) * 0.3F);
        this.wingright.xRot = -0.1309F + (Mth.sin(flyTime + 0.5F) * 0.3F);

        // Body movement during flight - subtle up/down motion
        float bodyBob = Mth.sin(flyTime * 0.5F) * 0.1F;
        this.body.y = -9.0F + bodyBob;

        // Body tilt based on flight dynamics
        this.body.xRot = -0.1745F + (Mth.sin(flyTime * 0.3F) * 0.05F);

        // Tail movement for stability simulation
        this.tail.yRot = Mth.sin(flyTime * 0.4F) * 0.1F;
        this.tail.xRot = 0.1745F + (Mth.sin(flyTime * 0.6F) * 0.05F);

        // Head stabilization during flight (reduces excessive head movement)
        float headStabilization = Mth.sin(flyTime * 0.2F) * 0.02F;
        this.head_base.xRot += headStabilization;

        // Leg positioning during flight - tucked up
        if (entity.isCarryingPrey()) {
            // Legs extended downward in gripping pose
            this.legleft.xRot = 0.3F + (Mth.sin(flyTime * 0.3F) * 0.05F);
            this.legright.xRot = 0.3F + (Mth.sin(flyTime * 0.3F + 0.2F) * 0.05F);

            // Optional: adjust leg zRot to simulate clutching motion
            this.legleft.zRot = 0.1F;
            this.legright.zRot = -0.1F;
        } else {
            // Tucked legs for normal flight
            float legTuck = -0.5F + (Mth.sin(flyTime * 0.8F) * 0.1F);
            this.legleft.xRot = legTuck;
            this.legright.xRot = legTuck;

            this.legleft.zRot = 0.0F;
            this.legright.zRot = 0.0F;
        }

        // Velocity-based animation adjustments if the entity has velocity data
        if (entity.getDeltaMovement().lengthSqr() > 0.01D) {
            float velocityFactor = (float) Math.min(entity.getDeltaMovement().lengthSqr() * 2.0D, 1.0D);
            this.wingleft.zRot += Mth.sin(flyTime * 2.0F) * velocityFactor * 0.2F;
            this.wingright.zRot -= Mth.sin(flyTime * 2.0F) * velocityFactor * 0.2F;

            this.body.xRot -= velocityFactor * 0.1F;
        }

        if (entity.getDeltaMovement().y() < -0.1D) {
            this.wingleft.zRot = -3.1416F + (wingFlapAngle * 0.3F);
            this.wingright.zRot = -3.1416F - (wingFlapAngle * 0.3F);
            this.body.xRot = -0.2F;
        }
    }


    private void resetFlyingPose() {
        // Reset body position and rotation to default when not flying
        this.body.y = -9.0F;
        this.body.xRot = -0.1745F;
        this.tail.yRot = 0.0F;
        this.tail.xRot = 0.1745F;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.head_base.yRot = headYaw * ((float)Math.PI / 180f);
        this.head_base.xRot = headPitch * ((float)Math.PI / 180f);
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
