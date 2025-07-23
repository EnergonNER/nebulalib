package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_PlayerInBiome implements ITestBase {
    public final Biome targetBiome;
    public TEST_PlayerInBiome(Biome biome) {
        this.targetBiome = biome;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        return player.world.getBiome(player.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        return (attacker instanceof EntityPlayer && attacker.world.getBiome(attacker.getPosition()) == this.targetBiome)
                || (target instanceof EntityPlayer && target.world.getBiome(target.getPosition()) == this.targetBiome);
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        return deadEntity instanceof EntityPlayer && deadEntity.world.getBiome(deadEntity.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.player.world.getBiome(event.player.getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getWorld().getBiome(event.getEntity().getPosition()) == this.targetBiome;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getWorld().getBiome(event.getPlayer().getPosition()) == this.targetBiome;
    }
}
