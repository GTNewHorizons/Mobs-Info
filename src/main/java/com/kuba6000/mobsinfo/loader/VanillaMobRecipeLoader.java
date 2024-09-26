package com.kuba6000.mobsinfo.loader;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.mixin.early.minecraft.EntitySlimeAccessor;

public class VanillaMobRecipeLoader {

    public static HashMap<String, MobRecipeLoader.GeneralMappedMob> vanillaMobList = new HashMap<>();

    public static void init() {
        World world = new DummyWorld();
        world.isRemote = true;
        {
            EntityLiving entity = new EntityDragon(world);
            String entityString = "EnderDragon";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityWither(world);
            String entityString = "WitherBoss";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(MobDrop.create(Items.nether_star));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityBlaze(world);
            String entityString = "Blaze";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.blaze_rod)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 1))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityCaveSpider(world);
            String entityString = "CaveSpider";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.string)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.spider_eye)
                    .withChance(0.3333d)
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO music discs
            EntityLiving entity = new EntityCreeper(world);
            String entityString = "Creeper";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.gunpowder)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityEnderman(world);
            String entityString = "Enderman";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.ender_pearl)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 1))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityGhast(world);
            String entityString = "Ghast";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.ghast_tear)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 1))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.gunpowder)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityGiantZombie(world);
            String entityString = "Giant";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityIronGolem(world);
            String entityString = "VillagerGolem";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Item.getItemFromBlock(Blocks.red_flower))
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2)));
            drops.add(
                MobDrop.create(Items.iron_ingot)
                    .withChance(MobDrop.getChanceBasedOnFromTo(3, 5)));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityMagmaCube entity = new EntityMagmaCube(world);
            ((EntitySlimeAccessor) entity).callSetSlimeSize(2);
            String entityString = "LavaSlime";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.magma_cream)
                    .withChance(0.25d)
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityPigZombie(world);
            String entityString = "PigZombie";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.rotten_flesh)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 1))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.gold_nugget)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 1))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.gold_ingot)
                    .withType(MobDrop.DropType.Rare)
                    .withChance(0.025d));

            int mindamage = 26;
            int maxdamage = Items.golden_sword.getMaxDamage() - 25;
            if (mindamage > maxdamage) mindamage = maxdamage;
            drops.add(
                MobDrop.create(Items.golden_sword)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.085d * 0.25d)
                    .withRandomEnchant(14)
                    .withRandomDamage(mindamage, maxdamage));
            drops.add(
                MobDrop.create(Items.golden_sword)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.085d * 0.75d)
                    .withRandomDamage(mindamage, maxdamage));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntitySilverfish(world);
            String entityString = "Silverfish";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntitySkeleton(world);
            String entityString = "Skeleton";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.arrow)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.bone)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            int mindamage = 26;
            int maxdamage = Items.bow.getMaxDamage() - 25;
            if (mindamage > maxdamage) mindamage = maxdamage;
            drops.add(
                MobDrop.create(Items.bow)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.085d * 0.25d)
                    .withRandomEnchant(14)
                    .withRandomDamage(mindamage, maxdamage));
            drops.add(
                MobDrop.create(Items.bow)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.085d * 0.75d)
                    .withRandomDamage(mindamage, maxdamage));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntitySkeleton entity = new EntitySkeleton(world);
            entity.setSkeletonType(1);
            String entityString = "witherSkeleton";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.coal)
                    .withChance(0.3333d)
                    .withLooting());
            drops.add(
                MobDrop.create(Items.bone)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(new ItemStack(Items.skull, 1, 1))
                    .withType(MobDrop.DropType.Rare)
                    .withChance(0.025d));
            int mindamage = 26;
            int maxdamage = Items.stone_sword.getMaxDamage() - 25;
            if (mindamage > maxdamage) mindamage = maxdamage;
            drops.add(
                MobDrop.create(Items.stone_sword)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.085d)
                    .withRandomDamage(mindamage, maxdamage));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntitySlime entity = new EntitySlime(world);
            ((EntitySlimeAccessor) entity).callSetSlimeSize(1);
            String entityString = "Slime";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.slime_ball)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntitySnowman(world);
            String entityString = "SnowMan";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.snowball)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 15)));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntitySpider(world);
            String entityString = "Spider";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.string)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.spider_eye)
                    .withChance(0.3333d)
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityWitch(world);
            String entityString = "Witch";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            final Item[] witchDrops = new Item[] { Items.glowstone_dust, Items.sugar, Items.redstone, Items.spider_eye,
                Items.glass_bottle, Items.gunpowder, Items.stick, Items.stick };
            double chance = MobDrop.getChanceBasedOnFromTo(1, 3) * MobDrop.getChanceBasedOnFromTo(0, 2)
                / witchDrops.length;
            for (int i = 0; i < witchDrops.length - 1; i++) drops.add(
                MobDrop.create(witchDrops[i])
                    .withChance(witchDrops[i] == Items.stick ? chance * 2d : chance)
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityZombie(world);
            String entityString = "Zombie";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.rotten_flesh)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            double chance = 0.025d / 3d;
            drops.add(
                MobDrop.create(Items.iron_ingot)
                    .withType(MobDrop.DropType.Rare)
                    .withChance(chance));
            drops.add(
                MobDrop.create(Items.carrot)
                    .withType(MobDrop.DropType.Rare)
                    .withChance(chance));
            drops.add(
                MobDrop.create(Items.potato)
                    .withType(MobDrop.DropType.Rare)
                    .withChance(chance));

            int mindamage = 26;
            int maxdamage = Items.iron_sword.getMaxDamage() - 25;
            if (mindamage > maxdamage) mindamage = maxdamage;
            drops.add(
                MobDrop.create(Items.iron_sword)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.05d * 0.3333d * 0.085d * 0.25d)
                    .withRandomEnchant(14)
                    .withRandomDamage(mindamage, maxdamage));
            drops.add(
                MobDrop.create(Items.iron_sword)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.05d * 0.3333d * 0.085d * 0.75d)
                    .withRandomDamage(mindamage, maxdamage));

            mindamage = 26;
            maxdamage = Items.iron_shovel.getMaxDamage() - 25;
            if (mindamage > maxdamage) mindamage = maxdamage;
            drops.add(
                MobDrop.create(Items.iron_shovel)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.05d * 0.6666d * 0.085d * 0.25d)
                    .withRandomEnchant(14)
                    .withRandomDamage(mindamage, maxdamage));
            drops.add(
                MobDrop.create(Items.iron_shovel)
                    .withType(MobDrop.DropType.Additional)
                    .withChance(0.05d * 0.6666d * 0.085d * 0.75d)
                    .withRandomDamage(mindamage, maxdamage));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityBat entity = new EntityBat(world);
            entity.setIsBatHanging(false);
            String entityString = "Bat";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO cooked chicken
            EntityLiving entity = new EntityChicken(world);
            String entityString = "Chicken";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.feather)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(MobDrop.create(Items.chicken));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO cooked beef
            EntityLiving entity = new EntityCow(world);
            String entityString = "Cow";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.leather)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.beef)
                    .withChance(MobDrop.getChanceBasedOnFromTo(1, 3))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO more horse types?
            EntityLiving entity = new EntityHorse(world);
            String entityString = "EntityHorse";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(MobDrop.create(Items.leather));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO cooked beef
            EntityLiving entity = new EntityMooshroom(world);
            String entityString = "MushroomCow";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.leather)
                    .withChance(MobDrop.getChanceBasedOnFromTo(0, 2))
                    .withLooting());
            drops.add(
                MobDrop.create(Items.beef)
                    .withChance(MobDrop.getChanceBasedOnFromTo(1, 3))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityOcelot(world);
            String entityString = "Ozelot";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO cooked porkchop
            EntityLiving entity = new EntityPig(world);
            String entityString = "Pig";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.porkchop)
                    .withChance(MobDrop.getChanceBasedOnFromTo(1, 3))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        { // TODO colored wool
            EntityLiving entity = new EntitySheep(world);
            String entityString = "Sheep";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(MobDrop.create(Item.getItemFromBlock(Blocks.wool)));

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntitySquid(world);
            String entityString = "Squid";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();
            drops.add(
                MobDrop.create(Items.dye)
                    .withChance(MobDrop.getChanceBasedOnFromTo(1, 3))
                    .withLooting());

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityVillager(world);
            String entityString = "Villager";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }
        {
            EntityLiving entity = new EntityWolf(world);
            String entityString = "Wolf";
            entity.captureDrops = true;
            ArrayList<MobDrop> drops = new ArrayList<>();

            vanillaMobList.put(
                entityString,
                new MobRecipeLoader.GeneralMappedMob(
                    entity,
                    MobRecipe.generateMobRecipe(entity, entityString, drops),
                    drops,
                    true));
        }

        for (MobRecipeLoader.GeneralMappedMob value : vanillaMobList.values()) {
            for (MobDrop drop : value.drops) {
                drop.clampChance();
            }
            for (MobDrop mOutput : value.recipe.mOutputs) {
                mOutput.clampChance();
            }
        }
    }

}
