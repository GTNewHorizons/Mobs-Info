package com.kuba6000.mobsinfo.loader.extras;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import chococraft.common.config.GeneralConfig;
import chococraft.common.registry.ChocoCraftItems;

public class ChocoCraft implements IExtraLoader {

    @Override
    public void process(String k, ArrayList<MobDrop> drops, MobRecipe recipe) {
        String entityClassPath = recipe.entity.getClass()
            .toString()
            .substring(6);

        ArrayList<String> classPaths = (ArrayList) GeneralConfig.unpackMobDrops(GeneralConfig.mobDrops)[0];
        ArrayList<String> chocoItems = (ArrayList) GeneralConfig.unpackMobDrops(GeneralConfig.mobDrops)[1];
        ArrayList<Integer> classdropChances = (ArrayList) GeneralConfig.unpackMobDrops(GeneralConfig.mobDrops)[2];

        for (int i = 0; i < classPaths.size(); i++) {
            if (classPaths.get(i)
                .equals(entityClassPath)) {
                int chance = (int) ((classdropChances.get(i) / 256d) * 10000d);
                String chocoItem = chocoItems.get(i);

                switch (chocoItem.toLowerCase()) {
                    case "gysahl":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.GYSAHL_CARROT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "krakka":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.KRAKKA_ROOT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "tantal":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.TANTAL_VEGGIE),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "pasana":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.PASANA_FRUIT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "cree":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CREE_ROOT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "reagan":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.REAGAN_VEGGIE),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "mimett":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.MIMETT_FRUIT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "sylkis":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.SYLKIS_BUD),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "pipio":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.PIPIO_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "luchile":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.LUCHILE_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "saraha":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.SARAHA_BEAN),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "lasan":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.LASAN_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "param":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.PARAM_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "porov":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.POROV_BEAN),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "carob":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CAROB_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "zeio":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.ZEIO_NUT),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "gysalseeds":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.GYSAHL_GARDEN_SEEDS),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "chocopedia":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CHOCOPEDIA),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "chocosaddle":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CHOCOBO_SADDLE),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "saddlebags":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CHOCOBO_SADDLE_BAGS),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                    case "packbags":
                        drops.add(
                            new MobDrop(
                                new ItemStack(ChocoCraftItems.CHOCOBO_PACK_BAGS),
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                false));
                        break;
                }
            }
        }
    }

}
