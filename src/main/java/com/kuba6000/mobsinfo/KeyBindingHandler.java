package com.kuba6000.mobsinfo;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.kuba6000.mobsinfo.api.LoaderReference;
import com.kuba6000.mobsinfo.config.Config;
import com.kuba6000.mobsinfo.nei.MobHandler;
import com.kuba6000.mobsinfo.nei.MobHandlerFishing;
import com.kuba6000.mobsinfo.nei.MobHandlerInfernal;
import com.kuba6000.mobsinfo.nei.VillagerTradesHandler;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler {

    public static KeyBindingHandler INSTANCE = new KeyBindingHandler();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final KeyBinding openGui = new KeyBinding("Open Mobs Info GUI", Keyboard.KEY_NONE, "Mobs Info");

    public static void init() {
        ClientRegistry.registerKeyBinding(openGui);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent evt) {
        if (mc.thePlayer != null && mc.theWorld != null && openGui.isPressed()) {
            ArrayList<TemplateRecipeHandler> handlers = new ArrayList<>();
            handlers.add(new MobHandler().addAllRecipes());
            if (LoaderReference.InfernalMobs.isLoaded && Config.Compatibility.enableMobHandlerInfernal)
                handlers.add(new MobHandlerInfernal().addAllRecipes());
            handlers.add(new VillagerTradesHandler().addAllRecipes());
            if (Config.FishingHandler.enabled) handlers.add(new MobHandlerFishing().addAllRecipes());

            GuiCraftingRecipe gui = null;
            for (TemplateRecipeHandler handler : handlers) {
                if (handler.numRecipes() == 0) continue;
                if (gui == null) {
                    gui = (GuiCraftingRecipe) GuiCraftingRecipe.createRecipeGui(handler.getOverlayIdentifier(), false);
                } else gui.currenthandlers.add(handler);
            }
            if (gui != null) mc.displayGuiScreen(gui);
        }
    }

}
