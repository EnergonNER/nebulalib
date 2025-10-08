package energon.nebulalib.config.preset;

import energon.nebulalib.config.Config;

public class EntityConfigBase implements IEConfigBase {
    public boolean active;
    public float health;
    public float damage;
    public float armor;
    public float armorToughness;
    public float movementSpeed;
    public float followRange;
    public float knockback;
    public EntityConfigBase(boolean active, float health, float damage, float armor, float armorToughness, float movementSpeed, float followRange, float knockback) {
        this.active = active;
        this.health = health;
        this.damage = damage;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.movementSpeed = movementSpeed;
        this.followRange = followRange;
        this.knockback = knockback;
    }

    public void init(Config config, String category) {
        active = config.getBoolean("active", category, active, "Active?");
        health = config.getFloat("health", category, health, 0F, 999999F, "Health.");
        damage = config.getFloat("attack_damage", category, damage, 0F, 999999F, "Damage.");
        armor = config.getFloat("armor", category, armor, 0F, 999999F, "Armor.");
        armorToughness = config.getFloat("armor_toughness", category, armorToughness, 0F, 999999F, "Armor Toughness.");
        movementSpeed = config.getFloat("movement_speed", category, movementSpeed, 0F, 999999F, "Movement Speed.");
        followRange = config.getFloat("follow_range", category, followRange, 0F, 999999F, "Follow Range.");
        knockback = config.getFloat("knockback", category, knockback, 0F, 999999F, "Knockback.");
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public float getArmor() {
        return armor;
    }

    @Override
    public float getArmorToughness() {
        return armorToughness;
    }

    @Override
    public float getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public float getFollowRange() {
        return followRange;
    }

    @Override
    public float getKnockback() {
        return knockback;
    }
}
