package com.kuba6000.mobsinfo.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;

import com.kuba6000.mobsinfo.api.helper.TranslationHelper;

public class SpawnInfo {

    enum Translations {

        BIOME,
        STRUCTURE,
        EVENT;

        final String key;

        Translations() {
            key = "mobsinfo.spawninfo." + this.name()
                .toLowerCase();
        }

        public String get() {
            return StatCollector.translateToLocal(key);
        }

        public List<String> getAllLines() {
            ArrayList<String> lines = new ArrayList<>(Collections.singletonList(StatCollector.translateToLocal(key)));
            int i = 1;
            while (StatCollector.canTranslate(key + "_" + i))
                lines.add(StatCollector.translateToLocal(key + "_" + (i++)));
            return lines;
        }

        public String get(Object... args) {
            return TranslationHelper.translateFormattedFixed(key, args);
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return get();
        }
    }

    private static final HashSet<SpawnInfo> allKnownInfos = new HashSet<>();

    public static Set<SpawnInfo> getAllKnownInfos() {
        return allKnownInfos;
    }

    /**
     * Interns SpawnListEntryComparator's per-place instances, since they're immutable value objects and
     * each mob otherwise builds its own. Identity-keyed (biomes are singletons, no equals/hashCode override).
     * Concurrent: spawn lists rebuild from the client packet handler on server join.
     */
    private static final Map<BiomeGenBase, SpawnInfoBiome> BIOME_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, SpawnInfoStructure> STRUCTURE_CACHE = new ConcurrentHashMap<>();

    /**
     * @return The shared instance for this biome, creating (and registering as a known place) it if needed.
     */
    public static SpawnInfoBiome ofBiome(BiomeGenBase biome) {
        return BIOME_CACHE.computeIfAbsent(biome, SpawnInfoBiome::new);
    }

    /**
     * @return The shared instance for this structure, creating (and registering as a known place) it if needed.
     */
    public static SpawnInfoStructure ofStructure(String structureName) {
        return STRUCTURE_CACHE.computeIfAbsent(structureName, SpawnInfoStructure::new);
    }

    protected final String info;

    public SpawnInfo() {
        this.info = null;
        if (this.getClass() == SpawnInfo.class) throw new UnsupportedOperationException();
    }

    public SpawnInfo(String info) {
        this.info = info;
        allKnownInfos.add(this);
    }

    public String getInfo() {
        return info;
    }

    private int cachedHashCode = 0;

    /**
     * {@link #getInfo()} concatenates a translated prefix onto the place name, so hashing allocated a String on every
     * set insertion and on every frame of the NEI spawn tooltip, which compares hashes pairwise. Cached the same way
     * {@link com.kuba6000.mobsinfo.api.utils.ItemID} does. Switching language after a hash has been taken leaves it
     * stale, which is harmless now that instances are canonical: there is only ever one object per place to compare.
     */
    @Override
    public int hashCode() {
        int code = cachedHashCode;
        if (code == 0) {
            code = getInfo().hashCode();
            if (code == 0) {
                code = 1;
            }
            cachedHashCode = code;
        }
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpawnInfo)) return false;
        return this.hashCode() == obj.hashCode();
    }

    public static class SpawnInfoBiome extends SpawnInfo {

        private final BiomeGenBase biome;

        public SpawnInfoBiome(BiomeGenBase biome) {
            this.biome = biome;
            allKnownInfos.add(this);
        }

        @Override
        public String getInfo() {
            return Translations.BIOME.get() + biome.biomeName;
        }

        public BiomeGenBase getBiome() {
            return biome;
        }
    }

    public static class SpawnInfoStructure extends SpawnInfo {

        private final String structureName;

        public SpawnInfoStructure(String structureName) {
            this.structureName = structureName;
            allKnownInfos.add(this);
        }

        @Override
        public String getInfo() {
            return Translations.STRUCTURE.get() + structureName;
        }
    }

    public static class SpawnInfoEvent extends SpawnInfo {

        private final String eventInfo;

        public SpawnInfoEvent(String eventInfo) {
            this.eventInfo = eventInfo;
            // allKnownInfos.add(this); don't save events
        }

        @Override
        public String getInfo() {
            return Translations.EVENT.get() + eventInfo;
        }
    }

}
