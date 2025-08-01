package energon.nebulalib.event.test;

import energon.nebulalib.event.EventHandler;
import energon.nebulalib.event.EventSaveData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_EventCompleted implements ITestBase {
    public final boolean all;
    public final int[] events;
    public TEST_EventCompleted(boolean all, int... events) {
        this.all = all;
        this.events = events;
    }

    @Override
    public boolean canStartEvent(int dimID, EventSaveData.EVENT_WORLD_DATA data) {
        if (this.all) {
            for (int i : this.events) {
                if (data.worldCanStartEvent(i)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i : this.events) {
                if (data.worldCompletedEvent(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (this.all) {
            for (int i : this.events) {
                if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
        if (this.all) {
            for (int i : this.events) {
                if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (this.all) {
            for (int i : this.events) {
                if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        if (this.all) {
            for (int i : this.events) {
                if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canStartEvent(EntityLivingBase deadEntity, DamageSource source) {
        if (deadEntity instanceof EntityPlayer) {
            EventSaveData.EVENT_PLAYER_DATA data = EventHandler.DATA.getPlayerData(deadEntity.getName(), true);
            if (this.all) {
                for (int i : this.events) {
                    if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                        return false;
                    }
                }
                return true;
            }
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
        } else if (source.getTrueSource() instanceof EntityPlayer) {
            EventSaveData.EVENT_PLAYER_DATA data = EventHandler.DATA.getPlayerData(source.getTrueSource().getName(), true);
            if (this.all) {
                for (int i : this.events) {
                    if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                        return false;
                    }
                }
                return true;
            }
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canStartEvent(Entity attacker, Entity target) {
        if (attacker instanceof EntityPlayer) {
            EventSaveData.EVENT_PLAYER_DATA data = EventHandler.DATA.getPlayerData(attacker.getName(), true);
            if (this.all) {
                for (int i : this.events) {
                    if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                        return false;
                    }
                }
                return true;
            }
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
        } else if (target instanceof EntityPlayer) {
            EventSaveData.EVENT_PLAYER_DATA data = EventHandler.DATA.getPlayerData(target.getName(), true);
            if (this.all) {
                for (int i : this.events) {
                    if (data.playerCanStartEvent(i, EventHandler.RARITY.COMMON)) {
                        return false;
                    }
                }
                return true;
            }
            for (int i : this.events) {
                if (data.playerCompletedEvent(i)) {
                    return true;
                }
            }
        }
        return false;
    }
}
