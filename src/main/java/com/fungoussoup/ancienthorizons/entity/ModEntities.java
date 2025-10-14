package com.fungoussoup.ancienthorizons.entity;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.custom.mob.*;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda.AnacondaPartEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.azhdarchidae.CryodrakonEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.sauropoda.DiplodocusEntity;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.BactrianCamelSpit;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.ChakramProjectileEntity;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.TranquilizerDartEntity;
import com.fungoussoup.ancienthorizons.entity.custom.mob.passerine.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AncientHorizons.MOD_ID);

    public static final Supplier<EntityType<TranquilizerDartEntity>> TRANQ_DART =
            ENTITY_TYPES.register("tranquilizer_dart",
                    () -> EntityType.Builder.of(TranquilizerDartEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("tranquilizer_dart"));

    public static final Supplier<EntityType<BactrianCamelSpit>> BACTRIAN_CAMEL_SPIT =
            ENTITY_TYPES.register("bactrian_camel_spit", () -> EntityType.Builder
                    .<BactrianCamelSpit>of(BactrianCamelSpit::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("bactrian_camel_spit"));

    public static final DeferredHolder<EntityType<?>, EntityType<AnacondaPartEntity>> ANACONDA_PART =
            ENTITY_TYPES.register("anaconda_part", () -> EntityType.Builder.<AnacondaPartEntity>of(AnacondaPartEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F)
                    .build("anaconda_part"));

    public static final Supplier<EntityType<ChakramProjectileEntity>> CHAKRAM =
            ENTITY_TYPES.register("chakram",
                    () -> EntityType.Builder.<ChakramProjectileEntity>of(ChakramProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "chakram").toString()));


    public static final Supplier<EntityType<TigerEntity>> TIGER =
            ENTITY_TYPES.register("tiger", () -> EntityType.Builder.of(TigerEntity::new, MobCategory.CREATURE)
                    .sized(1f,1.4f).build("tiger"));

    public static final Supplier<EntityType<SnowLeopardEntity>> SNOW_LEOPARD =
            ENTITY_TYPES.register("snow_leopard", () -> EntityType.Builder.of(SnowLeopardEntity::new, MobCategory.CREATURE)
                    .sized(0.6f,1f).build("snow_leopard"));

    public static final Supplier<EntityType<ZebraEntity>> ZEBRA =
            ENTITY_TYPES.register("zebra", () -> EntityType.Builder.of(ZebraEntity::new, MobCategory.CREATURE)
                    .sized(1.3965f,1.6f).build("zebra"));

    public static final Supplier<EntityType<ZonkeyEntity>> ZONKEY =
            ENTITY_TYPES.register("zonkey", () -> EntityType.Builder.of(ZonkeyEntity::new, MobCategory.CREATURE)
                    .sized(1.3965f,1.6f).build("zonkey"));

    public static final Supplier<EntityType<ZorseEntity>> ZORSE =
            ENTITY_TYPES.register("zorse", () -> EntityType.Builder.of(ZorseEntity::new, MobCategory.CREATURE)
                    .sized(1.3965f,1.6f).build("zorse"));

    public static final Supplier<EntityType<DomesticGoatEntity>> DOMESTIC_GOAT =
            ENTITY_TYPES.register("domestic_goat", () -> EntityType.Builder.of(DomesticGoatEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.3F).build("domestic_goat"));

    public static final Supplier<EntityType<SeagullEntity>> SEAGULL =
            ENTITY_TYPES.register("seagull", () -> EntityType.Builder.of(SeagullEntity::new, MobCategory.CREATURE)
                    .sized(0.6f,0.8f).build("seagull"));

    public static final Supplier<EntityType<TitEntity>> TIT =
            ENTITY_TYPES.register("tit", () -> EntityType.Builder.of(TitEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("tit"));

    public static final Supplier<EntityType<SparrowEntity>> SPARROW =
            ENTITY_TYPES.register("sparrow", () -> EntityType.Builder.of(SparrowEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("sparrow"));

    public static final Supplier<EntityType<BlackcapEntity>> BLACKCAP =
            ENTITY_TYPES.register("blackcap", () -> EntityType.Builder.of(BlackcapEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("blackcap"));

    public static final Supplier<EntityType<BluethroatEntity>> BLUETHROAT =
            ENTITY_TYPES.register("bluethroat", () -> EntityType.Builder.of(BluethroatEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("bluethroat"));

    public static final Supplier<EntityType<BullfinchEntity>> BULLFINCH =
            ENTITY_TYPES.register("bullfinch", () -> EntityType.Builder.of(BullfinchEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("bullfinch"));

    public static final Supplier<EntityType<CanaryEntity>> CANARY =
            ENTITY_TYPES.register("canary", () -> EntityType.Builder.of(CanaryEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("canary"));

    public static final Supplier<EntityType<CardinalEntity>> CARDINAL =
            ENTITY_TYPES.register("cardinal", () -> EntityType.Builder.of(CardinalEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("cardinal"));

    public static final Supplier<EntityType<ChaffinchEntity>> CHAFFINCH =
            ENTITY_TYPES.register("chaffinch", () -> EntityType.Builder.of(ChaffinchEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("chaffinch"));

    public static final Supplier<EntityType<GoldcrestEntity>> GOLDCREST =
            ENTITY_TYPES.register("goldcrest", () -> EntityType.Builder.of(GoldcrestEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("goldcrest"));

    public static final Supplier<EntityType<GoldfinchEntity>> GOLDFINCH =
            ENTITY_TYPES.register("goldfinch", () -> EntityType.Builder.of(GoldfinchEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("goldfinch"));

    public static final Supplier<EntityType<NightingaleEntity>> NIGHTINGALE =
            ENTITY_TYPES.register("nightingale", () -> EntityType.Builder.of(NightingaleEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("nightingale"));

    public static final Supplier<EntityType<RedstartEntity>> REDSTART =
            ENTITY_TYPES.register("redstart", () -> EntityType.Builder.of(RedstartEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("redstart"));

    public static final Supplier<EntityType<ReedlingEntity>> REEDLING =
            ENTITY_TYPES.register("reedling", () -> EntityType.Builder.of(ReedlingEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("reedling"));

    public static final Supplier<EntityType<RobinEntity>> ROBIN =
            ENTITY_TYPES.register("robin", () -> EntityType.Builder.of(RobinEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("robin"));

    public static final Supplier<EntityType<SiskinEntity>> SISKIN =
            ENTITY_TYPES.register("siskin", () -> EntityType.Builder.of(SiskinEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("siskin"));

    public static final Supplier<EntityType<SkylarkEntity>> SKYLARK =
            ENTITY_TYPES.register("skylark", () -> EntityType.Builder.of(SkylarkEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("skylark"));

    public static final Supplier<EntityType<WagtailEntity>> WAGTAIL =
            ENTITY_TYPES.register("wagtail", () -> EntityType.Builder.of(WagtailEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("wagtail"));

    public static final Supplier<EntityType<WaxwingEntity>> WAXWING =
            ENTITY_TYPES.register("waxwing", () -> EntityType.Builder.of(WaxwingEntity::new, MobCategory.CREATURE)
                    .sized(0.5f,0.5f).build("waxwing"));

    public static final Supplier<EntityType<ElephantEntity>> ELEPHANT =
            ENTITY_TYPES.register("elephant", () -> EntityType.Builder.of(ElephantEntity::new, MobCategory.CREATURE)
                    .sized(1.5f, 4.0f).build("elephant"));

    public static final Supplier<EntityType<PangolinEntity>> PANGOLIN =
            ENTITY_TYPES.register("pangolin", () -> EntityType.Builder.of(PangolinEntity::new, MobCategory.CREATURE)
                    .sized(1.0f, 1.0f).build("pangolin"));

    public static final Supplier<EntityType<GiraffeEntity>> GIRAFFE =
            ENTITY_TYPES.register("giraffe", () -> EntityType.Builder.of(GiraffeEntity::new, MobCategory.CREATURE)
                    .sized(1.3f, 2.8f).build("giraffe"));

    public static final Supplier<EntityType<RaccoonEntity>> RACCOON =
            ENTITY_TYPES.register("raccoon", () -> EntityType.Builder.of(RaccoonEntity::new, MobCategory.CREATURE)
                    .sized(0.7f, 0.6f).build("raccoon"));

    public static final Supplier<EntityType<EarthwormEntity>> EARTHWORM =
            ENTITY_TYPES.register("earthworm", () -> EntityType.Builder.of(EarthwormEntity::new, MobCategory.AMBIENT)
                    .sized(0.2f, 0.2f).build("earthworm"));

    public static final Supplier<EntityType<BrownBearEntity>> BROWN_BEAR =
            ENTITY_TYPES.register("brown_bear", () -> EntityType.Builder.of(BrownBearEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.4F).build("brown_bear"));

    public static final Supplier<EntityType<MantisEntity>> MANTIS =
            ENTITY_TYPES.register("mantis", () -> EntityType.Builder.of(MantisEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1f).build("mantis"));

    public static final Supplier<EntityType<PenguinEntity>> PENGUIN =
            ENTITY_TYPES.register("penguin", () -> EntityType.Builder.of(PenguinEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.2f).build("penguin"));

    public static final Supplier<EntityType<EagleEntity>> EAGLE =
            ENTITY_TYPES.register("eagle", () -> EntityType.Builder.of(EagleEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.8f).build("eagle"));

    public static final Supplier<EntityType<PhilippineEagleEntity>> PHILIPPINE_EAGLE =
            ENTITY_TYPES.register("philippine_eagle", () -> EntityType.Builder.of(PhilippineEagleEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.8f).build("philippine_eagle"));

    public static final Supplier<EntityType<MonkeyEntity>> MONKEY =
            ENTITY_TYPES.register("monkey", () -> EntityType.Builder.of(MonkeyEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.8f).build("monkey"));

    public static final Supplier<EntityType<BactrianCamel>> BACTRIAN_CAMEL =
            ENTITY_TYPES.register("bactrian_camel", () -> EntityType.Builder.of(BactrianCamel::new, MobCategory.CREATURE)
                    .sized(1.7f,2.375f).build("bactrian_camel"));

    public static final Supplier<EntityType<BelugaSturgeonEntity>> BELUGA_STURGEON =
            ENTITY_TYPES.register("beluga_sturgeon", () -> EntityType.Builder.of(BelugaSturgeonEntity::new, MobCategory.WATER_CREATURE)
                    .sized(1f, 1f).build("beluga_sturgeon"));

    public static final Supplier<EntityType<StoatEntity>> STOAT =
            ENTITY_TYPES.register("stoat", () -> EntityType.Builder.of(StoatEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("stoat"));

    public static final Supplier<EntityType<PheasantEntity>> PHEASANT =
            ENTITY_TYPES.register("pheasant", () -> EntityType.Builder.of(PheasantEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.7f).build("pheasant"));

    public static final Supplier<EntityType<ChimpanzeeEntity>> CHIMPANZEE =
            ENTITY_TYPES.register("chimpanzee", () -> EntityType.Builder.of(ChimpanzeeEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.6f).build("chimpanzee"));

    public static final Supplier<EntityType<SaolaEntity>> SAOLA =
            ENTITY_TYPES.register("saola", () -> EntityType.Builder.of(SaolaEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1.1f).build("saola"));

    public static final Supplier<EntityType<CryodrakonEntity>> CRYODRAKON =
            ENTITY_TYPES.register("cryodrakon", () -> EntityType.Builder.of(CryodrakonEntity::new, MobCategory.CREATURE)
                    .sized(1f, 2.2f).build("cryodrakon"));

    public static final Supplier<EntityType<FlamingoEntity>> FLAMINGO =
            ENTITY_TYPES.register("flamingo", () -> EntityType.Builder.of(FlamingoEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1f).build("flamingo"));

    public static final Supplier<EntityType<AnacondaEntity>> ANACONDA =
            ENTITY_TYPES.register("anaconda", () -> EntityType.Builder.of(AnacondaEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.6f).build("anaconda"));

    public static final Supplier<EntityType<RuffEntity>> RUFF =
            ENTITY_TYPES.register("ruff", () -> EntityType.Builder.of(RuffEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("ruff"));

    public static final Supplier<EntityType<FisherEntity>> FISHER =
            ENTITY_TYPES.register("fisher", () -> EntityType.Builder.of(FisherEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("fisher"));

    public static final Supplier<EntityType<HareEntity>> HARE =
            ENTITY_TYPES.register("hare", () -> EntityType.Builder.of(HareEntity::new, MobCategory.CREATURE)
                    .sized(0.4f, 0.6f).build("hare"));

    public static final Supplier<EntityType<RoadrunnerEntity>> ROADRUNNER =
            ENTITY_TYPES.register("roadrunner", () -> EntityType.Builder.of(RoadrunnerEntity::new, MobCategory.CREATURE)
                    .sized(0.4f, 0.65f).build("roadrunner"));

    public static final Supplier<EntityType<CicadaEntity>> CICADA =
            ENTITY_TYPES.register("cicada", () -> EntityType.Builder.of(CicadaEntity::new, MobCategory.AMBIENT)
                    .sized(0.4f, 0.2f).build("cicada"));

    public static final Supplier<EntityType<DiplodocusEntity>> DIPLODOCUS =
            ENTITY_TYPES.register("diplodocus", () -> EntityType.Builder.of(DiplodocusEntity::new, MobCategory.CREATURE)
                    .sized(1.7f, 4.7f).build("diplodocus"));

    public static final Supplier<EntityType<HoatzinEntity>> HOATZIN =
            ENTITY_TYPES.register("hoatzin", () -> EntityType.Builder.of(HoatzinEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("hoatzin"));

    public static final Supplier<EntityType<HypnovenatorEntity>> HYPNOVENATOR =
            ENTITY_TYPES.register("hypnovenator", () -> EntityType.Builder.of(HypnovenatorEntity::new, MobCategory.CREATURE)
                .sized(0.5f, 0.5f).build("hypnovenator"));

    public static final Supplier<EntityType<VelociraptorEntity>> VELOCIRAPTOR =
            ENTITY_TYPES.register("velociraptor", () -> EntityType.Builder.of(VelociraptorEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.5f).build("velociraptor"));

    public static final Supplier<EntityType<DeerEntity>> DEER =
            ENTITY_TYPES.register("deer", () -> EntityType.Builder.of(DeerEntity::new, MobCategory.CREATURE)
                    .sized(0.65f, 1.35f).build("deer"));

    public static final Supplier<EntityType<RoeDeerEntity>> ROE_DEER =
            ENTITY_TYPES.register("roe_deer", () -> EntityType.Builder.of(RoeDeerEntity::new, MobCategory.CREATURE)
                    .sized(0.45f, 0.875f).build("roe_deer"));

    public static final Supplier<EntityType<WhiteSharkEntity>> WHITE_SHARK =
            ENTITY_TYPES.register("white_shark", () -> EntityType.Builder.of(WhiteSharkEntity::new, MobCategory.WATER_CREATURE)
                    .sized(1f, 1.375f).build("white_shark"));

    public static final Supplier<EntityType<DearcEntity>> DEARC =
            ENTITY_TYPES.register("dearc", () -> EntityType.Builder.of(DearcEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.5f, 0.5f).build("dearc"));

    public static final Supplier<EntityType<LionEntity>> LION =
            ENTITY_TYPES.register("lion", () -> EntityType.Builder.of(LionEntity::new, MobCategory.WATER_CREATURE)
                    .sized(1f,1.4f).build("lion"));

    public static Supplier<EntityType<GallimimusEntity>> GALLIMIMUS =
            ENTITY_TYPES.register("gallimimus", () -> EntityType.Builder.of(GallimimusEntity::new, MobCategory.CREATURE)
                    .sized(1.4f, 2f).build("gallimimus"));

    public static Supplier<EntityType<CrocodileEntity>> CROCODILE =
            ENTITY_TYPES.register("crocodile", () -> EntityType.Builder.of(CrocodileEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.625f).build("crocodile"));

    public static final Supplier<EntityType<HippopotamusEntity>> HIPPOPOTAMUS =
            ENTITY_TYPES.register("hippopotamus", () -> EntityType.Builder.of(HippopotamusEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1.75f).build("hippopotamus"));

    public static final Supplier<EntityType<EromangasaurusEntity>> EROMANGASAURUS =
            ENTITY_TYPES.register("eromangasaurus", () -> EntityType.Builder.of(EromangasaurusEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.875f, 0.875f).build("eromangasaurus"));

    public static final Supplier<EntityType<BeipiaosaurusEntity>> BEIPIAOSAURUS =
            ENTITY_TYPES.register("beipiaosaurus", () -> EntityType.Builder.of(BeipiaosaurusEntity::new, MobCategory.CREATURE)
                .sized(0.875f, 0.875f).build("beipiaosaurus"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
