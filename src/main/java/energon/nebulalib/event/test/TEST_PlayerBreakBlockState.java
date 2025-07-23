package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.world.BlockEvent;

public class TEST_PlayerBreakBlockState implements ITestBase {
    public final IBlockState state;
    public TEST_PlayerBreakBlockState(IBlockState state) {
        this.state = state;
    }

    @Override
    public boolean canStartEvent(BlockEvent.BreakEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getState() == this.state;
    }
}
