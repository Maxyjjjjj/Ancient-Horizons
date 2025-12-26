package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends EntityTypeTagsProvider {
    public ModEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, AncientHorizons.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // POWDER SNOW WALKABLE - Cold-resistant animals
        this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
                .add(ModEntities.TIGER.get()) // Siberian Tiger
                .add(ModEntities.SNOW_LEOPARD.get()) // Mountain dweller
                .add(ModEntities.MONKEY.get()) // Golden Snub-Nosed Monkey
                .add(ModEntities.BACTRIAN_CAMEL.get()) // Cold-desert adapted
                .add(ModEntities.BROWN_BEAR.get()) // Cold climate bear
                .add(ModEntities.STOAT.get()) // Arctic weasel
                .add(ModEntities.FISHER.get()) // Cold forest mustelid
                .add(ModEntities.WOLVERINE.get()) // Arctic/subarctic
                .add(ModEntities.RED_PANDA.get()) // Mountain dweller
                .add(ModEntities.PENGUIN.get()) // Antarctic
                .add(ModEntities.HARE.get()) // similarly to rabbits
                .add(ModEntities.ROE_DEER.get()) // Cold climate deer
                .add(ModEntities.DEER.get()); // Can handle snow

        // FREEZE IMMUNE - Animals that thrive in cold
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                .add(ModEntities.SNOW_LEOPARD.get())
                .add(ModEntities.PENGUIN.get())
                .add(ModEntities.STOAT.get())
                .add(ModEntities.WOLVERINE.get());

        // ARTHROPOD - Insects and similar
        this.tag(EntityTypeTags.ARTHROPOD)
                .add(ModEntities.MANTIS.get())
                .add(ModEntities.EARTHWORM.get())
                .add(ModEntities.CICADA.get());

        // AQUATIC - Water-dwelling animals
        this.tag(EntityTypeTags.AQUATIC)
                .add(ModEntities.BELUGA_STURGEON.get())
                .add(ModEntities.WHITE_SHARK.get())
                .add(ModEntities.CROCODILE.get())
                .add(ModEntities.EROMANGASAURUS.get()); // Plesiosaur

        // FROG FOOD - Small creatures frogs can eat
        this.tag(EntityTypeTags.FROG_FOOD)
                .add(ModEntities.EARTHWORM.get())
                .add(ModEntities.CICADA.get());

        // FALL DAMAGE IMMUNE - Flying/gliding creatures
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(ModEntities.EAGLE.get())
                .add(ModEntities.PHILIPPINE_EAGLE.get())
                .add(ModEntities.SEAGULL.get())
                .add(ModEntities.MERGANSER.get())
                .add(ModEntities.CRYODRAKON.get())
                .add(ModEntities.DEARC.get())
                // All passerines
                .add(ModEntities.BLACKCAP.get())
                .add(ModEntities.BLUETHROAT.get())
                .add(ModEntities.BULLFINCH.get())
                .add(ModEntities.CANARY.get())
                .add(ModEntities.CARDINAL.get())
                .add(ModEntities.CHAFFINCH.get())
                .add(ModEntities.GOLDCREST.get())
                .add(ModEntities.GOLDFINCH.get())
                .add(ModEntities.NIGHTINGALE.get())
                .add(ModEntities.REDSTART.get())
                .add(ModEntities.REEDLING.get())
                .add(ModEntities.ROBIN.get())
                .add(ModEntities.SISKIN.get())
                .add(ModEntities.SKYLARK.get())
                .add(ModEntities.SPARROW.get())
                .add(ModEntities.TIT.get())
                .add(ModEntities.WAGTAIL.get())
                .add(ModEntities.WAXWING.get())
                // Other birds
                .add(ModEntities.PHEASANT.get())
                .add(ModEntities.HOATZIN.get())
                .add(ModEntities.ROADRUNNER.get())
                .add(ModEntities.RUFF.get());

        // CAN BREATHE UNDERWATER - Aquatic animals
        this.tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .add(ModEntities.BELUGA_STURGEON.get())
                .add(ModEntities.WHITE_SHARK.get())
                .add(ModEntities.CROCODILE.get())
                .add(ModEntities.HIPPOPOTAMUS.get());

        // SENSITIVE TO IMPALING - Aquatic creatures vulnerable to trident
        this.tag(EntityTypeTags.SENSITIVE_TO_IMPALING)
                .add(ModEntities.BELUGA_STURGEON.get())
                .add(ModEntities.WHITE_SHARK.get())
                .add(ModEntities.CROCODILE.get())
                .add(ModEntities.EROMANGASAURUS.get());

        // SENSITIVE TO BANE_OF_ARTHROPODS
        this.tag(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)
                .add(ModEntities.MANTIS.get())
                .add(ModEntities.CICADA.get());

        // DEFLECTS_PROJECTILES - Entities that deflect arrows
        this.tag(EntityTypeTags.DEFLECTS_PROJECTILES)
                .add(ModEntities.SAICHANIA.get()) // Armored ankylosaur
                .add(ModEntities.CROCODILE.get()); // Thick scales

        // IMMUNE_TO_OOZING - Entities immune to oozing effect
        this.tag(EntityTypeTags.IMMUNE_TO_OOZING)
                .add(ModEntities.CROCODILE.get())
                .add(ModEntities.PANGOLIN.get()); // Scales protect

        // IMMUNE_TO_INFESTED - Entities immune to silverfish
        this.tag(EntityTypeTags.IMMUNE_TO_INFESTED)
                .add(ModEntities.MANTIS.get())
                .add(ModEntities.CICADA.get())
                .add(ModEntities.EARTHWORM.get()); // Already bugs

        // NOT_SCARY_FOR_PUFFERFISH - Non-threatening to pufferfish
        this.tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH)
                .add(ModEntities.BELUGA_STURGEON.get()); // Another fish

        // NO_ANGER_FROM_WIND_CHARGE - Passive/neutral mobs
        this.tag(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE)
                .add(ModEntities.GIRAFFE.get())
                .add(ModEntities.ZEBRA.get())
                .add(ModEntities.ZORSE.get())
                .add(ModEntities.ZONKEY.get())
                .add(ModEntities.DOMESTIC_GOAT.get())
                .add(ModEntities.WILDEBEEST.get())
                .add(ModEntities.DEER.get())
                .add(ModEntities.ROE_DEER.get())
                .add(ModEntities.SAOLA.get())
                .add(ModEntities.RED_PANDA.get())
                .add(ModEntities.RACCOON.get())
                .add(ModEntities.PANGOLIN.get())
                .add(ModEntities.HARE.get())
                .add(ModEntities.PENGUIN.get())
                .add(ModEntities.GALLIMIMUS.get())
                .add(ModEntities.BEIPIAOSAURUS.get())
                .add(ModEntities.BLACKCAP.get())
                .add(ModEntities.BLUETHROAT.get())
                .add(ModEntities.BULLFINCH.get())
                .add(ModEntities.CANARY.get())
                .add(ModEntities.CARDINAL.get())
                .add(ModEntities.CHAFFINCH.get())
                .add(ModEntities.GOLDCREST.get())
                .add(ModEntities.GOLDFINCH.get())
                .add(ModEntities.NIGHTINGALE.get())
                .add(ModEntities.REDSTART.get())
                .add(ModEntities.REEDLING.get())
                .add(ModEntities.ROBIN.get())
                .add(ModEntities.SISKIN.get())
                .add(ModEntities.SKYLARK.get())
                .add(ModEntities.SPARROW.get())
                .add(ModEntities.TIT.get())
                .add(ModEntities.WAGTAIL.get())
                .add(ModEntities.WAXWING.get())
                .add(ModEntities.PHEASANT.get())
                .add(ModEntities.SEAGULL.get())
                .add(ModEntities.HOATZIN.get())
                .add(ModEntities.ROADRUNNER.get())
                .add(ModEntities.RUFF.get())
                .add(ModEntities.MERGANSER.get());

        tag(ModTags.EntityTypes.TIGER_PREY)
                .add(EntityType.PIG)
                .add(EntityType.COW)
                .add(EntityType.SHEEP)
                .add(EntityType.GOAT)
                .add(ModEntities.DOMESTIC_GOAT.get())
                .add(ModEntities.DEER.get())
                .add(ModEntities.ROE_DEER.get())
                .add(ModEntities.WILDEBEEST.get())
                .add(ModEntities.MONKEY.get())
                .add(EntityType.CREEPER);

        tag(ModTags.EntityTypes.TIGER_ENEMIES)
                .addTag(EntityTypeTags.UNDEAD);

        tag(ModTags.EntityTypes.SNOW_LEOPARD_PREY)
                .add(EntityType.SHEEP)
                .add(EntityType.GOAT)
                .add(ModEntities.DOMESTIC_GOAT.get())
                .add(ModEntities.DEER.get())
                .add(ModEntities.ROE_DEER.get())
                .add(ModEntities.HARE.get());

        tag(ModTags.EntityTypes.WOLF_PREY)
                .add(EntityType.SHEEP)
                .add(EntityType.RABBIT)
                .add(ModEntities.HARE.get())
                .add(ModEntities.DEER.get())
                .add(ModEntities.ROE_DEER.get());

        tag(ModTags.EntityTypes.TIGER_ENEMIES)
                .add(ModEntities.LION.get())
                .add(ModEntities.BROWN_BEAR.get())
                .add(EntityType.POLAR_BEAR);

        tag(ModTags.EntityTypes.MANTIS_PREY)
                .add(EntityType.BEE)
                .add(EntityType.SILVERFISH)
                .add(ModEntities.EARTHWORM.get())
                .add(ModEntities.CICADA.get());

        tag(ModTags.EntityTypes.EAGLE_PREY)
                .add(EntityType.RABBIT)
                .add(EntityType.CHICKEN)
                .add(EntityType.FOX)
                .add(EntityType.WOLF)
                .add(ModEntities.HARE.get())
                .add(ModEntities.RACCOON.get())
                .add(ModEntities.STOAT.get());

        tag(ModTags.EntityTypes.POLAR_BEAR_PREY)
                .add(EntityType.COD)
                .add(EntityType.SALMON)
                .add(EntityType.WOLF)
                .add(ModEntities.PENGUIN.get())
                .add(ModEntities.BELUGA_STURGEON.get());

        tag(ModTags.EntityTypes.FOX_PREY)
                .add(EntityType.CHICKEN)
                .add(EntityType.RABBIT)
                .add(EntityType.COD)
                .add(EntityType.SALMON)
                .add(ModEntities.HARE.get());

        tag(ModTags.EntityTypes.STOAT_PREY)
                .add(EntityType.RABBIT)
                .add(EntityType.CHICKEN)
                .add(ModEntities.HARE.get())
                .add(ModEntities.EARTHWORM.get());

        tag(ModTags.EntityTypes.AZHDARCHID_PREY)
                .add(EntityType.CHICKEN)
                .add(EntityType.RABBIT)
                .add(EntityType.COD)
                .add(EntityType.SALMON)
                .add(ModEntities.HARE.get())
                .add(ModEntities.PHEASANT.get());

        tag(ModTags.EntityTypes.FISHER_PREY)
                .add(EntityType.RABBIT)
                .add(EntityType.CHICKEN)
                .add(ModEntities.HARE.get())
                .add(ModEntities.RACCOON.get())
                .add(ModEntities.STOAT.get());

        tag(ModTags.EntityTypes.HYPNOVENATOR_PREY)
                .add(EntityType.RABBIT)
                .add(EntityType.CHICKEN)
                .add(ModEntities.HARE.get())
                .add(ModEntities.PHEASANT.get());

        tag(ModTags.EntityTypes.CROCODILE_LAND_PREY)
                .add(EntityType.PIG)
                .add(EntityType.COW)
                .add(EntityType.SHEEP)
                .add(EntityType.HORSE)
                .add(EntityType.DONKEY)
                .add(ModEntities.DOMESTIC_GOAT.get())
                .add(ModEntities.ZEBRA.get())
                .add(ModEntities.DEER.get())
                .add(ModEntities.WILDEBEEST.get());

        tag(ModTags.EntityTypes.PHILIPPINE_EAGLE_PREY)
                .add(EntityType.RABBIT)
                .add(EntityType.BAT)
                .add(ModEntities.HARE.get())
                .add(ModEntities.MONKEY.get());

        tag(ModTags.EntityTypes.MAIP_PREY)
                .add(ModEntities.GALLIMIMUS.get())
                .add(ModEntities.HYPNOVENATOR.get())
                .add(ModEntities.BEIPIAOSAURUS.get())
                .add(ModEntities.VELOCIRAPTOR.get());

        tag(ModTags.EntityTypes.MAIP_ENEMIES)
                .add(EntityType.RAVAGER);

        // PASSERINES TAG (all small birds)
        tag(ModTags.EntityTypes.PASSERINES)
                .add(ModEntities.BLACKCAP.get())
                .add(ModEntities.BLUETHROAT.get())
                .add(ModEntities.BULLFINCH.get())
                .add(ModEntities.CANARY.get())
                .add(ModEntities.CARDINAL.get())
                .add(ModEntities.CHAFFINCH.get())
                .add(ModEntities.GOLDCREST.get())
                .add(ModEntities.GOLDFINCH.get())
                .add(ModEntities.NIGHTINGALE.get())
                .add(ModEntities.REDSTART.get())
                .add(ModEntities.REEDLING.get())
                .add(ModEntities.ROBIN.get())
                .add(ModEntities.SISKIN.get())
                .add(ModEntities.SKYLARK.get())
                .add(ModEntities.SPARROW.get())
                .add(ModEntities.TIT.get())
                .add(ModEntities.WAGTAIL.get())
                .add(ModEntities.WAXWING.get());

        // FOSSIL ANIMALS TAG (extinct creatures)
        tag(ModTags.EntityTypes.FOSSIL_ANIMALS)
                .add(EntityType.SNIFFER)
                .add(ModEntities.CRYODRAKON.get())
                .add(ModEntities.VELOCIRAPTOR.get())
                .add(ModEntities.HYPNOVENATOR.get())
                .add(ModEntities.GALLIMIMUS.get())
                .add(ModEntities.DIPLODOCUS.get())
                .add(ModEntities.EROMANGASAURUS.get())
                .add(ModEntities.BEIPIAOSAURUS.get())
                .add(ModEntities.DEARC.get())
                .add(ModEntities.LATENIVENATRIX.get())
                .add(ModEntities.MAIP.get())
                .add(ModEntities.SAICHANIA.get());

        // CARNIVORES
        tag(ModTags.EntityTypes.CARNIVORES)
                .add(EntityType.WOLF)
                .add(EntityType.FOX)
                .add(EntityType.POLAR_BEAR)
                .add(EntityType.DOLPHIN)
                .add(EntityType.FROG)
                .add(EntityType.CAT)
                .add(EntityType.OCELOT)
                .add(ModEntities.TIGER.get())
                .add(ModEntities.SNOW_LEOPARD.get())
                .add(ModEntities.LION.get())
                .add(ModEntities.STOAT.get())
                .add(ModEntities.FISHER.get())
                .add(ModEntities.WOLVERINE.get())
                .add(ModEntities.EAGLE.get())
                .add(ModEntities.PHILIPPINE_EAGLE.get())
                .add(ModEntities.WHITE_SHARK.get())
                .add(ModEntities.CROCODILE.get())
                .add(ModEntities.MANTIS.get())
                .add(ModEntities.VELOCIRAPTOR.get())
                .add(ModEntities.HYPNOVENATOR.get())
                .add(ModEntities.LATENIVENATRIX.get())
                .add(ModEntities.MAIP.get())
                .add(ModEntities.CRYODRAKON.get())
                .add(ModEntities.DEARC.get())
                .add(ModEntities.EROMANGASAURUS.get())
                .add(ModEntities.MERGANSER.get())
                .add(ModEntities.BELUGA_STURGEON.get())
                .add(ModEntities.PENGUIN.get());

    }
}