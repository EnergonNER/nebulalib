package energon.nebulalib.entity;

import energon.nebulalib.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityAttributes {
    public double health;
    public double movement_speed;
    public double follow_range;
    public double attack_damage;
    public double armor;
    public double armor_toughness;
    public double knockback;
    public EntityAttributes(double h, double m, double f, double a, double ar, double ar_t, double kb) {
        this.health = h;
        this.movement_speed = m;
        this.follow_range = f;
        this.attack_damage = a;
        this.armor = ar;
        this.armor_toughness = ar_t;
        this.knockback = kb;
    }

    public void increaseHealth(float multi) {
        if (multi != 1F) {
            this.health *= multi;
        }
    }

    public void increaseMovement_Speed(float multi) {
        if (multi != 1F) {
            this.movement_speed *= multi;
        }
    }

    public void increaseFollow_Range(float multi) {
        if (multi != 1F) {
            this.follow_range *= multi;
        }
    }

    public void increaseAttackDamage(float multi) {
        if (multi != 1F) {
            this.attack_damage *= multi;
        }
    }

    public void increaseArmor(float multi) {
        if (multi != 1F) {
            this.armor *= multi;
        }
    }

    public void increaseArmor_Toughness(float multi) {
        if (multi != 1F) {
            this.armor_toughness *= multi;
        }
    }

    public void increaseKnockback(float multi) {
        if (multi != 1F) {
            this.knockback *= multi;
        }
    }

    public void configCompatibility(Config config, String category) {
        this.health = config.getDouble("health", category, this.health, 0, 999999, "Health.");
        this.movement_speed = config.getDouble("movement_speed", category, this.movement_speed, 0, 999999, "Movement Speed.");
        this.follow_range = config.getDouble("follow_range", category, this.follow_range, 0, 999999, "Follow Range.");
        this.attack_damage = config.getDouble("attack_damage", category, this.attack_damage, 0, 999999, "Attack Damage.");
        this.armor = config.getDouble("armor", category, this.armor, 0, 999999, "Armor.");
        this.armor_toughness = config.getDouble("armor_toughness", category, this.armor_toughness, 0, 999999, "Armor Toughness.");
        this.knockback = config.getDouble("knockback", category, this.knockback, 0, 999999, "Knockback.");
    }

    public <T extends LivingEntity> AttributeSupplier.Builder createAttributes() {
        return T.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, this.health)
                .add(Attributes.MOVEMENT_SPEED, this.movement_speed)
                .add(Attributes.FOLLOW_RANGE, this.follow_range)
                .add(Attributes.ATTACK_DAMAGE, this.attack_damage)
                .add(Attributes.ARMOR, this.armor)
                .add(Attributes.ARMOR_TOUGHNESS, this.armor_toughness)
                .add(Attributes.KNOCKBACK_RESISTANCE, this.knockback);
    }
}
