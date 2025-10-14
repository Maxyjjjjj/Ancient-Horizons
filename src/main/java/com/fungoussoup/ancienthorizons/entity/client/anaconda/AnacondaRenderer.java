package com.fungoussoup.ancienthorizons.entity.client.anaconda;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AnacondaRenderer extends MobRenderer<AnacondaEntity, AnacondaModel<AnacondaEntity>> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/anaconda/anaconda.png");

    public AnacondaRenderer(EntityRendererProvider.Context context) {
        super(context, new AnacondaModel<>(context.bakeLayer(AnacondaModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AnacondaEntity entity) {
        return TEXTURE;
    }
}