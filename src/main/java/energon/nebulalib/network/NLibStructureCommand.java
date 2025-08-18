package energon.nebulalib.network;

import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.StructureBase;
import energon.nebulalib.structure.StructureGeneratorBase;
import energon.nebulalib.structure.TemplateElement;
import energon.nebulalib.structure.spawn.DefaultStructureSpawnRules;
import energon.nebulalib.util.NLibStructureUtilities;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.io.File;
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
            case "reset":
                NLibStructureHandler.LIST_TEMPLATES.clear();
                NLibStructureHandler.LIST_STRUCTURES.clear();
                NLibStructureHandler.LIST_GENERATORS.clear();
                NLibStructureHandler.init();
                iCommandSender.sendMessage(new TextComponentString("Infrastructure recreated."));
                break;
            case "help":
                this.help(iCommandSender);
                break;
            case "spawn":
                if (strings.length != 2) {
                    iCommandSender.sendMessage(new TextComponentString("<on,off,info>"));
                } else {
                    switch (strings[1]) {
                        case "0":
                        case "off":
                        case "false":
                            NLibStructureHandler.SPAWN = false;
                            iCommandSender.sendMessage(new TextComponentString("Structure Spawn: off"));
                            break;
                        case "1":
                        case "on":
                        case "true":
                            NLibStructureHandler.SPAWN = true;
                            iCommandSender.sendMessage(new TextComponentString("Structure Spawn: on"));
                            break;
                        case "info":
                            iCommandSender.sendMessage(new TextComponentString("Structure Spawn: " + (NLibStructureHandler.SPAWN ? "on" : "off")));
                            break;
                    }
                }
                break;
            case "list":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<generator,structure,template>"));
                    break;
                }
                switch (strings[1]) {
                    case "generator":
                        if (strings.length == 2) {
                            iCommandSender.sendMessage(new TextComponentString("<clear,info,remove,reset>"));
                        }
                        switch (strings[2]) {
                            case "info":
                                if (strings.length == 3) {
                                    for (StructureGeneratorBase base : NLibStructureHandler.LIST_GENERATORS) {
                                        iCommandSender.sendMessage(new TextComponentString(base.getInfo(false)));
                                    }
                                } else {
                                    StructureGeneratorBase base = NLibStructureHandler.getGeneratorByName(strings[3]);
                                    if (base == null) {
                                        iCommandSender.sendMessage(new TextComponentString("Generator not found."));
                                        break;
                                    }
                                    iCommandSender.sendMessage(new TextComponentString(base.getInfo(true)));
                                }
                                break;
                            case "clear":
                                NLibStructureHandler.LIST_GENERATORS.clear();
                                iCommandSender.sendMessage(new TextComponentString("Generator list cleared."));
                                break;
                            case "reset":
                                NLibStructureHandler.LIST_GENERATORS.clear();
                                NLibStructureHandler.readGenerators(new File(Loader.instance().getConfigDir(), "nebulalib/generators"));
                                iCommandSender.sendMessage(new TextComponentString("Generator list reset."));
                                break;
                            case "remove":
                                if (strings.length == 3) {
                                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                                    break;
                                }
                                StructureGeneratorBase generatorBase = NLibStructureHandler.getGeneratorByName(strings[3]);
                                if (generatorBase != null) {
                                    NLibStructureHandler.LIST_GENERATORS.remove(generatorBase);
                                    iCommandSender.sendMessage(new TextComponentString("Generator: " + generatorBase.name + "  removed from the list."));
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Generator not found."));
                                }
                                break;
                        }
                        break;
                    case "structure":
                        if (strings.length == 2) {
                            iCommandSender.sendMessage(new TextComponentString("<clear,info,remove,reset>"));
                        }
                        switch (strings[2]) {
                            case "info":
                                if (strings.length == 3) {
                                    for (StructureBase base : NLibStructureHandler.LIST_STRUCTURES) {
                                        iCommandSender.sendMessage(new TextComponentString(base.getInfo(false)));
                                    }
                                } else {
                                    StructureBase base = NLibStructureHandler.getStructureByName(strings[3]);
                                    if (base == null) {
                                        iCommandSender.sendMessage(new TextComponentString("Structure not found."));
                                        break;
                                    }
                                    iCommandSender.sendMessage(new TextComponentString(base.getInfo(true)));
                                }
                                break;
                            case "clear":
                                NLibStructureHandler.LIST_STRUCTURES.clear();
                                iCommandSender.sendMessage(new TextComponentString("Structure list cleared."));
                                break;
                            case "reset":
                                NLibStructureHandler.LIST_STRUCTURES.clear();
                                NLibStructureHandler.readStructures(new File(Loader.instance().getConfigDir(), "nebulalib/structures"));
                                iCommandSender.sendMessage(new TextComponentString("Structure list reset."));
                                break;
                            case "remove":
                                if (strings.length == 3) {
                                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                                    break;
                                }
                                StructureBase base = NLibStructureHandler.getStructureByName(strings[3]);
                                if (base != null) {
                                    NLibStructureHandler.LIST_STRUCTURES.remove(base);
                                    iCommandSender.sendMessage(new TextComponentString("Structure: " + base.name + "  removed from the list."));
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Structure not found."));
                                }
                                break;
                        }
                        break;
                    case "template":
                        if (strings.length == 2) {
                            iCommandSender.sendMessage(new TextComponentString("<clear,info,remove,reset>"));
                        }
                        switch (strings[2]) {
                            case "info":
                                if (strings.length == 3) {
                                    for (TemplateElement element : NLibStructureHandler.LIST_TEMPLATES) {
                                        iCommandSender.sendMessage(new TextComponentString(element.getInfo(false)));
                                    }
                                } else {
                                    TemplateElement element = NLibStructureHandler.getTemplateByName(strings[3]);
                                    if (element == null) {
                                        iCommandSender.sendMessage(new TextComponentString("Template not found."));
                                        break;
                                    }
                                    iCommandSender.sendMessage(new TextComponentString(element.getInfo(true)));
                                }
                                break;
                            case "clear":
                                NLibStructureHandler.LIST_TEMPLATES.clear();
                                iCommandSender.sendMessage(new TextComponentString("Template list cleared."));
                                break;
                            case "reset":
                                NLibStructureHandler.LIST_TEMPLATES.clear();
                                NLibStructureHandler.readTemplate(new File(Loader.instance().getConfigDir(), "nebulalib/template"));
                                iCommandSender.sendMessage(new TextComponentString("Template list reset."));
                                break;
                            case "remove":
                                if (strings.length == 3) {
                                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                                    break;
                                }
                                TemplateElement element = NLibStructureHandler.getTemplateByName(strings[3]);
                                if (element != null) {
                                    NLibStructureHandler.LIST_TEMPLATES.remove(element);
                                    iCommandSender.sendMessage(new TextComponentString("Template: " + element.name + "  removed from the list."));
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Template not found."));
                                }
                                break;
                        }
                        break;
                }
                break;
            case "debug":
                if (strings.length != 2) {
                    iCommandSender.sendMessage(new TextComponentString("<on,off,info>"));
                } else {
                    switch (strings[1]) {
                        case "0":
                        case "off":
                        case "false":
                            NLibStructureHandler.DEBUG = false;
                            iCommandSender.sendMessage(new TextComponentString("Debug: off"));
                            break;
                        case "1":
                        case "on":
                        case "true":
                            NLibStructureHandler.DEBUG = true;
                            iCommandSender.sendMessage(new TextComponentString("Debug: on"));
                            break;
                        case "info":
                            iCommandSender.sendMessage(new TextComponentString("Debug: " + (NLibStructureHandler.DEBUG ? "on" : "off")));
                            break;
                    }
                }
                break;
            case "structure":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                    break;
                }
                StructureBase base = NLibStructureHandler.getStructureByName(strings[1]);
                if (base == null) {
                    iCommandSender.sendMessage(new TextComponentString("Structure not found."));
                    break;
                }
                if (strings.length == 2) {
                    iCommandSender.sendMessage(new TextComponentString("<info,remove,spawn>"));
                    break;
                }
                switch (strings[2]) {
                    case "info":
                        iCommandSender.sendMessage(new TextComponentString(base.getInfo(true)));
                        break;
                    case "remove":
                        if (NLibStructureHandler.LIST_STRUCTURES.removeIf((structureBase -> structureBase.name.equals(base.name)))) {
                            iCommandSender.sendMessage(new TextComponentString("Structure removed from the list."));
                        } else {
                            iCommandSender.sendMessage(new TextComponentString("Structure not found."));
                        }
                        break;
                    case "spawn":
                        if (strings.length == 3) {
                            iCommandSender.sendMessage(new TextComponentString("<auto,pos>"));
                            break;
                        }
                        switch (strings[3]) {
                            case "auto":
                                if (strings.length == 4) {
                                    iCommandSender.sendMessage(new TextComponentString("<spawn_type_id>"));
                                    break;
                                }
                                int spawnType = CommandBase.parseInt(strings[4]);
                                if (strings.length == 5) {
                                    iCommandSender.sendMessage(new TextComponentString("<x>"));
                                    break;
                                }
                                int posX = CommandBase.parseInt(strings[5]);
                                if (strings.length == 6) {
                                    iCommandSender.sendMessage(new TextComponentString("<z>"));
                                    break;
                                }
                                int posZ = CommandBase.parseInt(strings[6]);
                                Rotation rotation = base.defRotation.add(strings.length > 7 ? NLibStructureUtilities.getRotationByName(strings[7]) : Rotation.NONE);
                                BlockPos pos = DefaultStructureSpawnRules.getSpawnPos(iCommandSender.getEntityWorld(), new BlockPos(posX, 0, posZ), spawnType, base);
                                if (pos != null) {
                                    if (((strings.length > 8 && strings[8].equals("true")) || base.canStartSearch(iCommandSender.getEntityWorld(), pos))) {
                                        if (base.generate(iCommandSender.getEntityWorld(), pos, rotation)) {
                                            iCommandSender.sendMessage(new TextComponentString("Structure placed."));
                                            if (!(strings.length > 9 && strings[9].equals("true"))) {
                                                base.runAfterFunctions(iCommandSender.getEntityWorld(), pos, rotation);
                                                iCommandSender.sendMessage(new TextComponentString("Structure generation finished."));
                                            }
                                        } else {
                                            iCommandSender.sendMessage(new TextComponentString("Structure generation failed."));
                                        }
                                    } else {
                                        iCommandSender.sendMessage(new TextComponentString("Structure cannot be placed here, position does not match structure rules."));
                                    }
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Structure cannot be placed here, no suitable spawn position was found."));
                                }
                                break;
                            case "pos":
                                if (strings.length == 4) {
                                    iCommandSender.sendMessage(new TextComponentString("<x>"));
                                    break;
                                }
                                int posX_ = CommandBase.parseInt(strings[4]);
                                if (strings.length == 5) {
                                    iCommandSender.sendMessage(new TextComponentString("<y>"));
                                    break;
                                }
                                int posY_ = CommandBase.parseInt(strings[5]);
                                if (strings.length == 6) {
                                    iCommandSender.sendMessage(new TextComponentString("<z>"));
                                    break;
                                }
                                int posZ_ = CommandBase.parseInt(strings[6]);
                                Rotation rotation_ = base.defRotation.add(strings.length > 7 ? NLibStructureUtilities.getRotationByName(strings[7]) : Rotation.NONE);
                                BlockPos pos_ = new BlockPos(posX_, posY_, posZ_);
                                if ((strings.length > 8 && strings[8].equals("true")) || base.canStartSearch(iCommandSender.getEntityWorld(), pos_)) {
                                    if (base.generate(iCommandSender.getEntityWorld(), pos_, rotation_)) {
                                        iCommandSender.sendMessage(new TextComponentString("Structure placed."));
                                        if (!(strings.length > 9 && strings[9].equals("false"))) {
                                            base.runAfterFunctions(iCommandSender.getEntityWorld(), pos_, rotation_);
                                            iCommandSender.sendMessage(new TextComponentString("Structure generation finished."));
                                        }
                                    } else {
                                        iCommandSender.sendMessage(new TextComponentString("Structure generation failed."));
                                    }
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Structure cannot be placed here, position does not match structure rules."));
                                }
                                break;
                        }
                        break;
                }
                break;
            case "template":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                    break;
                }
                TemplateElement templateElement = NLibStructureHandler.getTemplateByName(strings[1]);
                if (templateElement == null) {
                    iCommandSender.sendMessage(new TextComponentString("Template not found."));
                    break;
                }
                if (strings.length == 2) {
                    iCommandSender.sendMessage(new TextComponentString("<info,remove,spawn>"));
                    break;
                }
                switch (strings[2]) {
                    case "info":
                        iCommandSender.sendMessage(new TextComponentString(templateElement.getInfo(true)));
                        break;
                    case "remove":
                        if (NLibStructureHandler.LIST_TEMPLATES.removeIf((elem -> elem.name.equals(templateElement.name)))) {
                            iCommandSender.sendMessage(new TextComponentString("Template removed from the list."));
                        } else {
                            iCommandSender.sendMessage(new TextComponentString("Template not found."));
                        }
                        break;
                    case "spawn":
                        if (strings.length == 3) {
                            iCommandSender.sendMessage(new TextComponentString("<auto,pos>"));
                            break;
                        }
                        switch (strings[3]) {
                            case "auto":
                                if (strings.length == 4) {
                                    iCommandSender.sendMessage(new TextComponentString("<spawn_type_id>"));
                                    break;
                                }
                                int spawnType = CommandBase.parseInt(strings[4]);
                                if (strings.length == 5) {
                                    iCommandSender.sendMessage(new TextComponentString("<x>"));
                                    break;
                                }
                                int posX = CommandBase.parseInt(strings[5]);
                                if (strings.length == 6) {
                                    iCommandSender.sendMessage(new TextComponentString("<z>"));
                                    break;
                                }
                                int posZ = CommandBase.parseInt(strings[6]);
                                Rotation rotation = strings.length > 7 ? NLibStructureUtilities.getRotationByName(strings[7]) : Rotation.NONE;
                                BlockPos pos = DefaultStructureSpawnRules.getSpawnPos(iCommandSender.getEntityWorld(), new BlockPos(posX, 0, posZ), spawnType, null);
                                if (pos != null) {
                                    if (templateElement.generate(iCommandSender.getEntityWorld(), pos, rotation)) {
                                        iCommandSender.sendMessage(new TextComponentString("Template placed."));
                                    } else {
                                        iCommandSender.sendMessage(new TextComponentString("Template generation failed."));
                                    }
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Template cannot be placed here, no suitable spawn position was found."));
                                }
                                break;
                            case "pos":
                                if (strings.length == 4) {
                                    iCommandSender.sendMessage(new TextComponentString("<x>"));
                                    break;
                                }
                                int posX_ = CommandBase.parseInt(strings[4]);
                                if (strings.length == 5) {
                                    iCommandSender.sendMessage(new TextComponentString("<y>"));
                                    break;
                                }
                                int posY_ = CommandBase.parseInt(strings[5]);
                                if (strings.length == 6) {
                                    iCommandSender.sendMessage(new TextComponentString("<z>"));
                                    break;
                                }
                                int posZ_ = CommandBase.parseInt(strings[6]);
                                Rotation rotation_ = strings.length > 7 ? NLibStructureUtilities.getRotationByName(strings[7]) : Rotation.NONE;
                                BlockPos pos_ = new BlockPos(posX_, posY_, posZ_);
                                if (templateElement.generate(iCommandSender.getEntityWorld(), pos_, rotation_)) {
                                    iCommandSender.sendMessage(new TextComponentString("Template placed."));
                                } else {
                                    iCommandSender.sendMessage(new TextComponentString("Template generation failed."));
                                }
                                break;
                        }
                        break;
                }
                break;
            case "generator":
                if (strings.length == 1) {
                    iCommandSender.sendMessage(new TextComponentString("<name>"));
                    break;
                }
                StructureGeneratorBase generatorBase = NLibStructureHandler.getGeneratorByName(strings[1]);
                if (generatorBase == null) {
                    iCommandSender.sendMessage(new TextComponentString("Generator not found."));
                    break;
                }
                generatorBase.commandExecuteHandler(minecraftServer, iCommandSender, strings);
                break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] strings, @Nullable BlockPos pos) {
        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            tab.add("help");
            tab.add("reset");
            tab.add("spawn");
            tab.add("list");
            tab.add("debug");
            tab.add("structure");
            tab.add("template");
            tab.add("generator");
            return tab;
        }
        switch (strings[0]) {
            case "spawn":
                if (strings.length == 2) {
                    tab.add("true");
                    tab.add("false");
                    tab.add("info");
                    break;
                }
                break;
            case "list":
                if (strings.length == 2) {
                    tab.add("generator");
                    tab.add("structure");
                    tab.add("template");
                    break;
                } else if (strings.length == 3) {
                    tab.add("clear");
                    tab.add("info");
                    tab.add("remove");
                    tab.add("reset");
                }
                switch (strings[1]) {
                    case "generator":
                        switch (strings[2]) {
                            case "info":
                            case "remove":
                                for (StructureGeneratorBase generatorBase : NLibStructureHandler.LIST_GENERATORS) {
                                    tab.add(generatorBase.name);
                                }
                                break;
                        }
                        break;
                    case "structure":
                        switch (strings[2]) {
                            case "info":
                            case "remove":
                                for (StructureBase structureBase : NLibStructureHandler.LIST_STRUCTURES) {
                                    tab.add(structureBase.name);
                                }
                                break;
                        }
                        break;
                    case "template":
                        switch (strings[2]) {
                            case "info":
                            case "remove":
                                for (TemplateElement templateElement : NLibStructureHandler.LIST_TEMPLATES) {
                                    tab.add(templateElement.name);
                                }
                                break;
                        }
                        break;
                }
                break;
            case "debug":
                if (strings.length == 2) {
                    tab.add("true");
                    tab.add("false");
                    break;
                }
                break;
            case "template":
                if (strings.length == 2) {
                    for (TemplateElement element : NLibStructureHandler.LIST_TEMPLATES) {
                        tab.add(element.name);
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
                                        tab.add("0");
                                        tab.add("90");
                                        tab.add("180");
                                        tab.add("270");
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
                                        tab.add("0");
                                        tab.add("90");
                                        tab.add("180");
                                        tab.add("270");
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
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
                                        tab.add("0");
                                        tab.add("90");
                                        tab.add("180");
                                        tab.add("270");
                                        break;
                                    case 9:
                                    case 10:
                                        tab.add("true");
                                        tab.add("false");
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
                                        tab.add("0");
                                        tab.add("90");
                                        tab.add("180");
                                        tab.add("270");
                                        break;
                                    case 9:
                                    case 10:
                                        tab.add("true");
                                        tab.add("false");
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;
            case "generator":
                if (strings.length == 2) {
                    for (StructureGeneratorBase generator : NLibStructureHandler.LIST_GENERATORS) {
                        tab.add(generator.name);
                    }
                } else {
                    StructureGeneratorBase generator = NLibStructureHandler.getGeneratorByName(strings[1]);
                    if (generator != null) {
                        generator.commandTabHandler(server, sender, strings, pos, tab);
                    }
                }
                break;
        }
        return tab;
    }
}
