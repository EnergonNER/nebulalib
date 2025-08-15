package energon.nebulalib.proxy;

import energon.nebulalib.event.NLibEventHandler;
import energon.nebulalib.network.NLibNetwork;
import energon.nebulalib.handler.NLibSoundHandler;
import energon.nebulalib.inject.SRPInject;
import energon.nebulalib.config.NLibConfig;
import energon.nebulalib.network.NLibEventCommand;
import energon.nebulalib.structure.NLibStructureHandler;
import energon.nebulalib.util.NLibFileUtilities;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.*;

import java.io.File;

public class CommonProxy {
	public void registerModel(Item item, int metadata) {}

	public void preInit(FMLPreInitializationEvent event) {
		NLibConfig.registerConfig(event);
		NLibNetwork.init();
	}

	public void init(FMLInitializationEvent event) {
		NLibSoundHandler.registerSounds();
		File configDir = Loader.instance().getConfigDir();
		File nebulaGenDir = new File(configDir, "nebulalib/generators/nebula");
		if (!nebulaGenDir.exists()) {
			System.out.println("NEBULA: LIB COPY FROM MOD START   ");
			switch (NLibFileUtilities.copyFromMod("nebulalib/custom/", "simple.json", nebulaGenDir, false)) {
				case 0:
					System.out.println("NEBULA: LIB > simple.json SUCCESSFUL   ");
					break;
				case 1:
					System.out.println("NEBULA: LIB > simple.json ERROR!!!   ");
					break;
				case 2:
					System.out.println("NEBULA: LIB > simple.json FILE EXIST!!!   ");
					break;
				case 3:
					System.out.println("NEBULA: LIB > simple.json FILE PATH ERROR!!!   ");
					break;
				case 4:
					System.out.println("NEBULA: LIB > simple.json INPUT = NULL!!!   ");
					break;
			}
			System.out.println("NEBULA: LIB COPY FROM MOD END   ");
		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		SRPInject.inj();
		NLibEventHandler.init();
		NLibStructureHandler.init();
	}

	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new NLibEventCommand());

	}

	public void serverStop(FMLServerStoppedEvent event) {


	}

	public void eventHandler(int eventID) {


	}
}
