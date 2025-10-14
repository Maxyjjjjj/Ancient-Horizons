package com.fungoussoup.ancienthorizons.entity.client.eromangasaurus;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.EromangasaurusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EromangasaurusRenderer extends MobRenderer<EromangasaurusEntity, EromangasaurusModel<EromangasaurusEntity>> {
    public EromangasaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new EromangasaurusModel<>(context.bakeLayer(EromangasaurusModel.LAYER_LOCATION)), 0.875f);
    }

    @Override
    public ResourceLocation getTextureLocation(EromangasaurusEntity eromangasaurusEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/eromangasaurus/eromangasaurus.png");
    }
}