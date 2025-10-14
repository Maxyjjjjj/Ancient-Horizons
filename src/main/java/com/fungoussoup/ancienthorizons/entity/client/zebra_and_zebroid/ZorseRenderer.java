package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.entity.custom.mob.ZorseEntity;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZorseRenderer extends AbstractHorseRenderer<ZorseEntity, ZorseModel> {

    private static final ResourceLocation ZORSE = ResourceLocation.fromNamespaceAndPath("ancienthorizons", "textures/entity/zorse/zorse.png");

    public ZorseRenderer(EntityRendererProvider.Context context) {
        super(context, new ZorseModel(context.bakeLayer(ZorseModel.LAYER_LOCATION)), 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ZorseEntity zorse) {
        return ZORSE;
    }
}
