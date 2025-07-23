package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;

public class TEST_PlayerBreakBlock implements ITestBase {
    public final Block block;
    public TEST_PlayerBreakBlock(Block block) {
        this.block = block;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getState().getBlock() == this.block;
    }
}
