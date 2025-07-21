package energon.nebulalib.item;

import energon.nebulalib.init.NLibItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class NLibItemBase extends Item {
    public NLibItemBase(String name) {
        setUnlocalizedName("nebulalib." + name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.REDSTONE);
        NLibItems.ITEMS.add(this);
    }
}
