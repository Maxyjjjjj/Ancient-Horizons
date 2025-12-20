package com.fungoussoup.ancienthorizons;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.client.abstract_passerine.*;
import com.fungoussoup.ancienthorizons.entity.client.bactrian_camel.BactrianCamelRenderer;
import com.fungoussoup.ancienthorizons.entity.client.beipiaosaurus.BeipiaosaurusRenderer;
import com.fungoussoup.ancienthorizons.entity.client.beluga_sturgeon.BelugaSturgeonRenderer;
import com.fungoussoup.ancienthorizons.entity.client.brown_bear.BrownBearRenderer;
import com.fungoussoup.ancienthorizons.entity.client.carcharodon.WhiteSharkRenderer;
import com.fungoussoup.ancienthorizons.entity.client.chimp.ChimpanzeeRenderer;
import com.fungoussoup.ancienthorizons.entity.client.cicada.CicadaRenderer;
import com.fungoussoup.ancienthorizons.entity.client.croc.CrocodileRenderer;
import com.fungoussoup.ancienthorizons.entity.client.dearc.DearcRenderer;
import com.fungoussoup.ancienthorizons.entity.client.deer.DeerRenderer;
import com.fungoussoup.ancienthorizons.entity.client.domestic_goat.DomesticGoatRenderer;
import com.fungoussoup.ancienthorizons.entity.client.eagle.EagleRenderer;
import com.fungoussoup.ancienthorizons.entity.client.earthworm.EarthwormRenderer;
import com.fungoussoup.ancienthorizons.entity.client.elephant.ElephantRenderer;
import com.fungoussoup.ancienthorizons.entity.client.eromangasaurus.EromangasaurusRenderer;
import com.fungoussoup.ancienthorizons.entity.client.fisher.FisherRenderer;
import com.fungoussoup.ancienthorizons.entity.client.flamingo.FlamingoRenderer;
import com.fungoussoup.ancienthorizons.entity.client.gallimimus.GallimimusRenderer;
import com.fungoussoup.ancienthorizons.entity.client.giraffe.GiraffeRenderer;
import com.fungoussoup.ancienthorizons.entity.client.hare.HareRenderer;
import com.fungoussoup.ancienthorizons.entity.client.hippo.HippopotamusRenderer;
import com.fungoussoup.ancienthorizons.entity.client.hoatzin.HoatzinRenderer;
import com.fungoussoup.ancienthorizons.entity.client.hypnovenator.HypnovenatorRenderer;
import com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid.CryodrakonRenderer;
import com.fungoussoup.ancienthorizons.entity.client.lion.LionRenderer;
import com.fungoussoup.ancienthorizons.entity.client.mantis.MantisRenderer;
import com.fungoussoup.ancienthorizons.entity.client.merganser.MerganserRenderer;
import com.fungoussoup.ancienthorizons.entity.client.monkey.MonkeyRenderer;
import com.fungoussoup.ancienthorizons.entity.client.non_mob.DartRenderer;
import com.fungoussoup.ancienthorizons.entity.client.pangolin.PangolinRenderer;
import com.fungoussoup.ancienthorizons.entity.client.penguin.PenguinRenderer;
import com.fungoussoup.ancienthorizons.entity.client.pheasant.PheasantRenderer;
import com.fungoussoup.ancienthorizons.entity.client.philippine_eagle.PhilippineEagleRenderer;
import com.fungoussoup.ancienthorizons.entity.client.raccoon.RaccoonRenderer;
import com.fungoussoup.ancienthorizons.entity.client.roadrunner.RoadrunnerRenderer;
import com.fungoussoup.ancienthorizons.entity.client.roe_deer.RoeDeerRenderer;
import com.fungoussoup.ancienthorizons.entity.client.ruff.RuffRenderer;
import com.fungoussoup.ancienthorizons.entity.client.saola.SaolaRenderer;
import com.fungoussoup.ancienthorizons.entity.client.sauropod.DiplodocusRenderer;
import com.fungoussoup.ancienthorizons.entity.client.seagull.SeagullRenderer;
import com.fungoussoup.ancienthorizons.entity.client.stoat.StoatRenderer;
import com.fungoussoup.ancienthorizons.entity.client.tiger.TigerRenderer;
import com.fungoussoup.ancienthorizons.entity.client.snow_leopard.SnowLeopardRenderer;
import com.fungoussoup.ancienthorizons.entity.client.velociraptor.VelociraptorRenderer;
import com.fungoussoup.ancienthorizons.entity.client.wolverine.WolverineRenderer;
import com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid.ZebraRenderer;
import com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid.ZonkeyRenderer;
import com.fungoussoup.ancienthorizons.entity.client.zebra_and_zebroid.ZorseRenderer;
import com.fungoussoup.ancienthorizons.registry.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.slf4j.Logger;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.LoggerFactory;


