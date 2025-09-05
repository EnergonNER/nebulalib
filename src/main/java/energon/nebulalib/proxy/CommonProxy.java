package energon.nebulalib.proxy;

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
		if (NLibConfig.STRUCTURES_ON) {
			NLibStructureHandler.writeStaticValues();
		}
	}

	public void init(FMLInitializationEvent event) {
		NLibSoundHandler.registerSounds();
		if (NLibConfig.STRUCTURES_ON) {
			NLibStructureHandler.copy();
		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		SRPInject.inj();
		NLibEventHandler.init();
		if (NLibConfig.STRUCTURES_ON) {
			NLibStructureHandler.init();
		}
	}

	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new NLibEventCommand());
		if (NLibConfig.STRUCTURES_ON) {
			event.registerServerCommand(new NLibStructureCommand());
		}
	}

	public void serverStop(FMLServerStoppedEvent event) {


	}
}
