package com.fungoussoup.ancienthorizons.entity.client.wolverine;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.WolverineEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WolverineRenderer extends MobRenderer<WolverineEntity, WolverineModel<WolverineEntity>> {
    private static final ResourceLocation WOLVERINE_REGULAR = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/wolverine/wolverine.png");

    private static final ResourceLocation WOLVERINE_REGULAR_ANGRY = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/wolverine/wolverine_angry.png");

    private static final ResourceLocation WOLVERINE_LOGAN = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/wolverine/wolverine_logan.png");

    private static final ResourceLocation WOLVERINE_LOGAN_ANGRY = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/entity/wolverine/wolverine_logan_angry.png");

    public WolverineRenderer(EntityRendererProvider.Context context) {
        super(context, new WolverineModel<>(context.bakeLayer(WolverineModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(WolverineEntity entity) {
        String name = ChatFormatting.stripFormatting(entity.getName().getString());
        if ("Logan".equals(name)) {
            return entity.isAggressive() ? WOLVERINE_LOGAN_ANGRY : WOLVERINE_LOGAN;
        } else {
            return entity.isAggressive() ? WOLVERINE_REGULAR_ANGRY : WOLVERINE_REGULAR;
        }
    }
}
