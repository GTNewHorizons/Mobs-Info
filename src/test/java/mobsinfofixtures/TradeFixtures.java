package mobsinfofixtures;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public final class TradeFixtures {

    public static final Item INPUT = new Item();
    public static final Item OUTPUT = new Item();

    public static class FloatOffer implements IVillageTradeHandler {

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList list, Random random) {
            if (random.nextFloat() < 0.025f) list.add(new MerchantRecipe(new ItemStack(INPUT), new ItemStack(OUTPUT)));
        }
    }

    public static class VeryRareOffer implements IVillageTradeHandler {

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList list, Random random) {
            if (random.nextInt(100_000_000) == 0)
                list.add(new MerchantRecipe(new ItemStack(INPUT), new ItemStack(OUTPUT)));
        }
    }

    public static class Prices implements IVillageTradeHandler {

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList list, Random random) {
            if (random.nextInt(10) >= 2) return;
            ItemStack first = new ItemStack(INPUT, 1 + random.nextInt(3));
            ItemStack second = random.nextBoolean() ? new ItemStack(INPUT, 1 + random.nextInt(2), 1) : null;
            list.add(new MerchantRecipe(first, second, new ItemStack(OUTPUT, 1 + random.nextInt(2))));
        }
    }

    public static class MutableOffer implements IVillageTradeHandler {

        private final ItemStack input = new ItemStack(INPUT);
        private final ItemStack output = new ItemStack(OUTPUT);

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList list, Random random) {
            input.stackSize = random.nextInt(3) + 1;
            output.stackSize = random.nextInt(2) + 1;
            list.add(new MerchantRecipe(input, output));
        }
    }

    public static class Comparisons implements IVillageTradeHandler {

        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList list, Random random) {
            if (random.nextInt(20) == 0)
                list.add(new MerchantRecipe(new ItemStack(INPUT), new ItemStack(OUTPUT, 1, 0)));
            if (random.nextInt(20) < 3) list.add(new MerchantRecipe(new ItemStack(INPUT), new ItemStack(OUTPUT, 1, 1)));
            if (random.nextInt(20) >= 18)
                list.add(new MerchantRecipe(new ItemStack(INPUT), new ItemStack(OUTPUT, 1, 2)));
        }
    }
}
