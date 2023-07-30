package com.kuba6000.mobsinfo.command;

import static com.kuba6000.mobsinfo.command.CommandHidden.Translations.INVALID;
import static com.kuba6000.mobsinfo.command.CommandHidden.Translations.INVALID_MOB;
import static com.kuba6000.mobsinfo.command.CommandHidden.Translations.LOCK_ERROR;
import static com.kuba6000.mobsinfo.command.CommandHidden.Translations.SUCCESS;
import static com.kuba6000.mobsinfo.command.CommandHidden.Translations.USAGE;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import com.kuba6000.mobsinfo.MobsInfo;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.network.SaveDataPacket;
import com.kuba6000.mobsinfo.savedata.PlayerData;
import com.kuba6000.mobsinfo.savedata.PlayerDataManager;

public class CommandHidden extends CommandBase {

    enum Translations {

        INVALID,
        USAGE,
        SUCCESS,
        INVALID_MOB,
        LOCK_ERROR;

        final String key;

        Translations() {
            key = "mobsinfo.command.hidden." + this.name()
                .toLowerCase();
        }

        public String get() {
            return StatCollector.translateToLocal(key);
        }

        public String get(Object... args) {
            return StatCollector.translateToLocalFormatted(key, args);
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return get();
        }
    }

    @Override
    public String getCommandName() {
        return "hidden";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "hidden " + USAGE.get();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(INVALID.get(USAGE.get())));
            return;
        }
        EntityPlayerMP player = getPlayer(sender, args[0]);
        PlayerData pData = PlayerDataManager.getPlayer(player);
        switch (args[1]) {
            case "unlock": {
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(INVALID.get(USAGE.get())));
                    return;
                }
                String entityName = args[2];
                if (MobRecipe.getRecipeByEntityName(entityName) == null) {
                    sender.addChatMessage(new ChatComponentText(INVALID_MOB.get()));
                    return;
                }
                pData.killedMobs.add(entityName);
                pData.markDirty();
                sender.addChatMessage(new ChatComponentText(SUCCESS.get()));
                MobsInfo.NETWORK.sendTo(new SaveDataPacket(pData), player);
                break;
            }
            case "lock": {
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(INVALID.get(USAGE.get())));
                    return;
                }
                String entityName = args[2];
                if (pData.killedMobs.remove(entityName)) {
                    sender.addChatMessage(new ChatComponentText(SUCCESS.get()));
                    pData.markDirty();
                    MobsInfo.NETWORK.sendTo(new SaveDataPacket(pData), player);
                } else {
                    sender.addChatMessage(new ChatComponentText(LOCK_ERROR.get()));
                }
                break;
            }
            case "reset": {
                pData.killedMobs.clear();
                pData.markDirty();
                MobsInfo.NETWORK.sendTo(new SaveDataPacket(pData), player);
                sender.addChatMessage(new ChatComponentText(SUCCESS.get()));
                break;
            }
            case "unlockall": {
                pData.killedMobs.addAll(MobRecipe.MobNameToRecipeMap.keySet());
                pData.markDirty();
                MobsInfo.NETWORK.sendTo(new SaveDataPacket(pData), player);
                sender.addChatMessage(new ChatComponentText(SUCCESS.get()));
                break;
            }
        }
    }
}
