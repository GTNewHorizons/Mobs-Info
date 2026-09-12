package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.is.mtc.handler.DropHandler;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

public class MineTradingCards implements IExtraLoader {

    private static final Logger LOG = LogManager.getLogger("mobsinfo[Mine Trading Cards]");
    private static final List<String> DROP_NAMES = Arrays.asList(
        "common_card",
        "uncommon_card",
        "rare_card",
        "ancient_card",
        "legendary_card",
        "common_pack",
        "uncommon_pack",
        "rare_pack",
        "ancient_pack",
        "legendary_pack",
        "standard_pack",
        "edition_pack",
        "custom_pack");

    @Override
    public void process(String name, ArrayList<MobDrop> drops, MobRecipe recipe) {
        // com.is.mtc.handler.DropHandler.onEvent (MTC 3.1.4).
        boolean cards = recipe.entity instanceof EntityMob ? DropHandler.CAN_DROP_CARDS_MOB
            : recipe.entity instanceof EntityAnimal && DropHandler.CAN_DROP_CARDS_ANIMAL;
        boolean packs = recipe.entity instanceof EntityMob ? DropHandler.CAN_DROP_PACKS_MOB
            : recipe.entity instanceof EntityAnimal && DropHandler.CAN_DROP_PACKS_ANIMAL;
        Item[] items = { com.is.mtc.MineTradingCards.cardCommon, com.is.mtc.MineTradingCards.cardUncommon,
            com.is.mtc.MineTradingCards.cardRare, com.is.mtc.MineTradingCards.cardAncient,
            com.is.mtc.MineTradingCards.cardLegendary, com.is.mtc.MineTradingCards.packCommon,
            com.is.mtc.MineTradingCards.packUncommon, com.is.mtc.MineTradingCards.packRare,
            com.is.mtc.MineTradingCards.packAncient, com.is.mtc.MineTradingCards.packLegendary,
            com.is.mtc.MineTradingCards.packStandard, com.is.mtc.MineTradingCards.packEdition,
            com.is.mtc.MineTradingCards.packCustom };
        float[] rates = { DropHandler.CARD_DROP_RATE_COM, DropHandler.CARD_DROP_RATE_UNC,
            DropHandler.CARD_DROP_RATE_RAR, DropHandler.CARD_DROP_RATE_ANC, DropHandler.CARD_DROP_RATE_LEG,
            DropHandler.PACK_DROP_RATE_COM, DropHandler.PACK_DROP_RATE_UNC, DropHandler.PACK_DROP_RATE_RAR,
            DropHandler.PACK_DROP_RATE_ANC, DropHandler.PACK_DROP_RATE_LEG, DropHandler.PACK_DROP_RATE_STD,
            DropHandler.PACK_DROP_RATE_EDT, DropHandler.PACK_DROP_RATE_CUS };
        double[] weights = new double[rates.length];
        double[] chances = new double[rates.length];
        for (int i = 0; i < rates.length; i++) {
            if ((i < 5 ? cards : packs) && rates[i] > 0) weights[i] = 1d / rates[i];
            chances[i] = Math.min(1d, weights[i]);
        }
        if (DropHandler.ONLY_ONE_DROP) {
            chances = new double[rates.length];
            accumulateSingleDrop(weights, 0, 0, 1d, 0d, chances);
        }
        // Like the normal recipe generator, MobDrop represents expected item counts.
        // Boss additions happen after ONLY_ONE_DROP and ignore the ordinary drop switches.
        if (recipe.entity instanceof EntityDragon) addBossAmounts(DropHandler.ENDER_DRAGON_DROPS, chances);
        else if (recipe.entity instanceof IBossDisplayData) addBossAmounts(DropHandler.BOSS_DROPS, chances);
        for (int i = 0; i < items.length; i++) {
            if (chances[i] > 0 && items[i] != null) {
                MobDrop drop = MobDrop.create(items[i])
                    .withChance(chances[i]);
                drop.clampChance();
                drops.add(drop);
            }
        }
    }

    private static void addBossAmounts(String[] entries, double[] amounts) {
        for (String entry : entries) {
            try {
                String[] parts = entry.toLowerCase()
                    .trim()
                    .split(":");
                int index = DROP_NAMES.indexOf(parts[0].trim());
                float amount = MathHelper.clamp_float(Float.parseFloat(parts[1].trim()), 0f, 64f);
                if (index < 0) throw new IllegalArgumentException("Unknown drop");
                if (!Float.isNaN(amount)) amounts[index] += amount;
            } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                LOG.warn("Skipping malformed Mine Trading Cards boss drop: {}", entry);
            }
        }
    }

    private static void accumulateSingleDrop(double[] weights, int index, int selected, double probability,
        double totalWeight, double[] chances) {
        if (index == weights.length) {
            if (totalWeight == 0) return;
            for (int i = 0; i < weights.length; i++) {
                if ((selected & (1 << i)) != 0) chances[i] += probability * weights[i] / totalWeight;
            }
            return;
        }
        // At most 2^13 subsets. MTC first rolls independently, then weights only the successful drops.
        double chance = Math.min(1d, weights[index]);
        if (chance < 1d)
            accumulateSingleDrop(weights, index + 1, selected, probability * (1d - chance), totalWeight, chances);
        if (chance > 0d) accumulateSingleDrop(
            weights,
            index + 1,
            selected | (1 << index),
            probability * chance,
            totalWeight + weights[index],
            chances);
    }
}
