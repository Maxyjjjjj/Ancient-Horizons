package com.fungoussoup.ancienthorizons.entity.ai.sensing;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.ai.BactrianCamelAi;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;

import java.util.function.Supplier;

public class ModSensorType<U extends Sensor<?>> {
    public static final SensorType<TemptingSensor> BACTRIAN_CAMEL_TEMPTATIONS = register("bactrian_camel_temptations", () -> new TemptingSensor(BactrianCamelAi.getTemptations()));

    private final Supplier<U> factory;

    public ModSensorType(Supplier<U> factory) {
        this.factory = factory;
    }

    public U create() {
        return this.factory.get();
    }
    private static <U extends Sensor<?>> SensorType<U> register(String key, Supplier<U> sensorSupplier) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, key), new SensorType<>(sensorSupplier));
    }
}
