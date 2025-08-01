package energon.nebulalib.event;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import energon.nebulalib.event.events.*;
import energon.nebulalib.event.test.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
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
    public static boolean DEBUG = false;

    public final static List<EventBase> PLAYERS_EVENT = new ArrayList<>();
    public static List<EventBase> PLAYERS_EVENT_ADD = new ArrayList<>();

    public final static List<EventBase> WORLDS_EVENT = new ArrayList<>();
    public static List<EventBase> WORLDS_EVENT_ADD = new ArrayList<>();

    public static void init() {
        EVENTS.add(new EVENT(1, SIDE.PLAYER_TICK, RARITY.COMMON, EVENT_Coth_FirstContact::new, "coth_0", "", new TEST_Delay(4), new TEST_EvoPhase(0, 0), new TEST_PlayerHasPotionEffect(SRPPotions.COTH_E)));
        EVENTS.add(new EVENT(2, SIDE.PLAYER_TICK, RARITY.COMMON, EVENT_Coth_Again::new, "coth_1", "", new TEST_Delay(4), new TEST_EvoPhase(1, 1), new TEST_PlayerHasPotionEffect(SRPPotions.COTH_E)));
        EVENTS.add(new EVENT(3, SIDE.PLAYER_TICK, RARITY.COMMON, EVENT_Coth_Another::new, "coth_2", "", new TEST_Delay(4), new TEST_EvoPhase(2, 2), new TEST_PlayerHasPotionEffect(SRPPotions.COTH_E)));
        EVENTS.add(new EVENT(4, SIDE.PLAYER_TICK, RARITY.COMMON, EVENT_Saw_Beckon::new, "saw_beckon", "", new TEST_PlayerLooksAtEntity(EntityPStationaryArchitect.class, 0.035)));
        EVENTS.add(new EVENT(5, SIDE.PLAYER_TICK, RARITY.COMMON, EVENT_Biome_Enter::new, "biome_enter", "", new TEST_Delay(4), new TEST_PlayerInBiome(SRPBiomes.biomeInfested)));
        EVENTS.add(new EVENT(6, SIDE.PLAYER_INTERACT, RARITY.RARE, EVENT_Node_Destroyed::new, "node_destroyed", "", new TEST_PlayerBreakBlock(SRPBlocks.BiomeHeart)));
        EVENTS.add(new EVENT(7, SIDE.PLAYER_INTERACT, RARITY.RARE, EVENT_City_Exit::new, "city_exit", "", new TEST_PlayerChangeDimension(0, 111)));
        EVENTS.add(new EVENT(8, SIDE.PLAYER_INTERACT, RARITY.RARE, EVENT_Kill_Preem::new, "kill_preem", "", new TEST_PlayerKillEntity(EntityPPreeminent.class)));
    }

    public static void serverStarted() {
        ticks = -200;
        importData = true;
        if (DEBUG) {
            for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                player.sendMessage(new TextComponentString("(EventHandler) Server Started!"));
            }
        }
        DEBUG = false;
    }

    public static void serverStopping() {
        if (DEBUG) {
            for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                player.sendMessage(new TextComponentString("(EventHandler) Server Stopping!"));
            }
        }
        ticks = -200;
        DATA = null;
        DEBUG = false;
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
            worldData = DATA.getWorldData(worldID, true);
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
        if (DEBUG) {
            for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                player.sendMessage(new TextComponentString("(EventHandler) Data initialized!"));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (importData || DATA == null) {
            initDATA(event.player.world);
        }
        EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.player.getName(), true);
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
                    playerData = DATA.getPlayerData(player.getName(), true);
                    for (EVENT test : EVENTS) {
                        if (test.side.isPlayerUpdateEvent() && playerData.canStartSearch(test.rarity) && test.canStartEvent(player, playerData)) {
                            test.startEvent(player);
                            break;
                        }
                    }
                }
                if (ticks % 50 == 5) {
                    EventSaveData.EVENT_WORLD_DATA worldData;
                    for (int worldID : DimensionManager.getStaticDimensionIDs()) {
                        worldData = DATA.getWorldData(worldID, true);
                        for (EVENT test : EVENTS) {
                            if (test.side.isWorldUpdateEvent() && worldData.canStartSearch(test.rarity) && worldData.worldCanStartEvent(test.eventID) && test.canStartEvent(worldID, worldData)) {
                                test.startEvent(DimensionManager.getWorld(worldID));
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
                if (eventBase.player == attacker && eventBase.disableAttack(event)) {
                    event.setCanceled(true);
                } else if (eventBase.player == target && eventBase.disableGetDamage(event)) {
                    event.setCanceled(true);
                }
            }
            if (!event.isCanceled()) {
                for (EVENT test : EVENTS) {
                    if (test.side.isInteractEvent() && test.canStartEvent(attacker, target)) {
                        if (test.side.isForAll()) {
                            test.startEventZone(attacker);
                        } else if (attacker instanceof EntityPlayer) {
                            EntityPlayer player = (EntityPlayer) attacker;
                            if (DATA.getPlayerData(player.getName(), true).playerCanStartEvent(test.eventID, test.rarity)) {
                                test.startEvent(player);
                            }
                        } else if (target instanceof EntityPlayer) {
                            EntityPlayer player = (EntityPlayer) target;
                            if (DATA.getPlayerData(player.getName(), true).playerCanStartEvent(test.eventID, test.rarity)) {
                                test.startEvent(player);
                            }
                        }
                        break;
                    }
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
                        if (DATA.getPlayerData(player.getName(), true).playerCanStartEvent(test.eventID, test.rarity)) {
                            test.startEvent(player);
                        }
                    } else if (deadEntity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) deadEntity;
                        if (DATA.getPlayerData(player.getName(), true).playerCanStartEvent(test.eventID, test.rarity)) {
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
        EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.player.getName(), true);
        for (EVENT test : EVENTS) {
            if (test.side.isOnlyPlayerInteract() && playerData.canStartSearch(test.rarity) && test.canStartEvent(event, playerData)) {
                test.startEvent(event.player);
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

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote) {
            //MAYBE CHANGE
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == event.getPlayer() && eventBase.disableBreakBlock(event)) {
                    event.setCanceled(true);
                }
            }
            if (!event.isCanceled()) {
                EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.getPlayer().getName(), true);
                for (EVENT test : EVENTS) {
                    if (test.side.isOnlyPlayerInteract() && playerData.canStartSearch(test.rarity) && test.canStartEvent(event, playerData)) {
                        test.startEvent(event.getPlayer());
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == event.getEntity() && eventBase.disablePlaceBlock(event)) {
                    event.setCanceled(true);
                }
            }
            if (!event.isCanceled()) {
                EventSaveData.EVENT_PLAYER_DATA playerData = DATA.getPlayerData(event.getEntity().getName(), true);
                for (EVENT test : EVENTS) {
                    if (test.side.isOnlyPlayerInteract() && playerData.canStartSearch(test.rarity) && test.canStartEvent(event, playerData)) {
                        test.startEvent((EntityPlayer) event.getEntity());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            List<EventBase> local = new ArrayList<>(PLAYERS_EVENT);
            for (EventBase eventBase : local) {
                if (eventBase.player == event.getEntityPlayer() && eventBase.disableInteractBlock(event)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public static class EVENT {
        public final int eventID;
        public final SIDE side;
        public final RARITY rarity;
        public final Function<EntityPlayer,EventBase> supplier;
        public final ITestBase[] tests;
        public final String name;
        public final String description;
        public EVENT(int id, SIDE side, RARITY r, Function<EntityPlayer,EventBase> s, String name, String description, ITestBase... t) {
            this.eventID = id;
            this.side = side;
            this.rarity = r;
            this.supplier = s;
            this.name = name;
            this.description = description;
            this.tests = t;
        }

        public EventBase getEvent(EntityPlayer player) {
            EventBase base = supplier.apply(player);
            base.eventID = this.eventID;
            return base;
        }

        /**START*/
        public void startEvent(EntityPlayer player) {
            for (EventBase eventTEST : PLAYERS_EVENT) {
                if (eventTEST.player != null && eventTEST.player.getName().equals(player.getName())) {
                    eventTEST.eventProgress += eventTEST.eventTime;
                    eventTEST.saveData();
                    eventTEST.player = null;
                }
            }

            EventBase eventBase = this.getEvent(player);
            EventHandler.DATA.addPlayerEvent(player.getName(), this.eventID, this.rarity);
            Network.sendPlayerEvent(player, this.eventID);
            PLAYERS_EVENT_ADD.add(eventBase);

            if (DEBUG) {
                for (EntityPlayer FMLPlayer : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    FMLPlayer.sendMessage(new TextComponentString("(EventHandler) Player - \"" + player.getName() + "\"  started event – \"" + this.name + "\""));
                }
            }
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

        /**Player_Break_Block*/
        public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
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

        /**Player_Place_Block*/
        public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
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
                if (DATA.getPlayerData(player.getName(), true).playerCanStartEvent(this.eventID, this.rarity)) {
                    this.startEvent(player);
                }
            }
        }

        /**WORLD_TICK*/
        public void startEvent(@Nullable World world) {
            if (world == null) {
                return;
            }

            for (EventBase eventBase : WORLDS_EVENT) {
                if (eventBase.world != null && eventBase.world.provider.getDimension() == world.provider.getDimension() && eventBase.eventID == this.eventID) {
                    eventBase.eventProgress += eventBase.eventTime;
                    eventBase.saveData();
                    eventBase.world = null;
                }
            }

            DATA.addWorldEvent(world.provider.getDimension(), this.eventID, this.rarity);
            EventBase base = this.getEvent(null);
            base.world = world;
            WORLDS_EVENT_ADD.add(base);

            if (DEBUG) {
                for (EntityPlayer FMLPlayer : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                    FMLPlayer.sendMessage(new TextComponentString("(EventHandler) World - \"" + world.provider.getDimension() + "\"  started event – \"" + this.name + "\""));
                }
            }
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

        public boolean isWorldUpdateEvent() {
            return this == WORLD_TICK;
        }

        public boolean isPlayerEvent() {
            return this != WORLD_TICK;
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

    public static RARITY getRarityByName(String name) {
        for (RARITY rar : RARITY.values()) {
            if (rar.name.equals(name)) {
                return rar;
            }
        }
        return RARITY.COMMON;
    }

    public enum RARITY {
        COMMON("common", 0),
        RARE("rare", 1),
        EPIC("epic", 2),
        LEGENDARY("legendary", 3);
        public final String name;
        public final byte lvl;
        RARITY(String name, int lvl) {
            this.name = name;
            this.lvl = (byte) lvl;
        }

        public boolean canChangeEvent(RARITY next) {
            return this.lvl < next.lvl;
        }
    }
}
