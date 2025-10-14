package com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractLargeAzhdarchidEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.azhdarchidae.CryodrakonEntity;
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
import net.minecraft.world.entity.LivingEntity;

import static com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid.LargeAzhdarchidAnimations.*;

public abstract class AbstractLargeAzhdarchidModel<T extends AbstractLargeAzhdarchidEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "cryodrakon"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart[] saddleParts;
    private final ModelPart neckbase;
    private final ModelPart neck;
    protected final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart[] bridleParts;
    private final ModelPart tail;
    private final ModelPart wingleft;
    private final ModelPart wingleft2;
    private final ModelPart wingleft3;
    private final ModelPart wingleft4;
    private final ModelPart legleft;
    private final ModelPart legright;

    private AnimationDefinition currentAnimation;
    private float ageInTicks = 0.0F;

    public AbstractLargeAzhdarchidModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.saddleParts = new ModelPart[]{this.body.getChild("saddle")};
        this.neckbase = this.body.getChild("neckbase");
        this.neck = this.neckbase.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.bridleParts = new ModelPart[]{this.head.getChild("bridle")};
        this.tail = this.body.getChild("tail");
        this.wingleft = this.body.getChild("wingleft");
        this.wingleft2 = this.wingleft.getChild("wingleft2");
        this.wingleft3 = this.body.getChild("wingleft3");
        this.wingleft4 = this.wingleft3.getChild("wingleft4");
        this.legleft = this.base.getChild("legleft");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 5.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -14.0F, 6.0F, 6.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(72, 16).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -2.0F, -5.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition neckbase = body.addOrReplaceChild("neckbase", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -13.0F));

        PartDefinition neck = neckbase.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(82, 68).addBox(-1.5F, -28.0F, -1.5F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 30).addBox(-2.0F, -3.0F, -3.5F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.025F))
                .texOffs(0, 72).addBox(-1.0F, -3.0F, -17.5F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -28.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-1.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.5F));

        PartDefinition bridle = head.addOrReplaceChild("bridle", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -3.5F));

        PartDefinition cube_r2 = bridle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(62, 36).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 3.0F, 28.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.0908F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 5.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wingleft = body.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(32, 72).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 37.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(2, 26).addBox(0.5F, -1.0F, 1.0F, 0.0F, 35.0F, 9.0F, new CubeDeformation(0.05F)), PartPose.offset(3.0F, -4.0F, -12.0F));

        PartDefinition wingleft2 = wingleft.addOrReplaceChild("wingleft2", CubeListBuilder.create().texOffs(58, 68).addBox(-1.75F, -1.0F, -1.0F, 2.0F, 42.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(44, 24).addBox(-0.5F, -1.0F, 1.0F, 0.0F, 42.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.0F, 35.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        PartDefinition wingleft3 = body.addOrReplaceChild("wingleft3", CubeListBuilder.create().texOffs(40, 73).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 37.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 26).addBox(-0.5F, -1.0F, 1.0F, 0.0F, 35.0F, 9.0F, new CubeDeformation(0.05F)), PartPose.offset(-3.0F, -4.0F, -12.0F));

        PartDefinition wingleft4 = wingleft3.addOrReplaceChild("wingleft4", CubeListBuilder.create().texOffs(66, 68).addBox(-0.25F, -1.0F, -1.0F, 2.0F, 42.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(58, 0).addBox(0.5F, -1.0F, 1.0F, 0.0F, 42.0F, 7.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-1.0F, 35.0F, 0.0F, 0.0F, 0.0F, 2.9671F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(48, 73).addBox(-1.5F, -1.0F, -1.0F, 2.0F, 32.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 6).addBox(-1.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 9).addBox(0.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 12).addBox(-0.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 1.0F, 7.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(74, 68).addBox(-0.5F, -1.0F, -1.0F, 2.0F, 32.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 15).addBox(-0.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 18).addBox(1.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 21).addBox(0.5F, 30.0F, -3.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 1.0F, 7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.toggleInvisibleParts(entity);

        // Store ageInTicks for potential animation use
        this.ageInTicks = ageInTicks;

        if (!entity.isFlying()) {
            // Vanilla-style walking animation
            float walkSpeed = 1.0F;
            float walkDegree = 1.0F;
            this.wingleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.wingleft3.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
            this.legright.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
            this.legleft.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        } else {
            // Set animation based on flying state
            if (entity.isSoaring()) {
                this.currentAnimation = AZHDARCHID_SOAR;
            } else if (entity.isBarrelRolling() || entity.getLookAngle().y < 0) {
                this.currentAnimation = AZHDARCHID_DIVE;
            } else {
                this.currentAnimation = AZHDARCHID_FLY;
            }
        }

        if (entity.isAttacking()) {
            LivingEntity target = entity.getTarget();
            if (!entity.onGround()) {
                this.currentAnimation = AZHDARCHID_PECK_AIR;
            } else {
                assert target != null;
                if (target.getHitbox().maxY == 1f) {
                    this.currentAnimation = AZHDARCHID_PECK_LAND;
                } else {
                    this.currentAnimation = AZHDARCHID_ATTACK;
                }
            }
        }

        // Apply the animation if one is set
        if (this.currentAnimation != null) {
            this.animate(entity.getAnimationState(), this.currentAnimation, ageInTicks);
        }

        // Clamp head rotation values and apply them
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.neckbase.yRot = headYaw * ((float) Math.PI / 180f);
        this.neckbase.xRot = headPitch * ((float) Math.PI / 180f);
    }

    private void toggleInvisibleParts(T entity) {
        // Handle saddle and bridle visibility
        boolean flag = entity.isSaddled();
        boolean flag1 = entity.isVehicle();

        for (ModelPart modelpart : this.saddleParts) {
            modelpart.visible = flag;
        }
        for (ModelPart modelPart : this.bridleParts) {
            modelPart.visible = flag && flag1;
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