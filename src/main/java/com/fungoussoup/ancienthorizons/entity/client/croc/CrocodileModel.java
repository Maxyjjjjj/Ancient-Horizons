package com.fungoussoup.ancienthorizons.entity.client.croc;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.CrocodileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import static com.fungoussoup.ancienthorizons.entity.client.croc.CrocodileAnimations.*;

public class CrocodileModel<T extends CrocodileEntity> extends HierarchicalModel<T> {
    @Override
    public ModelPart root() {
        return base;
    }

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "crocodile"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart headbase;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legleftback;
    private final ModelPart legrightback;

    public CrocodileModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.tail = this.body.getChild("tail");
        this.headbase = this.body.getChild("headbase");
        this.head = this.headbase.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.legleftfront = this.base.getChild("legleftfront");
        this.legrightfront = this.base.getChild("legrightfront");
        this.legleftback = this.base.getChild("legleftback");
        this.legrightback = this.base.getChild("legrightback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -8.5F, 8.0F, 6.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 26).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 11.5F));

        PartDefinition headbase = body.addOrReplaceChild("headbase", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, -8.5F));

        PartDefinition head = headbase.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 51).addBox(-3.5F, -1.0F, -6.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(36, 0).addBox(-2.5F, -1.0F, -21.0F, 5.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(46, 45).addBox(-3.5F, -4.0F, -6.0F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 31).addBox(0.5F, -6.0F, -5.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 26).addBox(-3.5F, -6.0F, -5.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 26).addBox(-2.5F, -2.0F, -15.0F, 5.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -6.0F));

        PartDefinition legleftfront = base.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -5.0F, -8.0F));

        PartDefinition legrightfront = base.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, -8.0F));

        PartDefinition legleftback = base.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -5.0F, 9.0F));

        PartDefinition legrightback = base.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(CrocodileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        boolean isBasking = entity.isBasking();
        if (isBasking) {
            // Basking pose - mouth open, relaxed position

            // Open jaw for thermoregulation
            this.jaw.xRot = -0.5F + (float) Math.sin(ageInTicks * 0.03F) * 0.05F; // Slight breathing motion

            // Slightly raise head
            this.head.xRot = -0.1F;

            // Subtle breathing animation on body
            this.body.xRot = (float) Math.sin(ageInTicks * 0.05F) * 0.02F;

            // Relaxed tail position with gentle sway
            this.tail.yRot = (float) Math.sin(ageInTicks * 0.02F) * 0.1F;

            // Legs slightly splayed out for basking
            this.legleftfront.zRot = 0.2F;
            this.legrightfront.zRot = -0.2F;
            this.legleftback.zRot = 0.15F;
            this.legrightback.zRot = -0.15F;
        } else {
            this.headbase.yRot = netHeadYaw * ((float)Math.PI / 180F);
            this.headbase.xRot = headPitch * ((float)Math.PI / 180F);

            if (!entity.isInWaterOrBubble()){
                if (!entity.isSprinting()){
                    animateWalk(CROC_WALK, limbSwing, limbSwingAmount, 1.5F, 2.5f);
                } else {
                    animateWalk(CROC_RUN, limbSwing, limbSwingAmount, 1.25F, 2.5f);
                }
            } else {
                animateWalk(CROC_SWIM, limbSwing, limbSwingAmount, 1.0F, 2.5F);
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
