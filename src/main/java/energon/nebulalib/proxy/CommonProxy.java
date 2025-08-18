package energon.nebulalib.proxy;

import energon.nebulalib.NebulaLib;
import energon.nebulalib.entity.spawn.DefaultEntitySpawnRules;
import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.network.NLibNetwork;
import energon.nebulalib.handler.NLibSoundHandler;
import energon.nebulalib.inject.SRPInject;
import energon.nebulalib.config.NLibConfig;
import energon.nebulalib.network.NLibEventCommand;
import energon.nebulalib.network.NLibStructureCommand;
import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.structure.spawn.DefaultStructureSpawnRules;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy {
	public void registerModel(Item item, int metadata) {}

	public void preInit(FMLPreInitializationEvent event) {
		NLibConfig.registerConfig(event);
		NLibNetwork.init();
		DefaultEntitySpawnRules.writeStaticValues();
		DefaultStructureSpawnRules.writeStaticValues();
		if (NebulaLib.STRUCTURES_ON) {
			NLibStructureHandler.writeStaticValues();
		}
	}

	public void init(FMLInitializationEvent event) {
		NLibSoundHandler.registerSounds();
		if (NebulaLib.STRUCTURES_ON) {
			NLibStructureHandler.copy();
		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		SRPInject.inj();
		NLibEventHandler.init();
		if (NebulaLib.STRUCTURES_ON) {
			NLibStructureHandler.init();
		}
	}

	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new NLibEventCommand());
		event.registerServerCommand(new NLibStructureCommand());
		if (NebulaLib.STRUCTURES_ON) {

		}
	}

	public void serverStop(FMLServerStoppedEvent event) {


	}

	public void eventHandler(int eventID) {


	}
}
