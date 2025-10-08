package energon.nebulalib.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class NLibLootTable {
    public static Map<String, NLibLootTable> LOOT_TABLES = new HashMap<>();


    public static NLibLootTable generate(String category, String[] loots) {
        NLibLootTable loot;
        try {
            loot = new NLibLootTable();







            if (category != null) {
                LOOT_TABLES.putIfAbsent(category, loot);
            }
            return loot;
        } catch (NumberFormatException e) {
            loot = new NLibLootTable();
            if (category != null) {
                LOOT_TABLES.putIfAbsent(category, loot);
            }
            return loot;
        }
    }

    public NLibLootTable() {

    }

    public void spawnLoot(LivingEntity summoner, int chance) {

    }
}
