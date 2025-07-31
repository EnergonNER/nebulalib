package energon.nebulalib.event.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

public class EVENT_Biome_Enter extends EVENT_Coth_FirstContact {
    public EVENT_Biome_Enter(@Nullable EntityPlayer p) {
        super(p);
        this.eventTime = 500;
    }

    @Override
    public void clientTick(EntityPlayer player) {
        if (this.phase == (byte) 0 && this.eventProgress > 10) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.biome_enter.title1"), "", 40, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 1 && this.eventProgress > 130) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.biome_enter.title2"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 2 && this.eventProgress > 230) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.biome_enter.title3"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 3 && this.eventProgress > 330) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.biome_enter.title4"), "", 20, 60, 20);
            this.phase++;
        } else if (this.phase == (byte) 4 && this.eventProgress > 430) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(I18n.translateToLocal("catalyst.event.biome_enter.title5"), "", 20, 60, 60);
            this.phase++;
        }
    }
}
