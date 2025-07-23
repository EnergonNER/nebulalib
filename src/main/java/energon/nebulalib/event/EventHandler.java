package energon.nebulalib.event;

import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import energon.nebulalib.event.events.*;
import energon.nebulalib.event.test.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EventHandler {
    public static List<EVENT> EVENTS = new ArrayList<>();
    public static int ticks = -200;
    public static boolean importData = true;
    public static EventSaveData DATA;

    private final static List<EventBase> PLAYERS_EVENT = new ArrayList<>();
    public static List<EventBase> PLAYERS_EVENT_ADD = new ArrayList<>();

    private final static List<EventBase> WORLDS_EVENT = new ArrayList<>();
    public static List<EventBase> WORLDS_EVENT_ADD = new ArrayList<>();

    public static void init() {
        EVENTS.add(new EVENT(1, SIDE.PLAYER_TICK, EVENT_SawBuglin::new, new TEST_PlayerLooksAtEntity(EntityLodo.class)));
        EVENTS.add(new EVENT(3, SIDE.PLAYER_TICK, EVENT_First_Contact::new, new TEST_PlayerHasPotionEffect(SRPPotions.COTH_E), new TEST_EvoPhase(0, 3)));
        //EVENTS.add(new EVENT(2, SIDE.PLAYER_INTERACT, EVENT_SawBuglin::new, new TEST_EntityKillPlayer(EntityShyco.class)));
    }

    public static void serverStarted() {
        ticks = -200;
        importData = true;
    }

    public static void serverStopping() {
        ticks = -200;
        DATA = null;
        PLAYERS_EVENT.clear();
        PLAYERS_EVENT_ADD.clear();
        WORLDS_EVENT.clear();
        WORLDS_EVENT_ADD.clear();
    }

    public static void initDATA(World world) {
        importData = false;
        DATA = EventSaveData.get(world);
        EventSaveData.EVENT_WORLD_DATA worldData;
        for (int worldID : DimensionManager.getStaticDimensionIDs()) {
            worldData = DATA.getWorldData(worldID);
            if (worldData.correctEvent != 0 || !worldData.correctEventEnded) {
                for (EVENT test : EVENTS) {
                    if (test.eventID == worldData.correctEvent) {
                        test.startEvent(DimensionManager.getWorld(worldID));
                        return;
                    }
                    worldData.setEventEnded();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (importData || DATA == null) {
            initDATA(event.player.world);
        }
        EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.player.getName());
        if (playerData.correctEvent != 0 || !playerData.correctEventEnded) {
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == event.player) {
                    return;
                }
            }
            for (EVENT test : EVENTS) {
                if (test.eventID == playerData.correctEvent) {
                    test.startEvent(event.player);
                    return;
                }
            }
            playerData.setEventEnded();
            DATA.setDirty(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (++ticks % 10 == 5) {
                if (importData || DATA == null) {
                    initDATA(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0));
                }
                EventSaveData.EVENT_PLAYER_DATA playerData;
                for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    playerData = DATA.getPlayerData(player.getName());
                    if (playerData.canStartSearch()) {
                        for (EVENT test : EVENTS) {
                            if (test.side.isPlayerUpdateEvent() && test.canStartEvent(player, playerData)) {
                                test.startEvent(player);
                                break;
                            }
                        }
                    }
                }
                if (ticks % 50 == 5) {
                    EventSaveData.EVENT_WORLD_DATA worldData;
                    for (int worldID : DimensionManager.getStaticDimensionIDs()) {
                        worldData = DATA.getWorldData(worldID);
                        if (worldData.canStartSearch()) {
                            for (EVENT test : EVENTS) {
                                if (test.side.inWorldUpdateEvent() && worldData.worldCanStartEvent(test.eventID) && test.canStartEvent(worldID, worldData)) {
                                    test.startEvent(DimensionManager.getWorld(worldID));
                                    break;
                                }
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
            WORLDS_EVENT.removeIf(EventBase::serverHandler);
            if (!WORLDS_EVENT_ADD.isEmpty()) {
                WORLDS_EVENT.addAll(WORLDS_EVENT_ADD);
                WORLDS_EVENT_ADD.clear();
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Entity attacker = event.getEntity();
        if (!attacker.world.isRemote) {
            Entity target = event.getTarget();
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == attacker && eventBase.disableAttack(target)) {
                    event.setCanceled(true);
                }
            }
            for (EVENT test : EVENTS) {
                if (test.side.isInteractEvent() && test.canStartEvent(attacker, target)) {
                    if (test.side.isForAll()) {
                        test.startEventZone(attacker);
                    } else if (attacker instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) attacker;
                        if (DATA.getPlayerData(player.getName()).playerCanStartEvent(test.eventID)) {
                            test.startEvent(player);
                        }
                    } else if (target instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) target;
                        if (DATA.getPlayerData(player.getName()).playerCanStartEvent(test.eventID)) {
                            test.startEvent(player);
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDeathEvent(LivingDeathEvent event) {
        EntityLivingBase deadEntity = event.getEntityLiving();
        if (!deadEntity.world.isRemote) {
            for (EVENT test : EVENTS) {
                if (test.side.isInteractEvent() && test.canStartEvent(deadEntity, event.getSource())) {
                    if (test.side.isForAll()) {
                        test.startEventZone(deadEntity);
                    } else if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
                        if (DATA.getPlayerData(player.getName()).playerCanStartEvent(test.eventID)) {
                            test.startEvent(player);
                        }
                    } else if (deadEntity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) deadEntity;
                        if (DATA.getPlayerData(player.getName()).playerCanStartEvent(test.eventID)) {
                            test.startEvent(player);
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.player.getName());
        if (playerData.canStartSearch()) {
            for (EVENT test : EVENTS) {
                if (test.side.isOnlyPlayerInteract() && test.canStartEvent(event, playerData)) {
                    test.startEvent(event.player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTravelToDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == event.getEntity() && eventBase.disableChangeDimension(event)) {
                    event.setCanceled(true);
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

        public EventBase getEvent(EntityPlayer player) {
            return supplier.apply(player);
        }

        /**START*/
        public void startEvent(EntityPlayer player) {
            EventBase eventBase = this.getEvent(player);
            EventHandler.DATA.addPlayerEvent(player.getName(), this.eventID);
            Network.sendPlayerEvent(player, this.eventID);
            PLAYERS_EVENT_ADD.add(eventBase);
        }

        //RULES
        /**PLAYER_TICK*/
        public boolean canStartEvent(EntityPlayer player, EventSaveData.EVENT_PLAYER_DATA data) {
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

        /**Player_Change_Dimension*/
        public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
            if (data.playerCompletedEvent(this.eventID)) {
                return false;
            }
            for (ITestBase t : this.tests) {
                if (!t.canStartEvent(event, data)) {
                    return false;
                }
            }
            return true;
        }

        /**Hurt_Event*/
        public boolean canStartEvent(Entity attacker, Entity target) {
            for (ITestBase t : this.tests) {
                if (!t.canStartEvent(attacker, target)) {
                    return false;
                }
            }
            return true;
        }

        /**WORLD_TICK*/
        public boolean canStartEvent(int worldID, EventSaveData.EVENT_WORLD_DATA data) {
            for (ITestBase t : this.tests) {
                if (!t.canStartEvent(worldID, data)) {
                    return false;
                }
            }
            return true;
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

        /**ENTITY_KILLED*/
        public void startEventZone(Entity entity) {
            int radius = 24;
            int height = 12;
            this.startEventZone(entity.world, new AxisAlignedBB(entity.posX - radius, entity.posY - height, entity.posZ - radius, entity.posX + radius, entity.posY + height, entity.posZ + radius));
        }

        /**ENTITY_KILLED*/
        public void startEventZone(World world, AxisAlignedBB box) {
            for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, box)) {
                if (DATA.getPlayerData(player.getName()).playerCanStartEvent(this.eventID)) {
                    this.startEvent(player);
                }
            }
        }

        /**WORLD_TICK*/
        public void startEvent(@Nullable World world) {
            if (world == null) {
                return;
            }
            DATA.addWorldEvent(world.provider.getDimension(), this.eventID);
            EventBase base = this.getEvent(null);
            base.world = world;
            WORLDS_EVENT_ADD.add(base);
            /*for (EntityPlayer player : world.playerEntities) {
                if (DATA.getPlayerData(player.getName()).playerCanStartEvent(this.eventID)) {
                    this.startEvent(player);
                }
            }*/
        }
    }

    public enum SIDE {
        /**onPlayerUpdate*/
        PLAYER_TICK,
        /**onWorldUpdate*/
        WORLD_TICK,
        /**onEntityDeath, onEntityHurt for all players in the zone*/
        VOID_INTERACT,
        /**onPlayerKill, onPlayerHurt, onHurtPlayer, onPlayerDead for player*/
        PLAYER_INTERACT;
        public boolean isPlayerUpdateEvent() {
            return this == PLAYER_TICK;
        }

        public boolean inWorldUpdateEvent() {
            return this == WORLD_TICK;
        }

        public boolean isInteractEvent() {
            return this == VOID_INTERACT || this == PLAYER_INTERACT;
        }

        public boolean isOnlyPlayerInteract() {
            return this == PLAYER_INTERACT;
        }

        public boolean isForAll() {
            return this == VOID_INTERACT;
        }
    }
}
