package com.fungoussoup.ancienthorizons.entity.client.sauropod;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.sauropoda.DiplodocusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DiplodocusRenderer extends MobRenderer<DiplodocusEntity, DiplodocusModel<DiplodocusEntity>> {
    public DiplodocusRenderer(EntityRendererProvider.Context context) {
        super(context, new DiplodocusModel<>(context.bakeLayer(DiplodocusModel.LAYER_LOCATION)), 1.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(DiplodocusEntity diplodocusEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/diplodocus/diplodocus.png");
    }
}
