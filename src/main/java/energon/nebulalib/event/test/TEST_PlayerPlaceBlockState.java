package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.world.BlockEvent;

public class TEST_PlayerPlaceBlockState implements ITestBase {
    public final IBlockState state;
    public TEST_PlayerPlaceBlockState(IBlockState state) {
        this.state = state;
    }

    @Override
    public boolean canStartEvent(BlockEvent.EntityPlaceEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return event.getState() == this.state;
    }
}
