package com.kuba6000.mobsinfo.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.tuple.Pair;

import com.kuba6000.mobsinfo.api.helper.EnderIOHelper;
import com.kuba6000.mobsinfo.api.helper.InfernalMobsCoreHelper;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;
import com.kuba6000.mobsinfo.mixin.InfernalMobs.InfernalMobsCoreAccessor;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import jas.spawner.modern.MVELProfile;
import jas.spawner.modern.spawner.biome.group.BiomeHelper;
import jas.spawner.modern.spawner.biome.structure.StructureHandler;
import jas.spawner.modern.spawner.biome.structure.StructureHandlerRegistry;
import jas.spawner.modern.spawner.creature.entry.BiomeSpawnListRegistry;
import jas.spawner.modern.spawner.creature.entry.SpawnListEntry;
import jas.spawner.modern.spawner.creature.handler.LivingHandler;

public class MobRecipe {

    public final ArrayList<MobDrop> mOutputs;
    public int mMaxDamageChance;
    public final boolean infernalityAllowed;
    public final boolean alwaysinfernal;
    public static MobRecipeLoader.droplist infernaldrops;
    public final boolean isPeacefulAllowed;
    public final EntityLiving entity;
    public final float maxEntityHealth;
    public final boolean isUsableInVial;
    public final String entityName;
    public HashSet<SpawnInfo> spawnList;

    @SuppressWarnings("unchecked")
    public MobRecipe copy() {
        return new MobRecipe(
            (ArrayList<MobDrop>) mOutputs.clone(),
            mMaxDamageChance,
            infernalityAllowed,
            alwaysinfernal,
            isPeacefulAllowed,
            entity,
            maxEntityHealth,
            isUsableInVial,
            entityName,
            spawnList == null ? null : (HashSet<SpawnInfo>) spawnList.clone());
    }

    private MobRecipe(ArrayList<MobDrop> mOutputs, int mMaxDamageChance, boolean infernalityAllowed,
        boolean alwaysinfernal, boolean isPeacefulAllowed, EntityLiving entity, float maxEntityHealth, boolean isUsable,
        String entityName, HashSet<SpawnInfo> spawnList) {
        this.mOutputs = mOutputs;
        this.mMaxDamageChance = mMaxDamageChance;
        this.infernalityAllowed = infernalityAllowed;
        this.alwaysinfernal = alwaysinfernal;
        this.isPeacefulAllowed = isPeacefulAllowed;
        this.entity = entity;
        this.maxEntityHealth = maxEntityHealth;
        this.isUsableInVial = isUsable;
        this.entityName = entityName;
        this.spawnList = spawnList;
    }

    public static MobRecipe generateMobRecipe(EntityLiving e, String entityID, ArrayList<MobDrop> outputs) {
        return new MobRecipe(e, entityID, outputs);
    }

    @SuppressWarnings("unchecked")
    private MobRecipe(EntityLiving e, String entityID, ArrayList<MobDrop> outputs) {
        if (LoaderReference.InfernalMobs) {
            InfernalMobsCoreAccessor infernalMobsCore = (InfernalMobsCoreAccessor) InfernalMobsCore.instance();
            if (infernaldrops == null) {
                infernaldrops = MobRecipeLoader.getInfernalDrops();
            }
            infernalityAllowed = InfernalMobsCoreHelper.callIsClassAllowed((InfernalMobsCore) infernalMobsCore, e);
            alwaysinfernal = infernalMobsCore.callCheckEntityClassForced(e);
        } else {
            infernalityAllowed = false;
            alwaysinfernal = false;
        }

        if (infernaldrops == null) infernaldrops = new MobRecipeLoader.droplist();

        isPeacefulAllowed = !(e instanceof IMob);

        mOutputs = (ArrayList<MobDrop>) outputs.clone();
        int maxdamagechance = 0;
        for (MobDrop o : mOutputs) if (o.damages != null) for (int v : o.damages.values()) maxdamagechance += v;
        mMaxDamageChance = maxdamagechance;
        maxEntityHealth = e.getMaxHealth();
        entity = e;
        isUsableInVial = EnderIOHelper.canEntityBeCapturedWithSoulVial(e, entityID);
        entityName = entityID;
    }

