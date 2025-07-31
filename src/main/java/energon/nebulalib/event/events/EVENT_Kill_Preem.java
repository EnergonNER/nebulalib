package energon.nebulalib.event.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class EVENT_Kill_Preem extends EVENT_Coth_FirstContact {
    public EVENT_Kill_Preem(@Nullable EntityPlayer p) {
        super(p);
        this.eventTime = 1000;
    }

    @Override
    public void clientTick(EntityPlayer player) {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title1"), "", 40, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 130) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title2"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 2 && this.eventProgress > 230) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title3"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 3 && this.eventProgress > 330) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title4"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 4 && this.eventProgress > 430) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title5"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 5 && this.eventProgress > 530) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title6"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 6 && this.eventProgress > 630) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title7"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 7 && this.eventProgress > 730) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title8"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 8 && this.eventProgress > 830) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title9"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 9 && this.eventProgress > 930) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.kill_preem.title10"), "", 20, 60, 40);
            this.phase++;
        }
    }
}
