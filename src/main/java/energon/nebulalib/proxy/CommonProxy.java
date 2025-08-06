package energon.nebulalib.proxy;

import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.network.NLibNetwork;
import energon.nebulalib.handler.NLibSoundHandler;
import energon.nebulalib.inject.SRPInject;
import energon.nebulalib.config.NLibConfig;
import energon.nebulalib.network.NLibEventCommand;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy {
	public void registerModel(Item item, int metadata) {}

	public void preInit(FMLPreInitializationEvent event) {
		NLibConfig.registerConfig(event);
		NLibNetwork.init();
	}

	public void init(FMLInitializationEvent event) {
		NLibSoundHandler.registerSounds();

	}

	public void postInit(FMLPostInitializationEvent event) {
		SRPInject.inj();
		NLibEventHandler.init();
	}

	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new NLibEventCommand());

	}

	public void serverStop(FMLServerStoppedEvent event) {


	}

	public void eventHandler(int eventID) {


	}
}
