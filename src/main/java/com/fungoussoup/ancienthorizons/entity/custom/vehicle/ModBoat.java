package com.fungoussoup.ancienthorizons.entity.custom.vehicle;

import com.fungoussoup.ancienthorizons.item.ModBoatItem;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModBoat extends Boat {
    private static final EntityDataAccessor<Integer> WOOD_TYPE;

    public ModBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }


    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WOOD_TYPE, 0);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Type", 8)) {
            this.setWoodType(Type.byName(pCompound.getString("Type")));
        }

    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("Type", this.getWoodType().getName());
    }

    public Type getWoodType() {
        return Type.byId((Integer)this.entityData.get(WOOD_TYPE));
    }

    public void setWoodType(Type type) {
        this.entityData.set(WOOD_TYPE, type.ordinal());
    }

    public Item getDropItem() {
        return (Item)this.getWoodType().getItem().get();
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    static {
        WOOD_TYPE = SynchedEntityData.defineId(ModBoat.class, EntityDataSerializers.INT);
    }

    public enum Type {
        WILLOW("willow", ModItems.WILLOW_BOAT, ModItems.WILLOW_CHEST_BOAT),
        HORNBEAM("hornbeam", ModItems.HORNBEAM_BOAT, ModItems.HORNBEAM_CHEST_BOAT),
        LINDEN("linden", ModItems.LINDEN_BOAT, ModItems.LINDEN_CHEST_BOAT),
        ASPEN("aspen", ModItems.ASPEN_BOAT, ModItems.ASPEN_CHEST_BOAT),
        SYCAMORE("sycamore", ModItems.SYCAMORE_BOAT, ModItems.SYCAMORE_CHEST_BOAT),
        BAOBAB("baobab", ModItems.BAOBAB_BOAT, ModItems.BAOBAB_CHEST_BOAT),
        GINKGO("ginkgo", ModItems.GINKGO_BOAT, ModItems.GINKGO_CHEST_BOAT),
        POPLAR("poplar", ModItems.POPLAR_BOAT, ModItems.POPLAR_CHEST_BOAT),
        MAPLE("maple", ModItems.MAPLE_BOAT, ModItems.MAPLE_CHEST_BOAT),
        PALM("palm", ModItems.PALM_BOAT, ModItems.PALM_CHEST_BOAT),
        BEECH("beech", ModItems.BEECH_BOAT, ModItems.BEECH_CHEST_BOAT),
        ASH("ash", ModItems.ASH_BOAT, ModItems.ASH_CHEST_BOAT),
        EUCALYPTUS("eucalyptus", ModItems.EUCALYPTUS_BOAT, ModItems.EUCALYPTUS_CHEST_BOAT),
        REDWOOD("redwood", ModItems.REDWOOD_BOAT, ModItems.REDWOOD_CHEST_BOAT),
        MONKEY_PUZZLE("monkey_puzzle", ModItems.MONKEY_PUZZLE_BOAT, ModItems.MONKEY_PUZZLE_CHEST_BOAT),
        YEW("yew", ModItems.YEW_BOAT, ModItems.YEW_CHEST_BOAT);

        private final String name;
        private final DeferredItem<ModBoatItem> item;
        private final DeferredItem<ModBoatItem> chestItem;

        Type(String name, DeferredItem<ModBoatItem> item, DeferredItem<ModBoatItem> chestItem) {
            this.name = name;
            this.item = item;
            this.chestItem = chestItem;
        }

        public ResourceLocation getTexture(boolean hasChest) {
            return ResourceLocation.fromNamespaceAndPath("ancienthorizons",
                    "textures/entity/" + (hasChest ? "chest_boat" : "boat") + "/" + this.name + ".png");
        }

        public String getModelLocation() {
            return "boat/" + this.name;
        }

        public String getChestModelLocation() {
            return "chest_boat/" + this.name;
        }

        public String getName() {
            return this.name;
        }

        public DeferredItem<ModBoatItem> getItem() {
            return this.item;
        }

        public DeferredItem<ModBoatItem> getChestItem() {
            return this.chestItem;
        }

        public static Type byId(int id) {
            Type[] values = values();
            return id >= 0 && id < values.length ? values[id] : values[0];
        }

        public static Type byName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return values()[0];
        }
    }

}