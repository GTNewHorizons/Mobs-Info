package com.kuba6000.mobsinfo.nei;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.FishingHooks;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import cpw.mods.fml.common.Loader;

public class MobHandlerFishingTest {

    private static List<WeightedRandomFishable> fish, junk, treasure;
    private static MockedStatic<Loader> forge;
    private static MockedStatic<Minecraft> client;

    @BeforeClass
    public static void setup() {
        Loader loader = mock(Loader.class);
        forge = mockStatic(Loader.class);
        forge.when(Loader::instance)
            .thenReturn(loader);
        Bootstrap.func_151354_b();
        Minecraft minecraft = mock(Minecraft.class);
        minecraft.fontRenderer = mock(FontRenderer.class);
        when(minecraft.fontRenderer.trimStringToWidth(anyString(), anyInt())).thenAnswer(call -> call.getArgument(0));
        when(minecraft.fontRenderer.trimStringToWidth(anyString(), anyInt(), anyBoolean()))
            .thenAnswer(call -> call.getArgument(0));
        client = mockStatic(Minecraft.class);
        client.when(Minecraft::getMinecraft)
            .thenReturn(minecraft);
        fish = new ArrayList<>(FishingHooks.fish);
        junk = new ArrayList<>(FishingHooks.junk);
        treasure = new ArrayList<>(FishingHooks.treasure);
        FishingHooks.fish.clear();
        FishingHooks.junk.clear();
        FishingHooks.treasure.clear();
        FishingHooks.junk.add(new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1).func_150709_a(0.25f));
        FishingHooks.junk.add(new WeightedRandomFishable(new ItemStack(Items.stick), 3));
        FishingHooks.treasure.add(new WeightedRandomFishable(new ItemStack(Items.book), 1).func_150707_a());
        FishingHooks.treasure.add(new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 3).func_150707_a());
    }

    @AfterClass
    public static void cleanup() {
        FishingHooks.fish.clear();
        FishingHooks.fish.addAll(fish);
        FishingHooks.junk.clear();
        FishingHooks.junk.addAll(junk);
        FishingHooks.treasure.clear();
        FishingHooks.treasure.addAll(treasure);
        forge.close();
        client.close();
    }

    @Test
    public void displaysActualFishingOutputs() {
        MobHandlerFishing handler = new MobHandlerFishing();
        handler.loadCraftingRecipes(handler.getOverlayIdentifier());
        List<PositionedStack> outputs = handler.getOtherStacks(0);
        assertSame(Items.enchanted_book, outputs.get(2).item.getItem());
        assertTrue(outputs.get(0).item.getItemDamage() > 0);
        assertTrue(outputs.get(3).item.isItemEnchanted());
        for (int frame = 0; frame < 10; frame++) {
            for (PositionedStack output : outputs) output.setPermutationToRender(0);
            assertSame(Items.enchanted_book, outputs.get(2).item.getItem());
            assertTrue(outputs.get(0).item.getItemDamage() > 0);
            assertTrue(outputs.get(3).item.isItemEnchanted());
        }
    }

    @Test
    public void findsRandomFishingVariantsWithoutAdvertisingPlainBooks() {
        MobHandlerFishing book = new MobHandlerFishing();
        book.loadCraftingRecipes(new ItemStack(Items.enchanted_book));
        assertEquals(1, book.numRecipes());
        MobHandlerFishing damaged = new MobHandlerFishing();
        damaged.loadCraftingRecipes(new ItemStack(Items.fishing_rod, 1, 12));
        assertEquals(1, damaged.numRecipes());
        MobHandlerFishing plain = new MobHandlerFishing();
        plain.loadCraftingRecipes(new ItemStack(Items.book));
        assertEquals(0, plain.numRecipes());
    }

    @Test
    public void tooltipUsesHoveredSlotAndWholeCatchProbability() {
        MobHandlerFishing handler = new MobHandlerFishing();
        handler.loadCraftingRecipes(handler.getOverlayIdentifier());
        List<PositionedStack> outputs = handler.getOtherStacks(0);
        GuiRecipe<?> gui = mock(GuiRecipe.class);
        when(gui.isMouseOver(outputs.get(3), 0)).thenReturn(true);
        try (MockedStatic<StatCollector> translations = mockStatic(StatCollector.class)) {
            translations.when(() -> StatCollector.translateToLocal(anyString()))
                .thenReturn("%s");
            List<String> tooltip = handler
                .handleItemTooltip(gui, new ItemStack(Items.fishing_rod), new ArrayList<>(), 0);
            // Treasure is 5% of catches; this entry has three of the four tickets.
            assertEquals(String.format("%.2f", 3.75), tooltip.get(0));
            when(gui.isMouseOver(outputs.get(3), 0)).thenReturn(false);
            when(gui.isMouseOver(outputs.get(0), 0)).thenReturn(true);
            tooltip = handler.handleItemTooltip(gui, new ItemStack(Items.fishing_rod), new ArrayList<>(), 0);
            // Junk is 10% of catches; this entry has one of the four tickets.
            assertEquals(String.format("%.2f", 2.5), tooltip.get(0));
            when(gui.isMouseOver(outputs.get(0), 0)).thenReturn(false);
            assertTrue(
                handler.handleItemTooltip(gui, new ItemStack(Items.fishing_rod), new ArrayList<>(), 0)
                    .isEmpty());
        }
    }

    @Test
    public void emptyFishCategoryDoesNotLeaveAGapBeforeTheFirstLootRow() {
        MobHandlerFishing handler = new MobHandlerFishing();
        handler.loadCraftingRecipes(handler.getOverlayIdentifier());
        List<PositionedStack> outputs = handler.getOtherStacks(0);
        // The visible first category header is at y=75, with its item row at y=87.
        assertEquals(87, outputs.get(0).rely);
        assertEquals(outputs.get(0).rely, outputs.get(1).rely);
        assertEquals(outputs.get(0).rely + 30, outputs.get(2).rely);
        assertTrue(handler.getRecipeHeight(0) >= outputs.get(3).rely + 16);
    }
}
