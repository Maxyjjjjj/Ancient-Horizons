package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ZonkeyEntity;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

public class ZonkeyModel extends HorseModel<ZonkeyEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "zonkey"), "main");

    private final ModelPart[] saddleParts;
    private final ModelPart[] ridingParts;

    public ZonkeyModel(ModelPart root) {
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
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ZonkeyEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

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
