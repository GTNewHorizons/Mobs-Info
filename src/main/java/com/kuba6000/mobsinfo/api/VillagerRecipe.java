package com.kuba6000.mobsinfo.api;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.passive.EntityVillager;

public class VillagerRecipe {

    public static HashMap<Integer, VillagerRecipe> recipes = new HashMap<>();

    public final List<VillagerTrade> trades;
    public final int profession;
    public final EntityVillager mob;

    public VillagerRecipe(List<VillagerTrade> trades, int profession, EntityVillager villager) {
        this.trades = trades;
        this.profession = profession;
        this.mob = villager;
    }

}
