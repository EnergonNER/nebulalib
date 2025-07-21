package energon.nebulalib.proxy;

import energon.nebulalib.event.EventHandler;
import energon.nebulalib.event.Network;
import energon.nebulalib.inject.SRPInject;
import energon.nebulalib.config.NLibConfig;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy {
	public void registerModel(Item item, int metadata) {}

	public void preInit(FMLPreInitializationEvent event) {
		NLibConfig.registerConfig(event);
		Network.init();
	}

	public void init(FMLInitializationEvent event) {


	}

	public void postInit(FMLPostInitializationEvent event) {
		SRPInject.inj();
		EventHandler.init();
	}

	public void serverStart(FMLServerStartingEvent event) {


	}

	public void serverStop(FMLServerStoppedEvent event) {


	}

	public void eventHandler(int eventID) {


	}
}
