package com.fungoussoup.ancienthorizons.entity.client.lion;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.LionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LionRenderer extends MobRenderer<LionEntity, LionModel<LionEntity>> {
    private static final ResourceLocation MALE_REGULAR = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/lion/lion.png");
    private static final ResourceLocation MALE_WHITE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/lion/lion_white.png");
    private static final ResourceLocation FEMALE_REGULAR = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/lion/lioness.png");
    private static final ResourceLocation FEMALE_WHITE = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "textures/entity/lion/lioness_white.png");

    public LionRenderer(EntityRendererProvider.Context context) {
        super(context, new LionModel<>(context.bakeLayer(LionModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new LionCollarLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LionEntity entity) {
        if (entity.isMale() && !entity.isBaby()) {
            return entity.getVariant().getId() == 1 ? MALE_WHITE : MALE_REGULAR;
        } else {
            return entity.getVariant().getId() == 1 ? FEMALE_WHITE : FEMALE_REGULAR;
        }
    }
}
