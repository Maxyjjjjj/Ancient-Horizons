package com.fungoussoup.ancienthorizons.entity.client.snow_leopard;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.SnowLeopardEntity;
import com.fungoussoup.ancienthorizons.item.ModAnimalArmourItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SnowLeopardArmourLayer extends RenderLayer<SnowLeopardEntity, SnowLeopardModel<SnowLeopardEntity>> {
    private final SnowLeopardModel<SnowLeopardEntity> model;
    private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS;

    public SnowLeopardArmourLayer(RenderLayerParent<SnowLeopardEntity, SnowLeopardModel<SnowLeopardEntity>> renderer, EntityModelSet models) {
        super(renderer);
        this.model = new SnowLeopardModel<>(models.bakeLayer(SnowLeopardModel.ARMOR_LOCATION));
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SnowLeopardEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.hasArmor()) {
            ItemStack itemstack = livingEntity.getBodyArmorItem();
            Item var13 = itemstack.getItem();
            if (var13 instanceof ModAnimalArmourItem animalarmoritem) {
                if (animalarmoritem.getBodyType() == ModAnimalArmourItem.BodyType.UNCIINE) {
                    (this.getParentModel()).copyPropertiesTo(this.model);
                    this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
                    this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(animalarmoritem.getTexture()));
                    this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
                    this.maybeRenderCracks(poseStack, bufferSource, packedLight, itemstack);
                }
            }
        }
    }

    private void maybeRenderCracks(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack armorStack) {
        Crackiness.Level crackiness$level = Crackiness.WOLF_ARMOR.byDamage(armorStack);
        if (crackiness$level != Crackiness.Level.NONE) {
            ResourceLocation resourcelocation = ARMOR_CRACK_LOCATIONS.get(crackiness$level);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

    }

    static {
        ARMOR_CRACK_LOCATIONS = Map.of(Crackiness.Level.LOW, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/snow_leopard/snow_leopard_armor_crackiness_low.png"), Crackiness.Level.MEDIUM, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/snow_leopard/snow_leopard_armor_crackiness_medium.png"), Crackiness.Level.HIGH, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"textures/entity/snow_leopard/snow_leopard_armor_crackiness_high.png"));
    }
}

