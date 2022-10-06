package com.github.alexthe668.iwannaskate.server.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IWSWorldData extends SavedData {

    private static final String IDENTIFIER = "iwannaskate_world_data";
    private int skaterSpawnDelay;
    private float skaterSpawnChance;
    private UUID skaterUUID;
    private static Map<Level, IWSWorldData> dataMap = new HashMap<>();

    public IWSWorldData() {
        super();
    }

    public static IWSWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
            IWSWorldData fromMap = dataMap.get(overworld);
            if(fromMap == null){
                DimensionDataStorage storage = overworld.getDataStorage();
                IWSWorldData data = storage.computeIfAbsent(IWSWorldData::load, IWSWorldData::new, IDENTIFIER);
                if (data != null) {
                    data.setDirty();
                }
                dataMap.put(world, data);
                return data;
            }
            return fromMap;
        }
        return null;
    }

    public static IWSWorldData load(CompoundTag nbt) {
        IWSWorldData data = new IWSWorldData();
        if (nbt.contains("SkaterSpawnDelay", 99)) {
            data.skaterSpawnDelay = nbt.getInt("SkaterSpawnDelay");
        }
        if (nbt.contains("SkaterSpawnChance", 99)) {
            data.skaterSpawnChance = nbt.getFloat("SkaterSpawnChance");
        }
        if (nbt.contains("SkaterUUID", 8)) {
            data.skaterUUID = UUID.fromString(nbt.getString("SkaterUUID"));
        }
        return data;
    }

    public int getSkaterSpawnDelay() {
        return this.skaterSpawnDelay;
    }

    public void setSkaterSpawnDelay(int delay) {
        this.skaterSpawnDelay = delay;
    }

    public float getSkaterSpawnChance() {
        return this.skaterSpawnChance;
    }

    public void setSkaterSpawnChance(float chance) {
        this.skaterSpawnChance = chance;
    }

    public void setSkaterUUID(UUID id) {
        this.skaterUUID = id;
    }


    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("beachedCachalotSpawnDelay", this.skaterSpawnDelay);
        compound.putFloat("beachedCachalotSpawnChance", this.skaterSpawnChance);
        if (this.skaterUUID != null) {
            compound.putString("beachedCachalotId", this.skaterUUID.toString());
        }
        return compound;
    }
}
