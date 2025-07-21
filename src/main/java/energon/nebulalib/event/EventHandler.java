package energon.nebulalib.event;

import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import energon.nebulalib.event.events.EventBase;
import energon.nebulalib.event.events.Event_SawBuglin;
import energon.nebulalib.event.test.Test_LooksAtEntity;
import energon.nebulalib.event.test.ITestBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<EVENT> EVENTS = new ArrayList<>();
    public static int ticks = -200;
    public static boolean importData = true;
    public static EventSaveData DATA;
    private final static List<EventBase> PLAYERS_EVENT = new ArrayList<>();
    public static List<EventBase> PLAYERS_EVENT_ADD = new ArrayList<>();
    public static void init() {
        EVENTS.add(new EVENT(1, SIDE.PLAYER_TICK, Event_SawBuglin::new, new Test_LooksAtEntity(EntityLodo.class)));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (++ticks % 10 == 5) {
                if (importData) {
                    importData = false;
                    DATA = EventSaveData.get(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0]);
                }
                EventSaveData.EVENT_DATA playerData;
                for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    playerData = DATA.getPlayerData(player.getName());
                    if (playerData.correctEventEnded && playerData.correctEvent == 0) {
                        for (EVENT test : EVENTS) {
                            if (test.side.isPlayerEvent() && test.canStartEvent(player, playerData)) {
                                test.startEvent(player);
                                break;
                            }
                        }
                    }
                }



            }
            PLAYERS_EVENT.removeIf(EventBase::serverHandler);
            if (!PLAYERS_EVENT_ADD.isEmpty()) {
                PLAYERS_EVENT.addAll(PLAYERS_EVENT_ADD);
                PLAYERS_EVENT_ADD.clear();
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Entity attacker = event.getEntity();
        List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
        for (EventBase eventBase : local) {
            if (eventBase.player == attacker && !eventBase.canAttack(event.getTarget())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onDeathEvent(LivingDeathEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!living.world.isRemote) {
            for (EVENT test : EVENTS) {
                if (test.side.isEntityDeathEvent() && test.canStartEvent(living, event.getSource())) {
                    test.startEventZone(living.world, living);
                    break;
                }
            }
        }
    }

    public static class EVENT {
        public final int eventID;
        public final SIDE side;
        public final Function<EntityPlayer,EventBase> supplier;
        public final ITestBase[] tests;
        public EVENT(int id, SIDE side, Function<EntityPlayer,EventBase> s, ITestBase... t) {
            this.eventID = id;
            this.side = side;
            this.supplier = s;
            this.tests = t;
        }

        /**Death Event*/
        public boolean canStartEvent(EntityLivingBase entity, DamageSource source) {
            for (ITestBase t : this.tests) {
                if (!t.canStartEvent(entity, source)) {
                    return false;
                }
            }
            return true;
        }

        /**Player Tick*/
        public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_DATA data) {
            if (data.playerCompletedEvent(this.eventID)) {
                return false;
            }
            for (ITestBase t : this.tests) {
                if (!t.canStartEvent(player, data)) {
                    return false;
                }
            }
            return true;
        }

        public void startEventZone(World world, EntityLivingBase entity) {
            this.startEventZone(world, new AxisAlignedBB(entity.posX - 24, entity.posY - 12, entity.posZ - 24, entity.posX + 24, entity.posY + 12, entity.posZ + 24));
        }

        public void startEventZone(World world, AxisAlignedBB box) {
            for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, box)) {
                this.startEvent(player);
            }
        }

        public void startEvent(EntityPlayer player) {
            DATA.addPlayerEvent(player.getName(), this.eventID);
            Network.sendPlayerEvent(player, this.eventID);
            PLAYERS_EVENT_ADD.add(this.getEvent(player));
        }

        public EventBase getEvent(EntityPlayer player) {
            return supplier.apply(player);
        }
    }

    public enum SIDE {
        /**onPlayerUpdate*/
        PLAYER_TICK(),
        /**onEntitySpawn*/
        ENTITY_SPAWN(),
        /**onEntityDeath for all in the zone*/
        ENTITY_KILLED(),
        /**onEntityKill for killer*/
        ENTITY_KILL();
        SIDE() {

        }

        public boolean isPlayerEvent() {
            return this == PLAYER_TICK;
        }

        public boolean isEntityDeathEvent() {
            return this == ENTITY_KILLED;
        }
    }
}
