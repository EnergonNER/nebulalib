package energon.nebulalib.event.test;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Deprecated
public class TEST_NodeNoMoreExists implements ITestBase {
    public final int nodeExistEvent;
    public TEST_NodeNoMoreExists(int existEventID) {
        this.nodeExistEvent = existEventID;
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        if (!data.worldCanStartEvent(this.nodeExistEvent)) {
            World world = DimensionManager.getWorld(dimID);
            if (world != null) {
                return SRPWorldData.get(world).getNodes("a").isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        if (deadEntity instanceof EntityPlayer) {
            if (!EventSaveData.get(deadEntity.world).getPlayerData(deadEntity.getName(), true).playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
                return SRPWorldData.get(deadEntity.world).getNodes("a").isEmpty();
            }
        } else if (source.getTrueSource() instanceof EntityPlayer) {
            if (!EventSaveData.get(deadEntity.world).getPlayerData(source.getTrueSource().getName(), true).playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
                return SRPWorldData.get(deadEntity.world).getNodes("a").isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target, LivingAttackEvent event) {
        if (attacker instanceof EntityPlayer) {
            if (!EventSaveData.get(attacker.world).getPlayerData(attacker.getName(), true).playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
                return SRPWorldData.get(attacker.world).getNodes("a").isEmpty();
            }
        } else if (target instanceof EntityPlayer) {
            if (!EventSaveData.get(target.world).getPlayerData(target.getName(), true).playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
                return SRPWorldData.get(target.world).getNodes("a").isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        if (!data.playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
            return SRPWorldData.get(player.world).getNodes("a").isEmpty();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (!data.playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
            return SRPWorldData.get(event.player.world).getNodes("a").isEmpty();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (!data.playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
            return SRPWorldData.get(event.getWorld()).getNodes("a").isEmpty();
        }
        return false;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (!data.playerCanStartEvent(this.nodeExistEvent, NLibEventHandler.RARITY.COMMON)) {
            return SRPWorldData.get(event.getWorld()).getNodes("a").isEmpty();
        }
        return false;
    }
}
