package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ZorseEntity;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ZorseModel extends HorseModel<ZorseEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "zorse"), "main");

    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public ZorseModel(ModelPart root) {
        super(root);
        ModelPart modelpart = this.body.getChild("saddle");
        ModelPart modelpart1 = this.headParts.getChild("left_saddle_mouth");
        ModelPart modelpart2 = this.headParts.getChild("right_saddle_mouth");
        ModelPart modelpart3 = this.headParts.getChild("left_saddle_line");
        ModelPart modelpart4 = this.headParts.getChild("right_saddle_line");
        ModelPart modelpart5 = this.headParts.getChild("head_saddle");
        ModelPart modelpart6 = this.headParts.getChild("mouth_saddle_wrap");
        this.saddleParts = new ModelPart[]{modelpart, modelpart1, modelpart2, modelpart5, modelpart6};
        this.ridingParts = new ModelPart[]{modelpart3, modelpart4};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HorseModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.getChild("body");

        CubeListBuilder cubelistbuilder = CubeListBuilder.create()
                .texOffs(26, 21)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);

        partdefinition1.addOrReplaceChild("left_chest", cubelistbuilder,
                PartPose.offsetAndRotation(6.0F, -8.0F, 0.0F, 0.0F, (-(float)Math.PI / 2F), 0.0F));

        partdefinition1.addOrReplaceChild("right_chest", cubelistbuilder,
                PartPose.offsetAndRotation(-6.0F, -8.0F, 0.0F, 0.0F, ((float)Math.PI / 2F), 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ZorseEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void toggleInvisibleParts(ZorseEntity entity) {
        boolean flag = entity.isSaddled();
        boolean flag1 = entity.isVehicle();

        for(ModelPart modelpart : this.saddleParts) {
            modelpart.visible = flag;
        }

        for(ModelPart modelpart1 : this.ridingParts) {
            modelpart1.visible = flag1 && flag;
        }
    }
}
