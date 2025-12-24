package com.fungoussoup.ancienthorizons.client.screen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.bestiary.BestiaryEntry;
import com.fungoussoup.ancienthorizons.bestiary.BestiaryManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * The bestiary GUI screen
 */
public class BestiaryScreen extends Screen {
    private static final ResourceLocation BESTIARY_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AncientHorizons.MOD_ID, "textures/gui/bestiary.png");

    private static final int BOOK_WIDTH = 192;
    private static final int BOOK_HEIGHT = 192;

    private final List<BestiaryEntry> entries;
    private int currentPage = 0;

    private Button nextButton;
    private Button prevButton;

    public BestiaryScreen() {
        super(Component.translatable("ancienthorizons.gui.bestiary"));
        this.entries = new ArrayList<>(BestiaryManager.getAllEntries());
    }

    @Override
    protected void init() {
        super.init();

        int bookX = (this.width - BOOK_WIDTH) / 2;
        int bookY = (this.height - BOOK_HEIGHT) / 2;

        // Next button
        this.nextButton = Button.builder(
                        Component.translatable("ancienthorizons.button_text.next"),
                        button -> nextPage())
                .bounds(bookX + BOOK_WIDTH - 50, bookY + BOOK_HEIGHT - 30, 40, 20)
                .build();

        // Previous button
        this.prevButton = Button.builder(
                        Component.translatable("ancienthorizons.button_text.prev"),
                        button -> previousPage())
                .bounds(bookX + 10, bookY + BOOK_HEIGHT - 30, 40, 20)
                .build();

        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(prevButton);

        updateButtons();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int bookX = (this.width - BOOK_WIDTH) / 2;
        int bookY = (this.height - BOOK_HEIGHT) / 2;

        // Draw book background
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(BESTIARY_TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);

        // Render current entry
        if (currentPage >= 0 && currentPage < entries.size()) {
            renderEntry(graphics, entries.get(currentPage), bookX, bookY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderEntry(GuiGraphics graphics, BestiaryEntry entry, int bookX, int bookY) {
        int textX = bookX + 20;
        int textY = bookY + 20;
        int textWidth = BOOK_WIDTH - 40;

        if (!entry.isDiscovered()) {
            // Show "???" for undiscovered entries
            graphics.drawString(this.font, "???", textX, textY, 0x3F3F3F, false);
            graphics.drawWordWrap(this.font,
                    Component.translatable("ancienthorizons.gui.bestiary.not_discovered"),
                    textX, textY + 30, textWidth, 0x3F3F3F);
            return;
        }

        // Name
        graphics.drawString(this.font, entry.getName(), textX, textY, 0x000000, false);
        textY += 12;

        // Scientific name
        if (entry.getScientificName() != null && !entry.getScientificName().isEmpty()) {
            graphics.drawString(this.font, entry.getScientificName(), textX, textY, 0x555555, true);
            textY += 12;
        }

        // Period
        if (entry.getPeriod() != null && !entry.getPeriod().isEmpty()) {
            graphics.drawString(this.font,
                    Component.translatable("ancienthorizons.gui.bestiary.period").append(entry.getPeriod()).getString(),
                    textX, textY, 0x555555, false);
            textY += 15;
        }

        // Description
        if (entry.getDescription() != null && !entry.getDescription().isEmpty()) {
            graphics.drawWordWrap(this.font,
                    Component.literal(entry.getDescription()),
                    textX, textY, textWidth, 0x000000);
        }

        // Page number
        String pageText = (currentPage + 1) + " / " + entries.size();
        int pageX = bookX + (BOOK_WIDTH - this.font.width(pageText)) / 2;
        graphics.drawString(this.font, pageText, pageX, bookY + BOOK_HEIGHT - 15, 0x000000, false);
    }

    private void nextPage() {
        if (currentPage < entries.size() - 1) {
            currentPage++;
            updateButtons();
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateButtons();
        }
    }

    private void updateButtons() {
        this.prevButton.active = currentPage > 0;
        this.nextButton.active = currentPage < entries.size() - 1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}