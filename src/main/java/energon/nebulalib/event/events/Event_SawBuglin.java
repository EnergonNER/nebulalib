package energon.nebulalib.event.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Event_SawBuglin extends EventBase {
    public Event_SawBuglin(EntityPlayer player) {
        super(player, 1F / (20 * 10));
    }

    @Override
    public void serverTick() {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientTick(EntityPlayer player) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientEventStart(EntityPlayer player) {
        Minecraft.getMinecraft().ingameGUI.displayTitle("Buglin", "LOL", 20, 60, 20);
    }

    @Override
    public boolean canAttack(Entity target) {
        return false;
    }
}
