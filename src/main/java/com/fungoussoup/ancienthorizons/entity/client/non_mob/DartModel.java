package com.fungoussoup.ancienthorizons.entity.client.non_mob;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.TranquilizerDartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DartModel<T extends TranquilizerDartEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "dart"), "main");

    private final ModelPart dart;

    public DartModel(ModelPart root) {
        this.dart = root.getChild("dart");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition dart = partdefinition.addOrReplaceChild("dart", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(4, 10).addBox(0.0F, -0.5F, -4.5F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = dart.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0.5F, -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, 0.3491F, 0.0F));

        PartDefinition cube_r2 = dart.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(8, 6).addBox(-0.5F, -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.0F, -0.3491F, 0.0F));

        PartDefinition cube_r3 = dart.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, 0.5F, 0.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r4 = dart.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 6).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.3491F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getInBlockState().isAir()) {
            dart.zRot = ageInTicks * 0.5F; // Adjust speed as needed
        } else {
            dart.zRot = 0.0F; // Stop spinning when embedded
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        dart.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return dart;
    }
}
