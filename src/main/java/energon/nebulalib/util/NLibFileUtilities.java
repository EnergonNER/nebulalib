package energon.nebulalib.util;

import energon.nebulalib.NebulaLib;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class NLibFileUtilities {
    /**
     * NLibFileUtilities.copyFromMod("nebulalib/custom/", "appleskin.cfg", new File(file, "nebulalib/generators"), false);
     * */
    public static int copyFromMod(String dir, String resource, File toDir, boolean ignoreExit) {
        if (!ignoreExit && (new File(toDir, resource)).exists()) {
            return 2;
        }
        if (!toDir.exists() && !toDir.mkdirs()) {
            return 3;
        }
        try (InputStream input = NebulaLib.class.getResourceAsStream("/assets/" + dir + resource)) {
            if (input != null) {
                Files.copy(input, new File(toDir, resource).toPath());
                return 0;
            }
            return 4;
        } catch (IOException e) {
            return 1;
        }
    }

    @Nullable
    public static Template getTemplateFromMod(String location) {
        try (InputStream input = NebulaLib.class.getResourceAsStream("/assets/" + location)) {
            if (input != null) {
                Template template = new Template();
                template.read(CompressedStreamTools.readCompressed(input));
                return template;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static Template getTemplateFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Template template = new Template();
            template.read(CompressedStreamTools.readCompressed(fis));
            return template;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getFileNameWithoutExt(File file) {
        return getNameWithoutExt(file.getName());
    }

    public static String getNameWithoutExt(String name) {
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0) ? name.substring(0, dotIndex) : "ERROR";
    }
}
