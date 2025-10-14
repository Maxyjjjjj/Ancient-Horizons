package com.fungoussoup.ancienthorizons.entity.client.giraffe;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.GiraffeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GiraffeModel<T extends GiraffeEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "giraffe"), "main");
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart legleftfront;
    private final ModelPart legleftback;
    private final ModelPart legrightfront;
    private final ModelPart legrightback;

    public GiraffeModel(ModelPart root) {
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.legleftfront = root.getChild("legleftfront");
        this.legleftback = root.getChild("legleftback");
        this.legrightfront = root.getChild("legrightfront");
        this.legrightback = root.getChild("legrightback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, -21.0F, 12.0F, 14.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 9.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 37).addBox(-4.5F, -32.0F, -4.0F, 10.0F, 32.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(94, 31).addBox(-4.5F, -20.0F, -4.0F, 10.0F, 32.0F, 7.0F, new CubeDeformation(0.025F))
                .texOffs(34, 37).addBox(-3.5F, -32.5F, -1.0F, 8.0F, 32.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(47, 9).addBox(-2.5F, -29.0F, -11.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-3.5F, -37.025F, 0.0F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offset(-0.5F, -7.0F, -18.0F));

        PartDefinition earleft = neck.addOrReplaceChild("earleft", CubeListBuilder.create().texOffs(65, 5).addBox(0.0F, 0.0F, 0.0F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offset(5.5F, -32.0F, 0.0F));

        PartDefinition earright = neck.addOrReplaceChild("earright", CubeListBuilder.create().texOffs(47, 5).addBox(-7.5F, 0.0F, 0.0F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offset(-4.5F, -32.0F, 0.0F));

        PartDefinition legleftfront = partdefinition.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(46, 74).addBox(-2.025F, 0.0F, -1.0F, 4.0F, 34.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, -9.0F));

        PartDefinition legleftback = partdefinition.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(60, 37).addBox(-2.025F, 0.0F, -1.0F, 4.0F, 34.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, 8.0F));

        PartDefinition legrightfront = partdefinition.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(30, 74).addBox(-2.975F, 0.0F, -1.0F, 4.0F, 34.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -10.0F, -9.0F));

        PartDefinition legrightback = partdefinition.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(72, 71).addBox(-2.975F, 0.0F, -1.0F, 4.0F, 34.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -10.0F, 8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(GiraffeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
        this.legrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;

    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.neck.yRot = headYaw * ((float) Math.PI / 180f);
        this.neck.xRot = headPitch * ((float) Math.PI / 180f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legleftback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightfront.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        legrightback.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
