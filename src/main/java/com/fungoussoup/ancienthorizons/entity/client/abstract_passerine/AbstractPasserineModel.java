package com.fungoussoup.ancienthorizons.entity.client.abstract_passerine;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractPasserineEntity;
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

import static com.fungoussoup.ancienthorizons.entity.client.abstract_passerine.AbstractPasserineAnimations.PASSERINE_DANCE;
import static com.fungoussoup.ancienthorizons.entity.client.abstract_passerine.AbstractPasserineAnimations.PASSERINE_FLY;

public class AbstractPasserineModel<T extends AbstractPasserineEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "passerine"), "main");
    private final ModelPart body;
    private final ModelPart headbase;
    private final ModelPart legright;
    private final ModelPart legleft;

    private AnimationDefinition currentAnimation;
    private float ageInTicks = 0.0F;

    public AbstractPasserineModel(ModelPart root) {
        this.body = root.getChild("body");
        this.headbase = this.body.getChild("headbase");
        this.legright = root.getChild("legright");
        this.legleft = root.getChild("legleft");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.5F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(1, 11).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 2.0F));

        PartDefinition headbase = body.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -2.0F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 0).addBox(-1.5F, -1.75F, -1.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(15, -1).addBox(0.0F, -3.75F, -0.75F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 6).addBox(-0.5F, -0.25F, -2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create(), PartPose.offset(-2.05F, -0.5F, -1.0F));

        PartDefinition cube_r1 = wingleft.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 13).addBox(0.025F, -1.5F, -0.5F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition wingleft2 = body.addOrReplaceChild("wingleft2", CubeListBuilder.create(), PartPose.offset(2.05F, -0.5F, -1.0F));

        PartDefinition cube_r2 = wingleft2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 13).addBox(-0.025F, -1.5F, -0.5F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition legright = partdefinition.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(16, 6).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 22.0F, 0.0F));

        PartDefinition legleft = partdefinition.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(16, 9).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 22.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        if (entity.onGround()) {
            this.applyWalkingAnimation(limbSwing, limbSwingAmount);
        } else {
            this.applyFlyingAnimation(ageInTicks);
            // Legs remain in a neutral position while flying
            this.legright.xRot = 0.0F;
            this.legleft.xRot = 0.0F;
        }

        if (entity.isDancing() && !entity.isFlying()) {
            this.applyDancingAnimation(ageInTicks);
        }
    }

    private void applyDancingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        this.currentAnimation = PASSERINE_DANCE;
    }

    private void applyFlyingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        this.currentAnimation = PASSERINE_FLY;
    }

    private void applyWalkingAnimation(float limbSwing, float limbSwingAmount) {
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw,-30f,30f);
        headPitch = Mth.clamp(headPitch,-25f,45);

        this.headbase.yRot = headYaw * ((float)Math.PI / 180f);
        this.headbase.xRot = headPitch * ((float)Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legright.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
