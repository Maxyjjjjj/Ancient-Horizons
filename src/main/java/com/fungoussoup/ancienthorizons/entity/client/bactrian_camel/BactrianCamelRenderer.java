package com.fungoussoup.ancienthorizons.entity.client.bactrian_camel;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.BactrianCamel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BactrianCamelRenderer extends MobRenderer<BactrianCamel, BactrianCamelModel<BactrianCamel>> {
    private static final ResourceLocation CAMEL_LOCATION = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID,
            "textures/entity/bactrian_camel/bactrian_camel.png"
    );

    public BactrianCamelRenderer(EntityRendererProvider.Context context) {
        super(context, new BactrianCamelModel<>(context.bakeLayer(BactrianCamelModel.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(BactrianCamel entity) {
        return CAMEL_LOCATION;
    }
}