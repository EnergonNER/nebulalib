package energon.nebulalib.util;

import net.minecraft.util.Rotation;

import javax.annotation.Nullable;

public class NLibStructureUtilities {
    public static Rotation getRotationByName(String name) {
        Rotation rotation = getTrueRotationByName(name);
        return rotation != null ? rotation : Rotation.NONE;
    }

    @Nullable
    public static Rotation getTrueRotationByName(String name) {
        switch (name) {
            case "0":
            case "NONE":
            case "none":
            case "rotate_0":
                return Rotation.NONE;
            case "90":
            case "CLOCKWISE_90":
            case "clockwise_90":
            case "rotate_90":
                return Rotation.CLOCKWISE_90;
            case "180":
            case "CLOCKWISE_180":
            case "clockwise_180":
            case "rotate_180":
                return Rotation.CLOCKWISE_180;
            case "270":
            case "-90":
            case "COUNTERCLOCKWISE_90":
            case "counterclockwise_90":
            case "rotate_270":
                return Rotation.COUNTERCLOCKWISE_90;
            default: return null;
        }
    }
}
