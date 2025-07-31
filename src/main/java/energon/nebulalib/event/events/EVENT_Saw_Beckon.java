package energon.nebulalib.event.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class EVENT_Saw_Beckon extends EVENT_Coth_FirstContact {
    public EVENT_Saw_Beckon(@Nullable EntityPlayer p) {
        super(p);
        this.eventTime = 560;
    }

    @Override
    public void clientTick(EntityPlayer player) {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title1"), "", 40, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 130) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title2"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 2 && this.eventProgress > 230) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title3"), "", 20, 40, 20);
            this.phase++;
        } else if (this.phase == (byte) 3 && this.eventProgress > 310) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title4"), "", 20, 40, 20);
            this.phase++;
        } else if (this.phase == (byte) 4 && this.eventProgress > 390) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title5"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 5 && this.eventProgress > 490) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.saw_beckon.title6"), "", 20, 60, 40);
            this.phase++;
        }
    }
}
