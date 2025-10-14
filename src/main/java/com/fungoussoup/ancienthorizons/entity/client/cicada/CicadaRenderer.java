package com.fungoussoup.ancienthorizons.entity.client.cicada;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.CicadaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CicadaRenderer extends MobRenderer<CicadaEntity, CicadaModel<CicadaEntity>> {
    public CicadaRenderer(EntityRendererProvider.Context context) {
        super(context, new CicadaModel<>(context.bakeLayer(CicadaModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(CicadaEntity cicadaEntity) {
        return ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/cicada/cicada.png");
    }
}
