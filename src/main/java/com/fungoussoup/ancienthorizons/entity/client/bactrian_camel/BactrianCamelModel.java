package com.fungoussoup.ancienthorizons.entity.client.bactrian_camel;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BactrianCamel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BactrianCamelModel<T extends BactrianCamel> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "bactrian_camel"), "main");
    private static final float MAX_WALK_ANIMATION_SPEED = 2.0F;
    private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
    private static final float BABY_SCALE = 0.45F;
    private static final float BABY_Y_OFFSET = 29.35F;
    private static final String SADDLE = "saddle";
    private static final String BRIDLE = "bridle";
    private static final String REINS = "reins";
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public BactrianCamelModel(ModelPart root) {
        this.root = root;
        ModelPart innerRoot = root.getChild("root");
        this.body = innerRoot.getChild("body");
        this.head = this.body.getChild("head");
        this.saddleParts = new ModelPart[]{body.getChild("saddle"), this.head.getChild("bridle")};
        this.ridingParts = new ModelPart[]{this.head.getChild("reins")};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 10.5F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.5F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition fur = body.addOrReplaceChild("fur", CubeListBuilder.create().texOffs(84, 32).addBox(0.0F, 0.0F, -7.0F, 0.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(7.5F, 0.0F, -10.5F));

        PartDefinition fur2 = body.addOrReplaceChild("fur2", CubeListBuilder.create().texOffs(84, 32).addBox(0.0F, 0.0F, -7.0F, 0.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.5F, 0.0F, -10.5F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(108, 111).addBox(-2.5F, -21.0F, -21.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(104, 90).addBox(-3.5F, -21.0F, -15.0F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 78).addBox(-3.5F, -7.0F, -15.0F, 7.0F, 8.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -19.5F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(88, 105).addBox(-0.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -21.0F, -9.5F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(88, 108).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -21.0F, -9.5F));

        PartDefinition bridle = head.addOrReplaceChild("bridle", CubeListBuilder.create().texOffs(52, 78).addBox(-4.0F, -5.0F, -15.0F, 7.0F, 8.0F, 19.0F, new CubeDeformation(0.1F))
                .texOffs(0, 105).addBox(-4.0F, -19.0F, -15.0F, 7.0F, 14.0F, 7.0F, new CubeDeformation(0.1F))
                .texOffs(114, 32).addBox(-3.0F, -19.0F, -21.1F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.1F))
                .texOffs(98, 105).addBox(2.0F, -17.0F, -18.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(98, 105).mirror().addBox(-4.0F, -17.0F, -18.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, -2.0F, 0.0F));

        PartDefinition reins = head.addOrReplaceChild("reins", CubeListBuilder.create().texOffs(84, 46).addBox(0.0F, 0.0F, 0.0F, 0.0F, 7.0F, 23.0F, new CubeDeformation(0.0F))
                .texOffs(114, 43).addBox(-7.0F, 0.0F, 23.0F, 7.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(84, 46).addBox(-7.4F, 0.0F, 0.0F, 0.0F, 7.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, -18.0F, -17.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition hump = body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(86, 2).addBox(-4.5F, -5.0F, 2.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(86, 2).addBox(-4.5F, -5.0F, -10.5F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -10.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(114, 50).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 3.5F, 0.0873F, 0.0F, 0.0F));

        PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(0, 39).addBox(-8.0F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, new CubeDeformation(0.1F)), PartPose.offset(0.5F, 0.0F, 0.0F));

        PartDefinition left_front_leg = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(28, 105).addBox(-2.6F, 2.0F, -2.0F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9F, -3.0F, -20.5F));

        PartDefinition right_front_leg = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 105).addBox(-2.4F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, -3.0F, -20.0F));

        PartDefinition left_hind_leg = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(68, 105).addBox(-2.6F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9F, -3.0F, 0.0F));

        PartDefinition right_hind_leg = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(88, 111).addBox(-2.4F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, -3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(entity, netHeadYaw, headPitch, ageInTicks);
        this.toggleInvisibleParts(entity);
        this.animateWalk(BactrianCamelAnimation.CAMEL_WALK, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(entity.sitAnimationState, BactrianCamelAnimation.CAMEL_SIT, ageInTicks, 1.0F);
        this.animate(entity.sitPoseAnimationState, BactrianCamelAnimation.CAMEL_SIT_POSE, ageInTicks, 1.0F);
        this.animate(entity.sitUpAnimationState, BactrianCamelAnimation.CAMEL_STANDUP, ageInTicks, 1.0F);
        this.animate(entity.idleAnimationState, BactrianCamelAnimation.CAMEL_IDLE, ageInTicks, 1.0F);
        this.animate(entity.dashAnimationState, BactrianCamelAnimation.CAMEL_DASH, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(T entity, float netHeadYaw, float headPitch, float ageInTicks) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 45.0F);
        if (entity.getJumpCooldown() > 0) {
            float f = ageInTicks - (float)entity.tickCount;
            float f1 = 45.0F * ((float)entity.getJumpCooldown() - f) / 55.0F;
            headPitch = Mth.clamp(headPitch + f1, -25.0F, 70.0F);
        }

        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
    }

    private void toggleInvisibleParts(T entity) {
        boolean flag = entity.isSaddled();
        boolean flag1 = entity.isVehicle();

        for (ModelPart modelpart : this.saddleParts) {
            modelpart.visible = flag;
        }

        for (ModelPart modelpart1 : this.ridingParts) {
            modelpart1.visible = flag1 && flag;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if (this.young) {
            poseStack.pushPose();
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.translate(0.0F, 1.834375F, 0.0F);
            this.root().render(poseStack, buffer, packedLight, packedOverlay, color);
            poseStack.popPose();
        } else {
            this.root().render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}

