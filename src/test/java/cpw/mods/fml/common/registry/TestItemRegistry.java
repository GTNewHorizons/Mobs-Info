package cpw.mods.fml.common.registry;

import java.util.BitSet;

import net.minecraft.item.Item;

/** Minimal Forge registry setup for handler tests, without booting FML's LaunchWrapper lifecycle. */
public final class TestItemRegistry {

    private static final BitSet IDS = new BitSet();

    private TestItemRegistry() {}

    public static void register(int id, String name, Item item) {
        GameData.getItemRegistry()
            .add(id, name, item, IDS);
    }
}
