package com.fungoussoup.ancienthorizons.entity.client.domestic_goat;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.DomesticGoatEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DomesticGoatModel<T extends DomesticGoatEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "domestic_goat"), "main");
    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart tail;
    private final ModelPart legleftfront;
    private final ModelPart legrightfront;
    private final ModelPart legleftback;
    private final ModelPart legrightback;

    public DomesticGoatModel(ModelPart root) {
        this.base = root.getChild("base");
        this.body = this.base.getChild("body");
        this.neck = this.body.getChild("neck");
        this.tail = this.body.getChild("tail");
        this.legleftfront = this.base.getChild("legleftfront");
        this.legrightfront = this.base.getChild("legrightfront");
        this.legleftback = this.base.getChild("legleftback");
        this.legrightback = this.base.getChild("legrightback");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.0F, -6.0F, 7.0F, 6.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(18, 19).addBox(-1.0F, 3.0F, 2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 19).addBox(-2.5F, -9.0F, -2.0F, 5.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(27, 7).addBox(-1.5F, -7.0F, -5.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 36).addBox(0.0F, -4.0F, -5.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 23).addBox(2.5F, -9.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 34).addBox(-5.5F, -9.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, -5.0F));

        PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(34, 31).addBox(-1.5F, -10.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 26).addBox(0.5F, -10.0F, -4.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -0.48F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 34).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 7.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition legleftfront = base.addOrReplaceChild("legleftfront", CubeListBuilder.create().texOffs(18, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, -4.5F));

        PartDefinition legrightfront = base.addOrReplaceChild("legrightfront", CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 16.0F, -4.5F));

        PartDefinition legleftback = base.addOrReplaceChild("legleftback", CubeListBuilder.create().texOffs(0, 33).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 16.0F, 5.5F));

        PartDefinition legrightback = base.addOrReplaceChild("legrightback", CubeListBuilder.create().texOffs(8, 33).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 16.0F, 5.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return base;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        // Vanilla-style walking animation
        float walkSpeed = 1.0F;
        float walkDegree = 1.0F;
        this.legleftfront.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legrightfront.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;
        this.legrightback.xRot = Mth.cos(limbSwing * walkSpeed) * walkDegree * limbSwingAmount;
        this.legleftback.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * walkDegree * limbSwingAmount;

        float partialTick = 0;

        if (entity.getEatAnimationTick() > 0){
            this.neck.y = 6.0F + entity.getHeadEatPositionScale(partialTick) * 9.0F;
            this.neck.xRot = entity.getHeadEatAngleScale(partialTick);
        }
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.neck.yRot = headYaw * ((float) Math.PI / 180f);
        this.neck.xRot = headPitch * ((float) Math.PI / 180f);
    }


}
