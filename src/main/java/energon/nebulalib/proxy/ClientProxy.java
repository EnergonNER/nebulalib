package energon.nebulalib.proxy;

import energon.nebulalib.handler.NLibRenderHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.*;

import java.util.Objects;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerModel(Item item, int metadata) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		NLibRenderHandler.registerEntityRenders();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		super.serverStarting(event);

	}

	@Override
	public void serverStop(FMLServerStoppedEvent event) {
		super.serverStop(event);

	}
}
