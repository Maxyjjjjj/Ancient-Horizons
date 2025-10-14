package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ZebraEntity;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.EntityModelSet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.Items;

public class ZebraArmorLayer extends RenderLayer<ZebraEntity, HorseModel<ZebraEntity>> {

    private final HorseModel<ZebraEntity> armorModel;
    private static final ResourceLocation LEATHER_ARMOR_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/armor/horse_armor_leather.png");
    private static final ResourceLocation IRON_ARMOR_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/armor/horse_armor_iron.png");
    private static final ResourceLocation GOLDEN_ARMOR_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/armor/horse_armor_golden.png");
    private static final ResourceLocation DIAMOND_ARMOR_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/armor/horse_armor_diamond.png");

    public ZebraArmorLayer(RenderLayerParent<ZebraEntity, HorseModel<ZebraEntity>> parent, EntityModelSet modelSet) {
        super(parent);
        this.armorModel = new HorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    @Override
    protected ResourceLocation getTextureLocation(ZebraEntity entity) {
        if (entity.isBodyArmorItem(Items.LEATHER_HORSE_ARMOR.getDefaultInstance())){
            return LEATHER_ARMOR_TEXTURE;
        } else if (entity.isBodyArmorItem(Items.IRON_HORSE_ARMOR.getDefaultInstance())){
            return IRON_ARMOR_TEXTURE;
        } else if (entity.isBodyArmorItem(Items.GOLDEN_HORSE_ARMOR.getDefaultInstance())){
            return GOLDEN_ARMOR_TEXTURE;
        } else if (entity.isBodyArmorItem(Items.DIAMOND_HORSE_ARMOR.getDefaultInstance())){
            return DIAMOND_ARMOR_TEXTURE;
        } else {
            return null;
        }
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            ZebraEntity zebra,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (zebra.isWearingBodyArmor()) {
            this.getParentModel().copyPropertiesTo(this.armorModel);
            this.armorModel.prepareMobModel(zebra, limbSwing, limbSwingAmount, partialTicks);
            this.armorModel.setupAnim(zebra, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            render(poseStack, bufferSource, packedLight, zebra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}