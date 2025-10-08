package energon.nebulalib.config;

import java.io.PrintWriter;

public abstract class ConfigBase {
    public final String name;
    public String info;
    public ConfigBase(String n, String des) {
        name = n;
        info = des;
    }

    public void save(PrintWriter writer) {
        if (info != null) {
            String[] parts = info.split("\\n");
            for (int i = 0; i < parts.length; i++) {
                writer.println("\t# " + parts[i] + ((i == (parts.length - 1)? getDefault() : "")));
            }
        } else {
            writer.println("\t" + ElementType.DESCRIPTION.getPrefix().repeat(102));
            writer.println("\t" + ElementType.DESCRIPTION.getPrefix().repeat(102));
        }
    }

    public abstract Object getValue();

    public String getDefault() {
        return "";
    }
}
