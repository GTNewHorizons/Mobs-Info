package com.kuba6000.mobsinfo.api;

import cpw.mods.fml.common.Loader;

public enum LoaderReference {

    // replace all using
    // (LoaderReference\.[A-Z]\w+)
    // $1.isLoaded
    EnderIO("EnderIO"),
    InfernalMobs("InfernalMobs"),
    Thaumcraft("Thaumcraft"),
    MineTweaker("MineTweaker3"),
    TwilightForest("TwilightForest"),
    BetterLoadingScreen("betterloadingscreen"),
    DraconicEvolution("DraconicEvolution"),
    TinkersConstruct("TConstruct"),
    Witchery("witchery"),
    ThaumicHorizons("ThaumicHorizons"),
    ThaumicBases("thaumicbases"),
    WirelessCraftingTerminal("ae2wct"),
    CoFHCore("CoFHCore"),
    HardcoreEnderExpansion("HardcoreEnderExpansion"),
    Botania("Botania"),
    HarvestCraft("harvestcraft"),
    OpenBlocks("OpenBlocks"),
    BloodArsenal("BloodArsenal"),
    BloodMagic("AWWayofTime"),
    Avaritia("Avaritia"),
    ThaumicTinkerer("ThaumicTinkerer"),
    ForbiddenMagic("ForbiddenMagic"),
    ElectroMagicTools("EMT"),
    WitchingGadgets("WitchingGadgets"),
    Automagy("Automagy"),
    GTPlusPlus("miscutils"),
    Gregtech5("gregtech", "gregapi"),
    DQRespect("DQMIIINext"),
    EditMobDrops("editmobdrops"),
    ChocoCraft("chococraft"),
    ExtraUtilities("ExtraUtilities"),
    EtFuturumRequiem("etfuturum"),
    LycanitesMobs("lycanitesmobs"),
    JustAnotherSpawner("JustAnotherSpawner"),
    Reliquarry("xreliquary"),;

    public final String modID;
    public final boolean isLoaded;

    LoaderReference(String modID) {
        this.modID = modID;
        this.isLoaded = Loader.isModLoaded(this.modID);
    }

    LoaderReference(String modID, String notModID) {
        this.modID = modID;
        this.isLoaded = Loader.isModLoaded(this.modID) && !Loader.isModLoaded(notModID);
    }

}
