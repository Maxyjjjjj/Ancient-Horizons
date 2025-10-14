package com.fungoussoup.ancienthorizons.entity.client.anaconda;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaPartEntity;
import com.fungoussoup.ancienthorizons.entity.util.AnacondaPartIndex;
import com.fungoussoup.ancienthorizons.util.Maths;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AnacondaModel<T extends LivingEntity> extends HierarchicalModel<T> {
    // Different layer locations for each part type
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "anaconda"), "main");
    public static final ModelLayerLocation BODY_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "anaconda"), "body");
    public static final ModelLayerLocation HEAD_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "anaconda"), "head");
    public static final ModelLayerLocation TAIL_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "anaconda"), "tail");

    private final ModelPart root;
    private final ModelPart part;

    public AnacondaModel(ModelPart root) {
        this.root = root;
        this.part = root.getChild("part");
    }

    // Create main anaconda body layer (for full anaconda entity)
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Main anaconda body - you can customize this as needed
        PartDefinition basePart = partdefinition.addOrReplaceChild("part", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        basePart.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 33).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    // Create layer for specific part type
    public static LayerDefinition createPartLayer(AnacondaPartIndex index) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition basePart = partdefinition.addOrReplaceChild("part", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        switch (index) {
            case BODY -> {
                basePart.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 33).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));
            }
            case HEAD -> {
                basePart.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(0, 20).addBox(-4.0F, -2.0F, -13.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));
            }
            case TAIL -> {
                basePart.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(28, 46).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));
            }
        }

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float partialTick = ageInTicks - entity.tickCount;

        if(entity instanceof AnacondaEntity anaconda){
            float strangle = anaconda.getStrangleProgress(partialTick);

            // Your existing anaconda animation logic
            progressPositionPrev(part, strangle, 0, 4, 0, 5F);
            progressRotationPrev(part, strangle, Maths.rad(10), 0, 0, 5F);

            this.part.yRot += netHeadYaw * ((float)Math.PI / 180F);
            this.part.xRot += Math.min(0, headPitch * ((float)Math.PI / 180F));
            this.part.x += Mth.sin(limbSwing) * 2.0F * limbSwingAmount;

            this.walk(part, 0.7F, 0.2F, false, 1F, 0.05F, ageInTicks, strangle * 0.2F);

        } else if(entity instanceof AnacondaPartEntity partEntity) {
            float f = 1.01F;
            if(partEntity.getBodyIndex() % 2 == 1){
                f = 1.0F;
            }
            float swell = partEntity.getSwellLerp(partialTick) * 0.15F;

            // Scale the part
            part.xScale = f + swell;
            part.yScale = f + swell;
            part.zScale = f;
        }
    }

    // Helper methods - implement based on your utility classes
    private void progressPositionPrev(ModelPart part, float progress, float x, float y, float z, float speed) {
        part.x += progress * x;
        part.y += progress * y;
        part.z += progress * z;
    }

    private void progressRotationPrev(ModelPart part, float progress, float xRot, float yRot, float zRot, float speed) {
        part.xRot += progress * xRot;
        part.yRot += progress * yRot;
        part.zRot += progress * zRot;
    }

    private void walk(ModelPart part, float speed, float degree, boolean invert, float offset, float weight, float ageInTicks, float intensity) {
        float walkCycle = Mth.sin(ageInTicks * speed + offset) * degree * intensity;
        if (invert) walkCycle = -walkCycle;
        part.yRot += walkCycle * weight;
    }
}