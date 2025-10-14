package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.entity.custom.mob.ZonkeyEntity;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZonkeyRenderer extends AbstractHorseRenderer<ZonkeyEntity, HorseModel<ZonkeyEntity>> {

    private static final ResourceLocation ZONKEY = ResourceLocation.fromNamespaceAndPath("ancienthorizons", "textures/entity/zonkey/zonkey.png");

    public ZonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new ZonkeyModel(context.bakeLayer(ZonkeyModel.LAYER_LOCATION)), 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ZonkeyEntity zonkey) {
        return ZONKEY;
    }
}