@Mod(AncientHorizons.MOD_ID)
public class AncientHorizons {

    public static final String MOD_ID = "ancienthorizons";
    public static final String NAME = "Ancient Horizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
    
    public AncientHorizons(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModTrunkPlacerTypes.TRUNK_PLACERS.register(modEventBus);
        ModFoliagePlacerTypes.FOLIAGE_PLACERS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModAdvancements.TRIGGERS.register(modEventBus);

        ModSoundEvents.init();

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModStrippables.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @EventBusSubscriber(modid = AncientHorizons.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.TIGER.get(), TigerRenderer::new);
            EntityRenderers.register(ModEntities.SNOW_LEOPARD.get(), SnowLeopardRenderer::new);
            EntityRenderers.register(ModEntities.ELEPHANT.get(), ElephantRenderer::new);
            EntityRenderers.register(ModEntities.ZEBRA.get(), ZebraRenderer::new);
            EntityRenderers.register(ModEntities.ZORSE.get(), ZorseRenderer::new);
            EntityRenderers.register(ModEntities.ZONKEY.get(), ZonkeyRenderer::new);
            EntityRenderers.register(ModEntities.DOMESTIC_GOAT.get(), DomesticGoatRenderer::new);
            EntityRenderers.register(ModEntities.BLACKCAP.get(), BlackcapRenderer::new);
            EntityRenderers.register(ModEntities.BLUETHROAT.get(), BluethroatRenderer::new);
            EntityRenderers.register(ModEntities.BULLFINCH.get(), BullfinchRenderer::new);
            EntityRenderers.register(ModEntities.CANARY.get(), CanaryRenderer::new);
            EntityRenderers.register(ModEntities.CARDINAL.get(), CardinalRenderer::new);
            EntityRenderers.register(ModEntities.CHAFFINCH.get(), ChaffinchRenderer::new);
            EntityRenderers.register(ModEntities.GOLDCREST.get(), GoldcrestRenderer::new);
            EntityRenderers.register(ModEntities.GOLDFINCH.get(), GoldfinchRenderer::new);
            EntityRenderers.register(ModEntities.NIGHTINGALE.get(), NightingaleRenderer::new);
            EntityRenderers.register(ModEntities.REDSTART.get(), RedstartRenderer::new);
            EntityRenderers.register(ModEntities.REEDLING.get(), ReedlingRenderer::new);
            EntityRenderers.register(ModEntities.ROBIN.get(), RobinRenderer::new);
            EntityRenderers.register(ModEntities.SISKIN.get(), SiskinRenderer::new);
            EntityRenderers.register(ModEntities.SKYLARK.get(), SkylarkRenderer::new);
            EntityRenderers.register(ModEntities.SPARROW.get(), SparrowRenderer::new);
            EntityRenderers.register(ModEntities.TIT.get(), TitRenderer::new);
            EntityRenderers.register(ModEntities.WAGTAIL.get(), WagtailRenderer::new);
            EntityRenderers.register(ModEntities.WAXWING.get(), WaxwingRenderer::new);
            EntityRenderers.register(ModEntities.GIRAFFE.get(), GiraffeRenderer::new);
            EntityRenderers.register(ModEntities.PANGOLIN.get(), PangolinRenderer::new);
            EntityRenderers.register(ModEntities.SEAGULL.get(), SeagullRenderer::new);
            EntityRenderers.register(ModEntities.EARTHWORM.get(), EarthwormRenderer::new);
            EntityRenderers.register(ModEntities.PENGUIN.get(), PenguinRenderer::new);
            EntityRenderers.register(ModEntities.BROWN_BEAR.get(), BrownBearRenderer::new);
            EntityRenderers.register(ModEntities.MANTIS.get(), MantisRenderer::new);
            EntityRenderers.register(ModEntities.EAGLE.get(), EagleRenderer::new);
            EntityRenderers.register(ModEntities.RACCOON.get(), RaccoonRenderer::new);
            EntityRenderers.register(ModEntities.BACTRIAN_CAMEL.get(), BactrianCamelRenderer::new);
            EntityRenderers.register(ModEntities.BELUGA_STURGEON.get(), BelugaSturgeonRenderer::new);
            EntityRenderers.register(ModEntities.TRANQ_DART.get(), DartRenderer::new);
            EntityRenderers.register(ModEntities.STOAT.get(), StoatRenderer::new);
            EntityRenderers.register(ModEntities.PHEASANT.get(), PheasantRenderer::new);
            EntityRenderers.register(ModEntities.CHIMPANZEE.get(), ChimpanzeeRenderer::new);
            EntityRenderers.register(ModEntities.SAOLA.get(), SaolaRenderer::new);
            EntityRenderers.register(ModEntities.CRYODRAKON.get(), CryodrakonRenderer::new);
            EntityRenderers.register(ModEntities.FLAMINGO.get(), FlamingoRenderer::new);
            EntityRenderers.register(ModEntities.FISHER.get(), FisherRenderer::new);
            EntityRenderers.register(ModEntities.ROADRUNNER.get(), RoadrunnerRenderer::new);
            EntityRenderers.register(ModEntities.CICADA.get(), CicadaRenderer::new);
            EntityRenderers.register(ModEntities.HARE.get(), HareRenderer::new);
            EntityRenderers.register(ModEntities.RUFF.get(), RuffRenderer::new);
            EntityRenderers.register(ModEntities.HYPNOVENATOR.get(), HypnovenatorRenderer::new);
            EntityRenderers.register(ModEntities.VELOCIRAPTOR.get(), VelociraptorRenderer::new);
            EntityRenderers.register(ModEntities.DIPLODOCUS.get(), DiplodocusRenderer::new);
            EntityRenderers.register(ModEntities.DEER.get(), DeerRenderer::new);
            EntityRenderers.register(ModEntities.ROE_DEER.get(), RoeDeerRenderer::new);
            EntityRenderers.register(ModEntities.HOATZIN.get(), HoatzinRenderer::new);
            EntityRenderers.register(ModEntities.GALLIMIMUS.get(), GallimimusRenderer::new);
            EntityRenderers.register(ModEntities.CHAKRAM.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.CROCODILE.get(), CrocodileRenderer::new);
            EntityRenderers.register(ModEntities.HIPPOPOTAMUS.get(), HippopotamusRenderer::new);
            EntityRenderers.register(ModEntities.EROMANGASAURUS.get(), EromangasaurusRenderer::new);
            EntityRenderers.register(ModEntities.BEIPIAOSAURUS.get(), BeipiaosaurusRenderer::new);
            EntityRenderers.register(ModEntities.WHITE_SHARK.get(), WhiteSharkRenderer::new);
            EntityRenderers.register(ModEntities.DEARC.get(), DearcRenderer::new);
            EntityRenderers.register(ModEntities.LION.get(), LionRenderer::new);
            EntityRenderers.register(ModEntities.PHILIPPINE_EAGLE.get(), PhilippineEagleRenderer::new);
            EntityRenderers.register(ModEntities.MONKEY.get(), MonkeyRenderer::new);
            EntityRenderers.register(ModEntities.WOLVERINE.get(), WolverineRenderer::new);
            EntityRenderers.register(ModEntities.MERGANSER.get(), MerganserRenderer::new);
        }
    }
}