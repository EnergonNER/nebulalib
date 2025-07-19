package energon.nebulalib.util;

import energon.nebulalib.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class LootTable {
    public static Random random = new Random();
    public List<Pool> pools = new ArrayList<>();
    public String[] values;
    public int globalWight = 0;
    public LootTable(String[] v) {
        this.values = v;
    }

    public void dropItemsEntity(Entity entity) {
        this.dropItems(entity.level(), entity.getX(), entity.getY(0.2), entity.getZ(), 100, false);
    }

    public void dropItems(Level level, double x, double y, double z, int perPoolChance, boolean oneItem) {
        for (Pool pool : pools) {
            if (random.nextInt(perPoolChance) < pool.chance) {
                ItemEntity itementity = new ItemEntity(level, x, y, z, pool.getItemStack(level));
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
                if (oneItem) {
                    return;
                }
            }
        }
    }

    /**"minecraft:iron_ingot;1;4;100;enchs=minecraft:protection|1|2|100,minecraft:sharpness|1|100|100;tags=;name=LOL"*/
    public void generate(String[] list) {
        for (String element : list) {
            String[] parts = element.split(";");
            if (parts.length > 3) {
                int weight = Integer.parseInt(parts[3]);
                Pool pool = new Pool(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), weight);
                for (int i = 4; i < parts.length; i++) {
                    if (parts[i].contains("enchs=")) {
                        pool.generateEnchantments(parts[i].substring(6).split(","));
                    } else if (parts[i].contains("name=")) {
                        pool.custom_name = parts[i].substring(5);
                    } else if (parts[i].contains("tags=")) {

                    }
                }
                this.globalWight += weight;
                pools.add(pool);
            }
        }
    }

    public void selfGenerate() {
        this.generate(this.values);
    }

    public void configCompatibility(Config config, String category) {
        this.values = config.getStringList("loot_table", category, this.values, "(TEST)");
    }

    @Override
    public String toString() {
        return this.pools.toString();
    }

    public static class Pool {
        public final String itemName;
        public int min;
        public int max;
        public int chance;
        public String custom_name;
        public List<EnchantmentPool> enchantments;
        public Integer enchantmentsGlobalWeight;
        public Pool(String itemName, int min, int max, int chance) {
            this.itemName = itemName;
            if (min <= max) {
                this.min = min;
                this.max = max;
            } else {
                this.min = max;
                this.max = min;
            }
            this.chance = chance;
        }

        /**"minecraft:sharpness|1|2|100,.."*/
        public void generateEnchantments(String[] list) {
            this.enchantments = new ArrayList<>();
            this.enchantmentsGlobalWeight = 0;
            for (String element : list) {
                String[] parts = element.split(Pattern.quote("|"));
                if (parts.length == 4) {
                    int weight = Integer.parseInt(parts[3]);
                    EnchantmentPool pool = new EnchantmentPool(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), weight);
                    this.enchantmentsGlobalWeight += weight;
                    this.enchantments.add(pool);
                }
            }
        }

        public ItemStack getItemStack(Level level) {
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemName));
            if (item != null && item != Items.AIR) {
                ItemStack stack = new ItemStack(item, this.min + random.nextInt((this.max - this.min) + 1));
                if (this.custom_name != null) {
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal(this.custom_name));
                }
                if (this.enchantments != null) {
                    this.applyEnchantments(level, stack, 100, 3);
                }
                return stack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        public void applyEnchantments(Level level, ItemStack stack, int chancePerPool, int countEnch) {
            for (EnchantmentPool pool : this.enchantments) {
                if (countEnch > 0) {
                    if (random.nextInt(chancePerPool) < pool.chance) {
                        --countEnch;
                        pool.enchantItemStack(level, stack);
                    }
                } else {
                    return;
                }
            }
        }

        @Override
        public String toString() {
            return "Item=" + this.itemName + ",Min=" + this.min + ",Max=" + this.max + ",Chance=" + this.chance + (this.enchantments == null ? "" : "\nEnchantments=" + this.enchantments.toString()) + (this.custom_name == null ? "" : ("\nCustom_Name=" + this.custom_name));
        }
    }

    public static class EnchantmentPool {
        public String enchantment;
        public int min;
        public int max;
        public int chance;
        public EnchantmentPool(String ench, int min, int max, int chance) {
            this.enchantment = ench;
            if (min <= max) {
                this.min = min;
                this.max = max;
            } else {
                this.min = max;
                this.max = min;
            }
            this.chance = chance;
        }

        /**Crash if not Present*/
        public Holder<Enchantment> getEnchantments(Level level) {
            return level.registryAccess().lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(this.enchantment)));
        }

        public void enchantItemStack(Level level, ItemStack itemStack) {
            itemStack.enchant(this.getEnchantments(level), this.min + random.nextInt((this.max - this.min) + 1));
        }

        @Override
        public String toString() {
            return "ENCH=" + this.enchantment + "|MIN=" + this.min + "|MAX" + this.max + "|CHANCE=" + this.chance;
        }
    }
}
