package com.fungoussoup.ancienthorizons.gui;

import com.fungoussoup.ancienthorizons.entity.custom.mob.GallimimusEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GallimimusStaminaOverlay {

    private static final ResourceLocation STAMINA_BAR_TEXTURE_BG =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/jump_bap_background.png");
    private static final ResourceLocation STAMINA_BAR_TEXTURE_FILL =
            ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/jump_bap_filled.png");

    public static void renderStaminaBar(GuiGraphics guiGraphics, Minecraft mc) {
        assert mc.player != null;
        if (!(mc.player.getVehicle() instanceof GallimimusEntity gallimimus)) return;

        float staminaPercent = gallimimus.getStaminaPercent();

        int x = mc.getWindow().getGuiScaledWidth() / 2 - 91; // same placement as jump bar
        int y = mc.getWindow().getGuiScaledHeight() - 32;    // above hotbar

        // bar width (vanilla horse jump bar is 182 px wide)
        int fullWidth = 182;
        int filled = (int) (staminaPercent * fullWidth);

        // draw background (empty bar)
        guiGraphics.blit(STAMINA_BAR_TEXTURE_BG, x, y, 0, 0, fullWidth, 5);

        // draw filled part
        if (filled > 0) {
            guiGraphics.blit(STAMINA_BAR_TEXTURE_FILL, x, y, 0, 5, filled, 5);
        }
    }
}

