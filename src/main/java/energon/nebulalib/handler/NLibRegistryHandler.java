package energon.nebulalib.handler;

import energon.nebulalib.NebulaLib;
import energon.nebulalib.init.NLibBlocks;
import energon.nebulalib.init.NLibItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class NLibRegistryHandler {
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(NLibItems.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(NLibBlocks.BLOCKS.toArray(new Block[0]));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for(Item item : NLibItems.ITEMS) {
			NebulaLib.proxy.registerModel(item, 0);
		}
		for(Block block : NLibBlocks.BLOCKS) {
			NebulaLib.proxy.registerModel(ItemBlock.getItemFromBlock(block), 0);
		}
	}
}
