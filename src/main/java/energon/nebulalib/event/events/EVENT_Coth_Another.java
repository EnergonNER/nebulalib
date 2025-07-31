package energon.nebulalib.event.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class EVENT_Coth_Another extends EVENT_Coth_FirstContact {
    public EVENT_Coth_Another(@Nullable EntityPlayer p) {
        super(p);
        this.eventTime = 440;
    }

    @Override
    public void clientTick(EntityPlayer player) {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase2.title1"), "", 40, 80, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 150) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase2.title2"), "", 20, 80, 20);
            this.phase++;
        } else if (this.phase == (byte) 2 && this.eventProgress > 270) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase2.title3"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 3 && this.eventProgress > 370) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.coth_phase2.title4"), "", 20, 60, 40);
            this.phase++;
        }
    }
}
