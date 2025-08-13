package energon.nebulalib.util;

import java.util.Arrays;

public class NLibUtilities {
    public static String repeat(char l, int count) {
        char[] f = new char[count];
        Arrays.fill(f, l);
        return new String(f);
    }

    /**String.repeat(int)*/
    public static String repeat(String l, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(l);
        }
        return builder.toString();
    }
}
