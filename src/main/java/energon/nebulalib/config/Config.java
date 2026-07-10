package energon.nebulalib.config;

import energon.nebulalib.util.Utilities;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Config {
    private static final String list1 = ":#__#:";
    private static final String list2 = ":#_#:";
    private static final String util1 = ":\\|\\|:";

    public boolean exists;
    public final String version;
    public File configFile;
    public List<Category> CATEGORIES = new ArrayList<>();

    public Config(boolean e, String v, File f) {
        this.exists = e;
        this.version = v;
        this.configFile = f;
        this.read();
    }

    public static Config CreateConfig(Path path, String version) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File configFile = path.toFile();
        return new Config(configFile.exists(), version, configFile);
    }

    public static Config CreateConfig(String pathString, String version) {
        Path path = Paths.get(pathString);
        return CreateConfig(path, version);
    }

    public static Config CreateConfigWithDefaultPath(String pathString, String version) {
        return CreateConfig(FMLPaths.CONFIGDIR.get().resolve(pathString), version);
    }

    public void alert(String message) {
        this.exists = false;
        System.out.println(message);
    }

    public void read() {
        if (exists) {
            try {
                List<TEST> Structure = new ArrayList<>();
                List<String> __LINES__ = Files.readAllLines(configFile.toPath());
                String categoryName = "";
                boolean list = false;
                String listValues = "";
                for (int i = 0; i < __LINES__.size(); i++) {
                    String line = __LINES__.get(i).trim();
                    if (line.isEmpty() || line.startsWith(ElementType.DESCRIPTION.getPrefix())) {
                        continue;
                    }
                    if (i == 0 && line.startsWith(ElementType.VERSION.getPrefix())) {
                        String existVersion = line.split(Pattern.quote(ElementType.EQUALS.getPrefix()))[1];
                        if (!version.equals(existVersion)) {
                            this.alert("!!!Version Change!!!");
                            return;
                        }
                    } else if (line.contains(ElementType.CATEGORY_START.getPrefix())) {
                        categoryName = line.split(Pattern.quote(ElementType.CATEGORY_START.getPrefix()))[0];
                    } else if (line.startsWith(ElementType.STRING_LIST.getPrefix()) && line.contains(ElementType.LIST_START.getPrefix())) {
                        list = true;
                        listValues = (line.substring(line.indexOf(ElementType.STRING_LIST.getPrefix())).split(Pattern.quote(ElementType.LIST_START.getPrefix()))[0] + list1);
                    } else if (list && line.startsWith(ElementType.LIST_END.getPrefix())) {
                        list = false;
                        updateList(Structure, categoryName, listValues);
                    } else if (list) {
                        listValues += line + list2;
                    } else if (line.startsWith(ElementType.STRING.getPrefix())) {
                        updateList(Structure, categoryName, line.substring(line.indexOf(ElementType.STRING.getPrefix())));
                    } else if (line.startsWith(ElementType.INT.getPrefix())) {
                        updateList(Structure, categoryName, line.substring(line.indexOf(ElementType.INT.getPrefix())));
                    } else if (line.startsWith(ElementType.FLOAT.getPrefix())) {
                        updateList(Structure, categoryName, line.substring(line.indexOf(ElementType.FLOAT.getPrefix())));
                    } else if (line.startsWith(ElementType.DOUBLE.getPrefix())) {
                        updateList(Structure, categoryName, line.substring(line.indexOf(ElementType.DOUBLE.getPrefix())));
                    } else if (line.startsWith(ElementType.BOOLEAN.getPrefix())) {
                        updateList(Structure, categoryName, line.substring(line.indexOf(ElementType.BOOLEAN.getPrefix())));
                    }
                }
                for (TEST test : Structure) {
                    Category category = new Category(test.categoryName);
                    category.read(test);
                    CATEGORIES.add(category);
                }
            } catch (IOException e) {
                this.alert("!!!Read Error!!!");
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (!exists) {
            try (PrintWriter pw = new PrintWriter(configFile)) {
                pw.println(ElementType.VERSION.getPrefix() + "VERSION=" + version);
                for (Category category : CATEGORIES) {
                    category.save(pw);
                }
                exists = true;
            } catch (IOException e) {
                this.alert("!!!Save Error!!!");
                e.printStackTrace();
            }
        }
    }

    public static void updateList(List<TEST> list, String categoryName, String value) {
        for (TEST test : list) {
            if (test.categoryName.equals(categoryName)) {
                test.add(value);
                return;
            }
        }
        list.add(new TEST(categoryName, value));
    }

    public void addCategoryInfo(String name, String info) {
        for (Category category : CATEGORIES) {
            if (category.name.equals(name)) {
                category.info = info;
                return;
            }
        }
        CATEGORIES.add(new Category(name, info));
    }

    public Category getCategory(String name) {
        for (Category category : CATEGORIES) {
            if (category.name.equals(name)) {
                return category;
            }
        }
        Category c = new Category(name);
        CATEGORIES.add(c);
        return c;
    }

    public String getString(String element, String category, String defaultValue, String description) {
        return (String) getCategory(category).getElementString(this, element, defaultValue, description).getValue();
    }

    public String[] getStringList(String element, String category, String[] defaultValue, String description) {
        return (String[]) getCategory(category).getElementStringList(this, element, defaultValue, description).getValue();
    }

    public int getInt(String element, String category, int defaultValue, int min, int max, String description) {
        return (int) getCategory(category).getElementInt(this, element, defaultValue, min, max, description).getValue();
    }

    public float getFloat(String element, String category, float defaultValue, float min, float max, String description) {
        return (float) getCategory(category).getElementFloat(this, element, defaultValue, min, max, description).getValue();
    }

    public double getDouble(String element, String category, double defaultValue, double min, double max, String description) {
        return (double) getCategory(category).getElementDouble(this, element, defaultValue, min, max, description).getValue();
    }

    public Boolean getBoolean(String element, String category, Boolean defaultValue, String description) {
        return (Boolean) getCategory(category).getElementBoolean(this, element, defaultValue, description).getValue();
    }

    public static class StringElement extends ConfigBase {
        public String value;
        public String defaultValue;
        public StringElement(String n, String defValue, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
        }

        public StringElement(String n, String newValue) {
            super(n, null);
            value = newValue;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.STRING.getPrefix() + name + ElementType.EQUALS.getPrefix() + value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        public void lcUpdate(String description, String defValue) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
        }

        @Override
        public String getDefault() {
            return "\t[default: " + value + " ]";
        }
    }

    public static class StringListElement extends ConfigBase {
        public String[] value;
        public String[] defaultValue;
        public StringListElement(String n, String[] defValue, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
        }

        public StringListElement(String n, String[] newValue) {
            super(n, null);
            value = newValue;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.STRING_LIST.getPrefix() + name + ElementType.LIST_START.getPrefix());
            if (value != null) {
                for (String parts : value) {
                    writer.println("\t\t" + parts);
                }
            }
            writer.println("\t" + ElementType.LIST_END.getPrefix());
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String getDefault() {
            StringBuilder sb = new StringBuilder();
            sb.append("\t[default: ");
            for (int i = 0; i < value.length; i++) {
                sb.append("[").append(value[i]).append("]");
                if (i != value.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }

        public void lcUpdate(String description, String[] defValue) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
        }
    }

    public static class IntElement extends ConfigBase {
        public int value;
        public Integer defaultValue;
        public Integer minimum;
        public Integer maximum;
        public IntElement(String n, int defValue, int min, int max, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
            minimum = min;
            maximum = max;
        }

        public IntElement(String n, int newValue) {
            super(n, null);
            value = newValue;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.INT.getPrefix() + name + ElementType.EQUALS.getPrefix() + value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String getDefault() {
            return "\t[range: " + (minimum == null ? Integer.MIN_VALUE : minimum) + " ~ " + (maximum == null ? Integer.MAX_VALUE : maximum) + ", default: " + defaultValue + " ]";
        }

        public void lcUpdate(String description, int defValue, int min, int max) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
            if (minimum == null) {
                minimum = min;
            }
            if (maximum == null) {
                maximum = max;
            }
        }
    }

    public static class FloatElement extends ConfigBase {
        public float value;
        public Float defaultValue;
        public Float minimum;
        public Float maximum;
        public FloatElement(String n, float defValue, float min, float max, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
            minimum = min;
            maximum = max;
        }

        public FloatElement(String n, float defaultValue) {
            super(n, null);
            value = defaultValue;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.FLOAT.getPrefix() + name + ElementType.EQUALS.getPrefix() + value);
        }

        @Override
        public String getDefault() {
            return "\t[range: " + (minimum == null ? Integer.MIN_VALUE : minimum) + " ~ " + (maximum == null ? Integer.MAX_VALUE : maximum) + ", default: " + defaultValue + " ]";
        }

        public void lcUpdate(String description, float defValue, float min, float max) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
            if (minimum == null) {
                minimum = min;
            }
            if (maximum == null) {
                maximum = max;
            }
        }
    }

    public static class DoubleElement extends ConfigBase {
        public double value;
        public Double defaultValue;
        public Double minimum;
        public Double maximum;
        public DoubleElement(String n, double defValue, double min, double max, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
            minimum = min;
            maximum = max;
        }

        public DoubleElement(String n, double defaultValue) {
            super(n, null);
            value = defaultValue;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.DOUBLE.getPrefix() + name + ElementType.EQUALS.getPrefix() + value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String getDefault() {
            return "\t[range: " + (minimum == null ? Integer.MIN_VALUE : minimum) + " ~ " + (maximum == null ? Integer.MAX_VALUE : maximum) + ", default: " + defaultValue + " ]";
        }

        public void lcUpdate(String description, double defValue, double min, double max) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
            if (minimum == null) {
                minimum = min;
            }
            if (maximum == null) {
                maximum = max;
            }
        }
    }

    public static class BooleanElement extends ConfigBase {
        public Boolean value;
        public Boolean defaultValue;
        public BooleanElement(String n, Boolean defValue, String description) {
            super(n, description);
            value = defValue;
            defaultValue = defValue;
        }

        public BooleanElement(String n, Boolean newValue) {
            super(n, null);
            value = newValue;
        }

        @Override
        public void save(PrintWriter writer) {
            super.save(writer);
            writer.println("\t" + ElementType.BOOLEAN.getPrefix() + name + ElementType.EQUALS.getPrefix() + value);
        }

        @Override
        public Object getValue() {
            return value;
        }

        public void lcUpdate(String description, Boolean defValue) {
            if (info == null) {
                info = description;
            }
            if (defaultValue == null) {
                defaultValue = defValue;
            }
        }

        @Override
        public String getDefault() {
            return "\t[default: " + value + " ]";
        }
    }

    public static class Category {
        public final String name;
        public String info;
        public List<ConfigBase> elements = new ArrayList<>();
        public Category(String n) {
            name = n;
        }

        public Category(String n, String inf) {
            this(n);
            info = inf;
        }

        public ConfigBase get(String name, Config config) {
            for (ConfigBase element : elements) {
                if (element.name.equals(name)) {
                    return element;
                }
            }
            config.exists = false;
            return null;
        }

        public ConfigBase getElementString(Config config, String name, String defaultValue, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new StringElement(name, defaultValue, description);
                elements.add(base);
            }
            if (base instanceof StringElement) {
                ((StringElement) base).lcUpdate(description, defaultValue);
            }
            return base;
        }

        public ConfigBase getElementStringList(Config config, String name, String[] defaultValue, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new StringListElement(name, defaultValue, description);
                elements.add(base);
            }
            if (base instanceof StringListElement) {
                ((StringListElement) base).lcUpdate(description, defaultValue);
            }
            return base;
        }

        public ConfigBase getElementInt(Config config, String name, int defaultValue, int minimum, int maximum, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new IntElement(name, defaultValue, minimum, maximum, description);
                elements.add(base);
            }
            if (base instanceof IntElement) {
                ((IntElement) base).lcUpdate(description, defaultValue, minimum, maximum);
            }
            return base;
        }

        public ConfigBase getElementFloat(Config config, String name, float defaultValue, float minimum, float maximum, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new FloatElement(name, defaultValue, minimum, maximum, description);
                elements.add(base);
            }
            if (base instanceof FloatElement) {
                ((FloatElement) base).lcUpdate(description, defaultValue, minimum, maximum);
            }
            return base;
        }

        public ConfigBase getElementDouble(Config config, String name, double defaultValue, double minimum, double maximum, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new DoubleElement(name, defaultValue, minimum, maximum, description);
                elements.add(base);
            }
            if (base instanceof DoubleElement) {
                ((DoubleElement) base).lcUpdate(description, defaultValue, minimum, maximum);
            }
            return base;
        }

        public ConfigBase getElementBoolean(Config config, String name, Boolean defaultValue, String description) {
            ConfigBase base = get(name, config);
            if (base == null) {
                base = new BooleanElement(name, defaultValue, description);
                elements.add(base);
            }
            if (base instanceof BooleanElement) {
                ((BooleanElement) base).lcUpdate(description, defaultValue);
            }
            return base;
        }



        public void read(TEST test) {
            for (String parts : test.get()) {
                if (parts.startsWith(ElementType.STRING_LIST.getPrefix())) {
                    String[] check = parts.split(Pattern.quote(list1));
                    ConfigBase base = new StringListElement(check[0].substring(3), check[1].split(Pattern.quote(list2)));
                    elements.add(base);
                } else if (parts.startsWith(ElementType.STRING.getPrefix())) {
                    String[] check = parts.substring(2).split(Pattern.quote(ElementType.EQUALS.getPrefix()));
                    elements.add(new StringElement(check[0], check[1]));
                } else if (parts.startsWith(ElementType.INT.getPrefix())) {
                    String[] check = parts.substring(2).split(Pattern.quote(ElementType.EQUALS.getPrefix()));
                    elements.add(new IntElement(check[0], Integer.parseInt(check[1])));
                } else if (parts.startsWith(ElementType.FLOAT.getPrefix())) {
                    String[] check = parts.substring(2).split(Pattern.quote(ElementType.EQUALS.getPrefix()));
                    elements.add(new FloatElement(check[0], Float.parseFloat(check[1])));
                } else if (parts.startsWith(ElementType.DOUBLE.getPrefix())) {
                    String[] check = parts.substring(2).split(Pattern.quote(ElementType.EQUALS.getPrefix()));
                    elements.add(new DoubleElement(check[0], Double.parseDouble(check[1])));
                } else if (parts.startsWith(ElementType.BOOLEAN.getPrefix())) {
                    String[] check = parts.substring(2).split(Pattern.quote(ElementType.EQUALS.getPrefix()));
                    elements.add(new BooleanElement(check[0], Boolean.parseBoolean(check[1])));
                }
            }
        }

        public void save(PrintWriter writer) {
            writer.println("");
            writer.println("");
            writer.println(Utilities.repeat(ElementType.DESCRIPTION.getPrefix(), 106));
            //writer.println(ElementType.DESCRIPTION.getPrefix().repeat(106));
            writer.println(ElementType.DESCRIPTION.getPrefix() + " " + name);
            writer.println(ElementType.DESCRIPTION.getPrefix() + "--------------------------------------------------------------------------------------------------------" + ElementType.DESCRIPTION.getPrefix());
            if (info == null) {
                writer.println(ElementType.DESCRIPTION.getPrefix());
            } else {
                for (String parts : info.split("\\n")) {
                    writer.println(ElementType.DESCRIPTION.getPrefix() + " " +  parts);
                }
            }
            writer.println(Utilities.repeat(ElementType.DESCRIPTION.getPrefix(), 106));
            writer.println(name + ElementType.CATEGORY_START.getPrefix());
            for (ConfigBase element : elements) {
                element.save(writer);
                writer.println("");
            }
            writer.println(ElementType.CATEGORY_END.getPrefix());
        }
    }

    public static class TEST {
        public String categoryName;
        public String value = "";
        public TEST(String n) {
            categoryName = n;
        }

        public TEST(String n, String v) {
            this(n);
            value += v;
        }

        public void add(String add) {
            value += (util1+add);
        }

        public String[] get() {
            return value.split(Pattern.quote(util1));
        }

        @Override
        public String toString() {
            return "Name:" + categoryName + ", Value:" + value;
        }
    }
}
