package com.fungoussoup.ancienthorizons.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ModAnimalArmourItem extends ArmorItem {
    private final ResourceLocation textureLocation;
    @Nullable
    private final ResourceLocation overlayTextureLocation;
    private final ModAnimalArmourItem.BodyType bodyType;

    public ModAnimalArmourItem(Holder<ArmorMaterial> armorMaterial, ModAnimalArmourItem.BodyType bodyType, boolean hasOverlay, Item.Properties properties) {
        super(armorMaterial, Type.BODY, properties);
        this.bodyType = bodyType;
        ResourceLocation resourcelocation = bodyType.textureLocator.apply(armorMaterial.unwrapKey().orElseThrow().location());
        this.textureLocation = resourcelocation.withSuffix(".png");
        if (hasOverlay) {
            this.overlayTextureLocation = resourcelocation.withSuffix("_overlay.png");
        } else {
            this.overlayTextureLocation = null;
        }

    }

    public ResourceLocation getTexture() {
        return this.textureLocation;
    }

    @Nullable
    public ResourceLocation getOverlayTexture() {
        return this.overlayTextureLocation;
    }

    public ModAnimalArmourItem.BodyType getBodyType() {
        return this.bodyType;
    }

    public SoundEvent getBreakingSound() {
        return this.bodyType.breakingSound;
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public enum BodyType {
        UNCIINE((resourceLocation) -> resourceLocation.withPath("textures/entity/snow_leopard/snow_leopard_armor"), SoundEvents.WOLF_ARMOR_BREAK),
        TIGRINE((resourceLocation) -> resourceLocation.withPath("textures/entity/tiger/tiger_armor"), SoundEvents.WOLF_ARMOR_BREAK),
        STRUTHIONINE((resourceLocation) -> resourceLocation.withPath((str) -> "textures/entity/ostrich/armor/ostrich_armor_" + str), SoundEvents.ITEM_BREAK),
        GALLIMIMINE((resourceLocation) -> resourceLocation.withPath((str) -> "textures/entity/gallimimus/armor/gallimimus_armor_" + str), SoundEvents.ITEM_BREAK),
        AQUILINE((resourceLocation) -> resourceLocation.withPath("textures/entity/eagle/eagle_armor"), SoundEvents.WOLF_ARMOR_BREAK),
        PITHECOPHAGINE((resourceLocation) -> resourceLocation.withPath("textures/philippine_eagle/eagle_armor"), SoundEvents.WOLF_ARMOR_BREAK),
        CAMELINE((resourceLocation) -> resourceLocation.withPath((str) -> "textures/entity/bactrian_camel/armor/bactrian_camel_armor_" + str), SoundEvents.ITEM_BREAK);

        final Function<ResourceLocation, ResourceLocation> textureLocator;
        final SoundEvent breakingSound;

        BodyType(Function<ResourceLocation, ResourceLocation> textureLocator, SoundEvent breakingSound) {
            this.textureLocator = textureLocator;
            this.breakingSound = breakingSound;
        }
    }
}