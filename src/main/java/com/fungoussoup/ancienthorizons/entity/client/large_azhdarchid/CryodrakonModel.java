package com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.azhdarchidae.CryodrakonEntity;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class CryodrakonModel<T extends CryodrakonEntity> extends AbstractLargeAzhdarchidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "cryodrakon"), "main");

    private AnimationDefinition currentAnimation;
    private float ageInTicks = 0.0F;

    public CryodrakonModel(ModelPart root) {
        super(root);
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
                .texOffs(100, 72).addBox(-1.0F, -8.0F, -5.0F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.025F))
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
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }
}