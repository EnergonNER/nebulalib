package energon.nebulalib.entity;

import energon.nebulalib.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRule {
    public static Map<String, Class<? extends LivingEntity>> AVAILABLE = new HashMap<>();

    static {
        AVAILABLE.putIfAbsent("monster.class", Monster.class);
        AVAILABLE.putIfAbsent("animal.class", Animal.class);
        AVAILABLE.putIfAbsent("living.class", LivingEntity.class);
        AVAILABLE.putIfAbsent("player.class", Player.class);
    }

    public List<String> friendsString = new ArrayList<>();
    public List<Class<? extends LivingEntity>> friendsClass = new ArrayList<>();

    public List<String> targetsString = new ArrayList<>();
    public List<Class<? extends LivingEntity>> targetsClass = new ArrayList<>();

    public boolean listFriendsRev;
    public boolean firstFriends;
    public boolean notPresentFriend;

    public Class<? extends LivingEntity> owner = null;
    public String[] values;

    public EntityRule(boolean rev, boolean firstF, boolean noPresentF, String[] v) {
        this.listFriendsRev = rev;
        this.firstFriends = firstF;
        this.notPresentFriend = noPresentF;
        this.values = v;
    }

    public EntityRule(String[] v, boolean noPresentF) {
        this(false, true, noPresentF, v);
    }

    public void selfGenerate() {
        this.generate(this.values);
    }

    /**generate friends!!!
     * */
    public void generate(String[] list) {
        for (String element : list) {
            if (element.contains(".")) {
                if (element.contains("/")) {
                    Class<?> test = null;
                    try {
                        test = Class.forName(element.replace(".class", "").replace("!", ""));
                    } catch (ClassNotFoundException ignored) {}
                    if (test != null && LivingEntity.class.isAssignableFrom(test)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends LivingEntity> clazz = (Class<? extends LivingEntity>) test;
                        if (element.contains("!")) {
                            this.targetsClass.add(clazz);
                        } else {
                            this.friendsClass.add(clazz);
                        }
                    }
                } else {
                    Class<? extends LivingEntity> test = this.fun(element.replace("!", ""));
                    if (test != null) {
                        if (element.contains("!")) {
                            this.targetsClass.add(test);
                        } else {
                            this.friendsClass.add(test);
                        }
                    }
                }
            } else {
                if (element.contains("!")) {
                    this.targetsString.add(element.replace("!", ""));
                } else {
                    this.friendsString.add(element);
                }
            }
        }
    }

    public Class<? extends LivingEntity> fun(String name) {
        return name.equals("this.class") ? this.owner : AVAILABLE.get(name);
    }

    public boolean friendly(LivingEntity target) {
        if (this.listFriendsRev) {//FIX THIS
            return this.testCls(target) && this.testName(target);
        }
        return this.testCls(target) || this.testName(target);
    }

    public boolean canAttack(LivingEntity target) {
        return !this.testCls(target) || !this.testName(target);
    }

    /**Return true if friend, false - not present or target list*/
    public boolean testCls(LivingEntity target) {
        if (this.firstFriends) {
            for (Class<?> friend : this.friendsClass) {
                if (friend.isInstance(target)) {
                    return !this.listFriendsRev;
                }
            }
            for (Class<?> entity : this.targetsClass) {
                if (entity.isInstance(target)) {
                    return this.listFriendsRev;
                }
            }
        } else {
            for (Class<?> entity : this.targetsClass) {
                if (entity.isInstance(target)) {
                    return this.listFriendsRev;
                }
            }
            for (Class<?> friend : this.friendsClass) {
                if (friend.isInstance(target)) {
                    return !this.listFriendsRev;
                }
            }
        }
        return this.listFriendsRev || this.notPresentFriend;
    }


    /**Return true if friend, false - not present or target list*/
    public boolean testName(LivingEntity target) {
        ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (registryName == null) {
            return true;
        }
        if (this.firstFriends) {
            for (String elements : this.friendsString) {
                if (elements.contains(":")) {
                    if (elements.equals(registryName.toString())) {
                        return !this.listFriendsRev;
                    }
                } else {
                    if (elements.equals(registryName.getNamespace())) {
                        return !this.listFriendsRev;
                    }
                }
            }
            for (String elements : this.targetsString) {
                if (elements.contains(":")) {
                    if (elements.equals(registryName.toString())) {
                        return this.listFriendsRev;
                    }
                } else {
                    if (elements.equals(registryName.getNamespace())) {
                        return this.listFriendsRev;
                    }
                }
            }
        } else {
            for (String elements : this.targetsString) {
                if (elements.contains(":")) {
                    if (elements.equals(registryName.toString())) {
                        return this.listFriendsRev;
                    }
                } else {
                    if (elements.equals(registryName.getNamespace())) {
                        return this.listFriendsRev;
                    }
                }
            }
            for (String elements : this.friendsString) {
                if (elements.contains(":")) {
                    if (elements.equals(registryName.toString())) {
                        return !this.listFriendsRev;
                    }
                } else {
                    if (elements.equals(registryName.getNamespace())) {
                        return !this.listFriendsRev;
                    }
                }
            }
        }
        return this.listFriendsRev || this.notPresentFriend;
    }

    public void configCompatibility(Config config, String category) {
        this.values = config.getStringList("relationships", category, this.values, """
                  Manages relationships between entities. Exp.
                    "monster.class" — friendly toward all monsters,
                    "!animal.class" — aggressive toward all animals,
                    "!minecraft" — hostile toward all Minecraft entities,
                    "!minecraft:zombie" — targets only zombies.""" + "\n AVAILABLE: " + AVAILABLE.keySet());
    }

    /*public enum CheckTypes {
        NAME(EntityRule::testName),
        CLASS(EntityRule::testCls);
        public final BiFunction<EntityRule, LivingEntity, Boolean> checker;
        CheckTypes(BiFunction<EntityRule, LivingEntity, Boolean> fun) {
            this.checker = fun;
        }

        public boolean test(EntityRule rule, LivingEntity target) {
            return this.checker.apply(rule, target);
        }
    }*/
}
