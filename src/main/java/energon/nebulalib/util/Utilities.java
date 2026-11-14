package energon.nebulalib.util;

import java.util.Arrays;

public class Utilities {
    public static String repeat(char l, int count) {
        char[] f = new char[count];
        Arrays.fill(f, l);
        return new String(f);
    }

    public static String repeat(String l, int count) {
        return l.repeat(count);
    }
}
