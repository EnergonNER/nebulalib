package energon.nebulalib.network;

import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.StructureBase;
import energon.nebulalib.structure.spawn.DefaultStructureSpawnRules;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NLibStructureCommand extends CommandBase {
    @Override
    public String getName() {
        return "nebulalib_structures";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "nebulalib_structures <text>";
    }

    @Override
    public boolean checkPermission(MinecraftServer p_checkPermission_1_, ICommandSender sender) {
        return sender.canUseCommand(2, "nebulalib_structures");
    }

    @Override
    public List<String> getAliases() {
        return super.getAliases();
    }

    public void help(ICommandSender iCommandSender) {

    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
        if (strings.length == 0) {
            this.help(iCommandSender);
            return;
        }
        switch (strings[0]) {
            case "help":
                this.help(iCommandSender);
                break;
            case "info":

                break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] strings, @Nullable BlockPos pos) {
        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("help");
            tab.add("info");
            tab.add("debug");
            tab.add("generator");
            tab.add("structure");
            tab.add("template");
            return tab;
        }
        switch (strings[0]) {
            case "structure":
                if (strings.length == 2) {
                    for (StructureBase base : NLibStructureHandler.LIST_STRUCTURES) {
                        tab.add(base.name);
                    }
                    break;
                } else if (strings.length == 3) {
                    tab.add("info");
                    tab.add("remove");
                    tab.add("spawn");
                    break;
                }
                switch (strings[2]) {
                    case "info":
                    case "remove":
                        break;
                    case "spawn":
                        if (strings.length == 4) {
                            tab.add("auto");
                            tab.add("pos");
                            break;
                        }
                        switch (strings[3]) {
                            case "auto":
                                switch (strings.length) {
                                    case 5:
                                        for (Integer key : DefaultStructureSpawnRules.STRUCTURE_SPAWN_RULES.keySet()) {
                                            tab.add(key + "");
                                        }
                                        break;
                                    case 6:
                                        tab.add(sender.getPosition().getX() + "");
                                        break;
                                    case 7:
                                        tab.add(sender.getPosition().getZ() + "");
                                        break;
                                    case 8:
                                        tab.add("rotate_0");
                                        tab.add("rotate_90");
                                        tab.add("rotate_180");
                                        tab.add("rotate_270");
                                        break;
                                }
                                break;
                            case "pos":
                                switch (strings.length) {
                                    case 5:
                                        tab.add(sender.getPosition().getX() + "");
                                        break;
                                    case 6:
                                        tab.add(sender.getPosition().getY() + "");
                                        break;
                                    case 7:
                                        tab.add(sender.getPosition().getZ() + "");
                                        break;
                                    case 8:
                                        tab.add("rotate_0");
                                        tab.add("rotate_90");
                                        tab.add("rotate_180");
                                        tab.add("rotate_270");
                                        break;
                                }
                                break;
                        }
                        break;
                }
        }
        return tab;
    }
}
