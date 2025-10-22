package com.fungoussoup.ancienthorizons.registry;

import java.util.ArrayList;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public class ModSoundEvents {

    public static void init() {
        for (Pair<ResourceLocation, SoundEvent> registry : SOUND_EVENTS) {
            try {
                if (registry.getA() != null && registry.getB() != null) {
                    Registry.register(BuiltInRegistries.SOUND_EVENT, registry.getA(), registry.getB());
                } else {
                    System.err.println("Failed to register sound event: ResourceLocation or SoundEvent is null");
                }
            } catch (Exception e) {
                System.err.println("Error registering sound event: " + registry.getA());
            }
        }
    }

    public static final ArrayList<Pair<ResourceLocation, SoundEvent>> SOUND_EVENTS = new ArrayList<>();

    // TIGER
    public static final SoundEvent TIGER_AMBIENT = registerSoundEvent("tiger_idle", createSoundEvent("entity.tiger.idle"));
    public static final SoundEvent TIGER_HURT = registerSoundEvent("tiger_hurt", createSoundEvent("entity.tiger.hurt"));
    public static final SoundEvent TIGER_DEATH = registerSoundEvent("tiger_death", createSoundEvent("entity.tiger.death"));
    public static final SoundEvent TIGER_WARNING = registerSoundEvent("tiger_warning", createSoundEvent("entity.tiger.warning"));
    public static final SoundEvent TIGER_ROAR = registerSoundEvent("tiger_roar", createSoundEvent("entity.tiger.roar"));
    public static final SoundEvent TIGER_ROAR_AGGRO = registerSoundEvent("tiger_roar_aggro", createSoundEvent("entity.tiger.roar_aggro"));
    public static final SoundEvent TIGER_YAWN = registerSoundEvent("tiger_yawn", createSoundEvent("entity.tiger.yawn"));
    public static final SoundEvent TIGER_AMBIENT_BABY = registerSoundEvent("tiger_idle_baby", createSoundEvent("entity.tiger.idle_baby"));
    public static final SoundEvent TIGER_ANGRY = registerSoundEvent("tiger_angry", createSoundEvent("entity.tiger.angry"));
    public static final SoundEvent TIGER_ATTACK = registerSoundEvent("tiger_attack", createSoundEvent("entity.tiger.attack"));

    // GIRAFFE
    public static final SoundEvent GIRAFFE_AMBIENT = registerSoundEvent("giraffe_idle", createSoundEvent("entity.giraffe.idle"));
    public static final SoundEvent GIRAFFE_HURT = registerSoundEvent("giraffe_hurt", createSoundEvent("entity.giraffe.hurt"));
    public static final SoundEvent GIRAFFE_DEATH = registerSoundEvent("giraffe_death", createSoundEvent("entity.giraffe.death"));
    public static final SoundEvent GIRAFFE_SNORT = registerSoundEvent("giraffe_snort", createSoundEvent("entity.giraffe.snort"));

    // ZEBRA
    public static final SoundEvent ZEBRA_AMBIENT = registerSoundEvent("zebra_idle", createSoundEvent("entity.zebra.idle"));
    public static final SoundEvent ZEBRA_HURT = registerSoundEvent("zebra_hurt", createSoundEvent("entity.zebra.hurt"));
    public static final SoundEvent ZEBRA_DEATH = registerSoundEvent("zebra_death", createSoundEvent("entity.zebra.death"));
    public static final SoundEvent ZEBRA_ANGRY = registerSoundEvent("zebra_angry", createSoundEvent("entity.zebra.angry"));

    // ELEPHANT
    public static final SoundEvent ELEPHANT_AMBIENT = registerSoundEvent("elephant_idle", createSoundEvent("entity.elephant.idle"));
    public static final SoundEvent ELEPHANT_HURT = registerSoundEvent("elephant_hurt", createSoundEvent("entity.elephant.hurt"));
    public static final SoundEvent ELEPHANT_DEATH = registerSoundEvent("elephant_death", createSoundEvent("entity.elephant.death"));
    public static final SoundEvent ELEPHANT_TRUMPET = registerSoundEvent("elephant_trumpet", createSoundEvent("entity.elephant.trumpet"));
    public static final SoundEvent ELEPHANT_STEP = registerSoundEvent("elephant_step", createSoundEvent("entity.elephant.step"));
    public static final SoundEvent ELEPHANT_CHARGE = registerSoundEvent("elephant_charge", createSoundEvent("entity.elephant.charge"));

    // RACCOON
    public static final SoundEvent RACCOON_AMBIENT = registerSoundEvent("raccoon_idle", createSoundEvent("entity.raccoon.idle"));
    public static final SoundEvent RACCOON_HURT = registerSoundEvent("raccoon_hurt", createSoundEvent("entity.raccoon.hurt"));
    public static final SoundEvent RACCOON_DEATH = registerSoundEvent("raccoon_death", createSoundEvent("entity.raccoon.death"));

    // PASSERINES
    public static final SoundEvent PASSERINE_CHIRP = registerSoundEvent("passerine_chirp", createSoundEvent("entity.passerine.chirp"));

    // EAGLE
    public static final SoundEvent EAGLE_AMBIENT = registerSoundEvent("eagle_idle", createSoundEvent("entity.eagle.idle"));
    public static final SoundEvent EAGLE_HURT = registerSoundEvent("eagle_hurt", createSoundEvent("entity.eagle.hurt"));
    public static final SoundEvent EAGLE_DEATH = registerSoundEvent("eagle_death", createSoundEvent("entity.eagle.death"));

    // PHEASANT
    public static final SoundEvent PHEASANT_AMBIENT = registerSoundEvent("pheasant_idle", createSoundEvent("entity.pheasant.idle"));
    public static final SoundEvent PHEASANT_HURT = registerSoundEvent("pheasant_hurt", createSoundEvent("entity.pheasant.hurt"));
    public static final SoundEvent PHEASANT_DEATH = registerSoundEvent("pheasant_death", createSoundEvent("entity.pheasant.death"));

    // COW (VANILLA)
    public static final SoundEvent COW_RUT = registerSoundEvent("cow_rut", createSoundEvent("entity.cow.rut"));

    // ROADRUNNER
    public static final SoundEvent ROADRUNNER_IDLE = registerSoundEvent("roadrunner_idle", createSoundEvent("entity.roadrunner.idle"));
    public static final SoundEvent ROADRUNNER_HURT = registerSoundEvent("roadrunner_hurt", createSoundEvent("entity.roadrunner.hurt"));
    public static final SoundEvent ROADRUNNER_DEATH = registerSoundEvent("roadrunner_death", createSoundEvent("entity.roadrunner.death"));
    public static final SoundEvent ROADRUNNER_MEEPMEEP = registerSoundEvent("roadrunner_meep", createSoundEvent("entity.roadrunner.meep"));
    public static final SoundEvent ROADRUNNER_CRAZY = registerSoundEvent("roadrunner_crazy", createSoundEvent("entity.roadrunner.crazy"));

    // PENGUIN
    public static final SoundEvent PENGUIN_IDLE = registerSoundEvent("penguin_idle", createSoundEvent("entity.penguin.idle"));
    public static final SoundEvent PENGUIN_HURT = registerSoundEvent("penguin_hurt", createSoundEvent("entity.penguin.hurt"));
    public static final SoundEvent PENGUIN_DEATH = registerSoundEvent("penguin_death", createSoundEvent("entity.penguin.death"));

    // DEER
    public static final SoundEvent DEER_AMBIENT = registerSoundEvent("deer_idle", createSoundEvent("entity.deer.idle"));
    public static final SoundEvent FAWN_AMBIENT = registerSoundEvent("deer_idle_baby", createSoundEvent("entity.deer.idle.baby"));
    public static final SoundEvent DEER_HURT = registerSoundEvent("deer_hurt", createSoundEvent("entity.deer.hurt"));
    public static final SoundEvent DEER_DEATH = registerSoundEvent("deer_death", createSoundEvent("entity.deer.death"));

    // GALLIMIMUS
    public static final SoundEvent GALLIMIMUS_AMBIENT = registerSoundEvent("gallimimus_idle", createSoundEvent("entity.gallimimus.idle"));
    public static final SoundEvent GALLIMIMUS_HURT = registerSoundEvent("gallimimus_hurt", createSoundEvent("entity.gallimimus.hurt"));
    public static final SoundEvent GALLIMIMUS_DEATH = registerSoundEvent("gallimimus_death", createSoundEvent("entity.gallimimus.death"));
    public static final SoundEvent GALLIMIMUS_ANGRY = registerSoundEvent("gallimimus_angry", createSoundEvent("entity.gallimimus.angry"));

    // CROC
    public static final SoundEvent CROC_AMBIENT = registerSoundEvent("crocodile_idle", createSoundEvent("entity.croc.idle"));
    public static final SoundEvent CROC_HURT = registerSoundEvent("crocodile_hurt", createSoundEvent("entity.croc.hurt"));
    public static final SoundEvent CROC_DEATH = registerSoundEvent("crocodile_death", createSoundEvent("entity.croc.death"));

    // HIPPOPOTAMUS
    public static final SoundEvent HIPPOPOTAMUS_AMBIENT = registerSoundEvent("hippopotamus_idle", createSoundEvent("entity.hippopotamus.idle"));
    public static final SoundEvent HIPPOPOTAMUS_HURT = registerSoundEvent("hippopotamus_hurt", createSoundEvent("entity.hippopotamus.hurt"));
    public static final SoundEvent HIPPOPOTAMUS_DEATH = registerSoundEvent("hippopotamus_death", createSoundEvent("entity.hippopotamus.death"));

    // NON-MOB SOUNDS
    public static final SoundEvent CANNON_LAUNCH = registerSoundEvent("cannon_launch", createSoundEvent("block.cannon.launch"));
    public static final SoundEvent CHAKRAM_THROW = registerSoundEvent("chakram_throw", createSoundEvent("item.chakram.throw"));

    // LION
    public static final SoundEvent LION_AMBIENT = registerSoundEvent("lion_idle", createSoundEvent("entity.lion.idle"));
    public static final SoundEvent LION_HURT = registerSoundEvent("lion_hurt", createSoundEvent("entity.lion.hurt"));
    public static final SoundEvent LION_DEATH = registerSoundEvent("lion_death", createSoundEvent("entity.lion.death"));
    public static final SoundEvent LION_YAWN = registerSoundEvent("lion_yawn", createSoundEvent("entity.lion.yawn"));
    public static final SoundEvent LION_WARNING = registerSoundEvent("lion_warning", createSoundEvent("entity.lion.warning"));
    public static final SoundEvent LION_ROAR = registerSoundEvent("lion_roar", createSoundEvent("entity.lion.roar"));
    public static final SoundEvent LION_ROAR_AGGRO = registerSoundEvent("lion_roar_aggro", createSoundEvent("entity.lion.roar_aggro"));
    public static final SoundEvent LION_AMBIENT_BABY = registerSoundEvent("lion_idle_baby", createSoundEvent("entity.lion.idle_baby"));
    public static final SoundEvent LION_ANGRY = registerSoundEvent("lion_angry", createSoundEvent("entity.lion.angry"));
    public static final SoundEvent LION_ATTACK = registerSoundEvent("lion_attack", createSoundEvent("entity.lion.attack"));

    // SAOLA

    public static final SoundEvent SAOLA_AMBIENT = registerSoundEvent("saola_idle", createSoundEvent("entity.saola.idle"));
    public static final SoundEvent SAOLA_HURT = registerSoundEvent("saola_hurt", createSoundEvent("entity.saola.hurt"));
    public static final SoundEvent SAOLA_DEATH = registerSoundEvent("saola_death", createSoundEvent("entity.saola.death"));

    // WOLVERINE

    public static final SoundEvent WOLVERINE_AMBIENT = registerSoundEvent("wolverine_idle", createSoundEvent("entity.wolverine.idle"));
    public static final SoundEvent WOLVERINE_HURT = registerSoundEvent("wolverine_hurt", createSoundEvent("entity.wolverine.hurt"));
    public static final SoundEvent WOLVERINE_DEATH = registerSoundEvent("wolverine_death", createSoundEvent("entity.wolverine.death"));

    // DIPLODOCUS

    public static final SoundEvent DIPLODOCUS_AMBIENT = registerSoundEvent("diplodocus_idle", createSoundEvent("entity.diplodocus.idle"));
    public static final SoundEvent DIPLODOCUS_HURT = registerSoundEvent("diplodocus_hurt", createSoundEvent("entity.diplodocus.hurt"));
    public static final SoundEvent DIPLODOCUS_DEATH = registerSoundEvent("diplodocus_death", createSoundEvent("entity.diplodocus.death"));

    // MERGANSER

    public static final SoundEvent MERGANSER_AMBIENT = registerSoundEvent("merganser_idle", createSoundEvent("entity.merganser.idle"));
    public static final SoundEvent MERGANSER_HURT = registerSoundEvent("merganser_hurt", createSoundEvent("entity.merganser.hurt"));
    public static final SoundEvent MERGANSER_DEATH = registerSoundEvent("merganser_death", createSoundEvent("entity.merganser.death"));


    private static SoundEvent createSoundEvent(String path) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath("ancienthorizons", path);
        return SoundEvent.createVariableRangeEvent(rl);
    }

    public static SoundEvent registerSoundEvent(String name, SoundEvent sound) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath("ancienthorizons", name);
        SOUND_EVENTS.add(new Pair<>(rl, sound));
        return sound;
    }
}