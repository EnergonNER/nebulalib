package energon.nebulalib.config.preset;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public interface IEConfigBase {
    float getHealth();
    float getDamage();
    float getArmor();
    float getArmorToughness();
    float getMovementSpeed();
    float getFollowRange();
    float getKnockback();
    default <T extends LivingEntity> AttributeSupplier.Builder  createAttributes() {
        return T.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, this.getHealth())
                .add(Attributes.MOVEMENT_SPEED, this.getMovementSpeed())
                .add(Attributes.FOLLOW_RANGE, this.getFollowRange())
                .add(Attributes.ATTACK_DAMAGE, this.getDamage())
                .add(Attributes.ARMOR, this.getArmor())
                .add(Attributes.ARMOR_TOUGHNESS, this.getArmorToughness())
                .add(Attributes.KNOCKBACK_RESISTANCE, this.getKnockback());
    }
}
