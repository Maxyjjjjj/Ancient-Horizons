package com.fungoussoup.ancienthorizons.entity.client.earthworm;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EarthwormEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import static com.fungoussoup.ancienthorizons.entity.client.earthworm.EarthwormAnimations.EARTHWORM_CRAWL;

public class EarthwormModel<T extends EarthwormEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "earthworm"), "main");
    private final ModelPart body;
    private float ageInTicks = 0.0F;
    private AnimationDefinition currentAnimation;

    public EarthwormModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -7.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return body;
    }

    @Override
    public void setupAnim(T entity, float v, float v1, float ageInTicks, float v3, float v4) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        applyWalkingAnimation(ageInTicks);
    }

    private void applyWalkingAnimation(float ageInTicks) {
        this.ageInTicks = ageInTicks;
        if (this.currentAnimation == null || !this.currentAnimation.equals(EARTHWORM_CRAWL)) {
            this.currentAnimation = EARTHWORM_CRAWL;
        }
    }
}