    public void generateSpawnList() {
        if (MobNameToBiomeSpawnList == null) {
            MobNameToBiomeSpawnList = new HashMap<>();
            MobNameToStructureList = new HashMap<>();
            BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
            // JustAnotherSpawner.globalSettings().emptyVanillaSpawnLists
            if (LoaderReference.JustAnotherSpawner) {
                BiomeSpawnListRegistry biomeSpawnListRegistry = MVELProfile.worldSettings()
                    .biomeSpawnListRegistry();
                for (BiomeGenBase biome : biomeList) {
                    if (biome == null) continue;
                    new SpawnInfo.SpawnInfoBiome(biome);
                    for (SpawnListEntry spawnListEntry : biomeSpawnListRegistry
                        .getSpawnListFor(BiomeHelper.getPackageName(biome))) {
                        LivingHandler handler = spawnListEntry.getLivingHandler();
                        if (!handler.namedJASSpawnables.isEmpty()) {
                            for (String namedJASSpawnable : handler.namedJASSpawnables) {
                                Class<? extends Entity> entityClass = biomeSpawnListRegistry.livingGroupRegistry
                                    .jasNametoEntityClass()
                                    .get(namedJASSpawnable);
                                if (entityClass != null) MobNameToBiomeSpawnList
                                    .computeIfAbsent(
                                        EntityList.classToStringMapping.get(entityClass),
                                        s -> new ArrayList<>())
                                    .add(Pair.of(biome, null));
                            }

                        }
                    }
                }
                StructureHandlerRegistry registry = MVELProfile.worldSettings()
                    .structureHandlerRegistry();
                for (StructureHandler registryhandler : registry.handlers()) {
                    for (String structureKey : registryhandler.getStructureKeys()) {
                        new SpawnInfo.SpawnInfoStructure(structureKey);
                        for (SpawnListEntry spawnListEntry : registryhandler.getStructureSpawnList(structureKey)) {
                            LivingHandler handler = spawnListEntry.getLivingHandler();
                            if (!handler.namedJASSpawnables.isEmpty()) {
                                for (String namedJASSpawnable : handler.namedJASSpawnables) {
                                    Class<? extends Entity> entityClass = MVELProfile.worldSettings()
                                        .livingGroupRegistry()
                                        .jasNametoEntityClass()
                                        .get(namedJASSpawnable);
                                    if (entityClass != null) MobNameToStructureList
                                        .computeIfAbsent(
                                            EntityList.classToStringMapping.get(entityClass),
                                            s -> new ArrayList<>())
                                        .add(structureKey);
                                }

                            }
                        }
                    }
                }
                //
                // MVELProfile.worldSettings().eventSpawnRegistry().getEventsForTrigger()
            }

            // vanilla
            for (BiomeGenBase biome : biomeList) {
                if (biome == null) continue;
                // make sure to create known place
                new SpawnInfo.SpawnInfoBiome(biome);
                for (EnumCreatureType type : EnumCreatureType.values()) {
                    List<BiomeGenBase.SpawnListEntry> spawnableList = (List<BiomeGenBase.SpawnListEntry>) biome
                        .getSpawnableList(type);
                    if (spawnableList != null) for (BiomeGenBase.SpawnListEntry entry : spawnableList) {
                        MobNameToBiomeSpawnList
                            .computeIfAbsent(
                                EntityList.classToStringMapping.get(entry.entityClass),
                                s -> new ArrayList<>())
                            .add(Pair.of(biome, entry));
                    }
                }
            }
        }

        ArrayList<Pair<BiomeGenBase, BiomeGenBase.SpawnListEntry>> ar = MobNameToBiomeSpawnList.get(this.entityName);
        spawnList = new HashSet<>();
        if (ar != null) {
            for (Pair<BiomeGenBase, BiomeGenBase.SpawnListEntry> entry : ar) {
                spawnList.add(new SpawnInfo.SpawnInfoBiome(entry.getKey()));
            }
        }
        ArrayList<String> structs = MobNameToStructureList.get(this.entityName);
        if (structs != null) {
            for (String struct : structs) {
                spawnList.add(new SpawnInfo.SpawnInfoStructure(struct));
            }
        }

    }

    public void refresh() {
        int maxdamagechance = 0;
        for (MobDrop o : mOutputs) if (o.damages != null) for (int v : o.damages.values()) maxdamagechance += v;
        mMaxDamageChance = maxdamagechance;
    }

