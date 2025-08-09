package energon.nebulalib.event.events;

import energon.nebulalib.handler.NLibSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class EVENT_Biome_Enter extends EVENT_Coth_FirstContact {
    public EVENT_Biome_Enter(@Nullable EntityPlayer p) {
        super(p);
        this.eventTime = 570;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientEventStart() {}

    public byte soundPhase = (byte) 0;

    @Override
    @SideOnly(Side.CLIENT)
    public void clientTick() {
        if (this.soundPhase == (byte) 0 && this.eventProgress >= 0) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(NLibSoundHandler.presence_start, 1F));//0,400
            });
            this.soundPhase++;
        } else if (this.soundPhase == (byte) 1 && this.eventProgress >= 340) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(NLibSoundHandler.presence_end, 1F));//-60,360
            });
            this.soundPhase++;
        }


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
