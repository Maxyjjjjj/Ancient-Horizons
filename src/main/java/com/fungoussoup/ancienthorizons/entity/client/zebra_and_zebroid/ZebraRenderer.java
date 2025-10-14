package com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.ZebraEntity;
import com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid.ZebraModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZebraRenderer extends AbstractHorseRenderer<ZebraEntity, HorseModel<ZebraEntity>> {

    private static final ResourceLocation ZEBRA_REGULAR = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/zebra/zebra.png");

    private static final ResourceLocation ZEBRA_BLONDE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/zebra/zebra_blonde.png");

    private static final ResourceLocation ZEBRA_POLKADOT = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/zebra/zebra_polkadot.png");

    public ZebraRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ZebraModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new ZebraArmorLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ZebraEntity zebra) {
        return switch (zebra.getVariant()) {
            case ZEBRA_POLKADOT -> ZEBRA_POLKADOT;
            case ZEBRA_BLONDE -> ZEBRA_BLONDE;
            default -> ZEBRA_REGULAR;
        };
    }
}
