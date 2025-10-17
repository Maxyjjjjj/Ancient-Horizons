package com.fungoussoup.ancienthorizons.entity.client.monkey;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.MonkeyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MonkeyRenderer extends MobRenderer<MonkeyEntity, MonkeyModel<MonkeyEntity>> {
    public MonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new MonkeyModel<>(context.bakeLayer(MonkeyModel.LAYER_LOCATION)), 0.4325F);
    }

    @Override
    public ResourceLocation getTextureLocation(MonkeyEntity monkeyEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/monkey.monkey.json");
    }
}