    public ItemStack[] generateRandomOutputs(World world, Random rnd, int lootinglevel, boolean includePlayerOnlyLoot,
        boolean includeInfernalDropsIfPossible) {
        ArrayList<ItemStack> stacks = new ArrayList<>(mOutputs.size());
        for (MobDrop o : mOutputs) {
            int chance = o.chance;
            if (o.playerOnly && !includePlayerOnlyLoot) continue;
            int amount = o.stack.stackSize;
            if (o.lootable && lootinglevel > 0) {
                chance += lootinglevel * 5000;
                if (chance > 10000) {
                    int div = (int) Math.ceil(chance / 10000d);
                    amount *= div;
                    chance /= div;
                }
            }
            if (chance == 10000 || rnd.nextInt(10000) < chance) {
                ItemStack s = o.stack.copy();
                s.stackSize = amount;
                if (o.enchantable != null) EnchantmentHelper.addRandomEnchantment(rnd, s, o.enchantable);
                if (o.damages != null) {
                    int rChance = rnd.nextInt(mMaxDamageChance);
                    int cChance = 0;
                    for (Map.Entry<Integer, Integer> damage : o.damages.entrySet()) {
                        cChance += damage.getValue();
                        if (rChance <= cChance) {
                            s.setItemDamage(damage.getKey());
                            break;
                        }
                    }
                }
                stacks.add(s);
            }
        }

        if (LoaderReference.InfernalMobs && includeInfernalDropsIfPossible) {
            InfernalMobsCoreAccessor infernalMobsCore = (InfernalMobsCoreAccessor) InfernalMobsCore.instance();
            if (infernalityAllowed && !InfernalMobsCoreHelper.getDimensionBlackList((InfernalMobsCore) infernalMobsCore)
                .contains(world.provider.dimensionId)) {
                int p = 0;
                if (alwaysinfernal || (rnd.nextInt(infernalMobsCore.getEliteRarity()) == 0)) {
                    p = 1;
                    if (rnd.nextInt(infernalMobsCore.getUltraRarity()) == 0) {
                        p = 2;
                        if (rnd.nextInt(infernalMobsCore.getInfernoRarity()) == 0) p = 3;
                    }
                }
                ArrayList<ItemStack> infernalstacks = null;
                if (p > 0) if (p == 1) {
                    infernalstacks = infernalMobsCore.getDropIdListElite();
                } else if (p == 2) {
                    infernalstacks = infernalMobsCore.getDropIdListUltra();
                } else {
                    infernalstacks = infernalMobsCore.getDropIdListInfernal();
                }
                if (infernalstacks != null) {
                    ItemStack infernalstack = infernalstacks.get(rnd.nextInt(infernalstacks.size()))
                        .copy();
                    // noinspection ConstantConditions
                    EnchantmentHelper.addRandomEnchantment(
                        rnd,
                        infernalstack,
                        infernalstack.getItem()
                            .getItemEnchantability());
                    stacks.add(infernalstack);
                }
            }
        }

        return stacks.toArray(new ItemStack[0]);
    }

    public EntityLiving createEntityCopy()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        EntityLiving entityCopy = this.entity.getClass()
            .getConstructor(World.class)
            .newInstance(this.entity.worldObj);
        MobRecipeLoader.preGenerationEntityModifiers(entityCopy, this.entityName);
        return entityCopy;
    }

    public static HashMap<String, MobRecipe> MobNameToRecipeMap = new HashMap<>();
    public static HashMap<String, ArrayList<Pair<BiomeGenBase, BiomeGenBase.SpawnListEntry>>> MobNameToBiomeSpawnList = null;
    public static HashMap<String, ArrayList<String>> MobNameToStructureList = null;

    public static MobRecipe getRecipeByEntityName(String mobName) {
        return MobNameToRecipeMap.get(mobName);
    }

    public static ArrayList<Pair<BiomeGenBase, BiomeGenBase.SpawnListEntry>> getBiomeSpawnListByMobName(
        String mobName) {
        return MobNameToBiomeSpawnList.get(mobName);
    }

    public static Set<SpawnInfo> getSpawnListByMobName(String mobName) {
        if (!MobNameToRecipeMap.containsKey(mobName)) return Collections.emptySet();
        return MobNameToRecipeMap.get(mobName).spawnList;
    }
}
