package energon.nebulalib.config;

public enum ElementType {
    STRING("S:"),
    STRING_LIST("LS:"),
    INT("I:"),
    FLOAT("F:"),
    DOUBLE("D:"),
    BOOLEAN("B:"),
    CATEGORY_START(":{"),
    CATEGORY_END("}"),
    DESCRIPTION("#"),
    LIST_START("<::<"),
    LIST_END(">::>"),
    EQUALS("="),
    VERSION("~");

    public final String info;
    ElementType(String i) {
        info = i;
    }

    public String getPrefix() {
        return info;
    }
}
