package energon.nebulalib;

import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = NebulaLib.MODID, name = NebulaLib.NAME, version = NebulaLib.VERSION)
public class NebulaLib {
    public static final String MODID = "nebulalib";
    public static final String NAME = "Nebula: Lib";
    public static final String VERSION = "1.0";
    @SidedProxy(clientSide = NebulaLib.CLIENT, serverSide = NebulaLib.SERVER)
    public static CommonProxy proxy;
    public static final String CLIENT = "energon.nebulalib.proxy.ClientProxy";
    public static final String SERVER = "energon.nebulalib.proxy.CommonProxy";
    @Mod.Instance
    public static NebulaLib instance;
    public static Logger logger;
    public static Configuration config;
    public static boolean EVENTS_ON = true;
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

    }

    @EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        proxy.serverStart(event);

    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        if (EVENTS_ON) {
            MinecraftForge.EVENT_BUS.register(NLibEventHandler.class);
            NLibEventHandler.serverStarted();
        }
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        if (EVENTS_ON) {
            NLibEventHandler.serverStopping();
            MinecraftForge.EVENT_BUS.unregister(NLibEventHandler.class);
        }
    }

    @EventHandler
    public void onServerStop(FMLServerStoppedEvent event) {
        proxy.serverStop(event);

    }
}
