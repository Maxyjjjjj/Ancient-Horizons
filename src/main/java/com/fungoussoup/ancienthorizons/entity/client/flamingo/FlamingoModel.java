package com.fungoussoup.ancienthorizons.entity.client.flamingo;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.FlamingoEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FlamingoModel<T extends FlamingoEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "flamingo"), "main");

    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wingleft;
    private final ModelPart wingright;
    private final ModelPart legleft;
    private final ModelPart legright;

    public FlamingoModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.head = this.body.getChild("head");
        this.wingleft = this.base.getChild("wingleft");
        this.wingright = this.base.getChild("wingright");
        this.legleft = this.base.getChild("legleft");
        this.legright = this.base.getChild("legright");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0, 0, 0));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -4.5F, 6.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 7).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(21, 0).addBox(-1.5F, -12.0F, -3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(26, 23).addBox(-0.5F, -11.0F, -7.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -9.0F, -7.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.5F, 0.2182F, 0.0F, 0.0F));

        PartDefinition wingleft = base.addOrReplaceChild("wingleft", CubeListBuilder.create().texOffs(11, 20).addBox(0.0F, 0.0F, -1.0F, 1.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 8.0F, -3.0F));

        PartDefinition wingright = base.addOrReplaceChild("wingright", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 8.0F, -3.0F));

        PartDefinition legleft = base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 14.0F, 0.0F));

        PartDefinition legright = base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(22, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 14.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(FlamingoEntity flamingo, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);

        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleft.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legright.xRot = Mth.cos(limbSwing * walkSpeed + (float)Math.PI) * walkDegree * limbSwingAmount;
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
