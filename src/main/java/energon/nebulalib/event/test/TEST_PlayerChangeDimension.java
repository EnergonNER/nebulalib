package energon.nebulalib.event.test;

import energon.nebulalib.event.EventSaveData;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TEST_PlayerChangeDimension implements ITestBase {
    public final int targetDimID;
    public Integer fromDimID;
    public TEST_PlayerChangeDimension(int target) {
        this.targetDimID = target;
    }

    public TEST_PlayerChangeDimension(int target, int from) {
        this(target);
        this.fromDimID = from;
    }

    @Override
    public boolean canStartEvent(PlayerEvent.PlayerChangedDimensionEvent event, EventSaveData.EVENT_PLAYER_DATA data) {
        return (this.fromDimID != null) ? ((event.fromDim == this.fromDimID) && (event.toDim == this.targetDimID)) : (event.toDim == this.targetDimID);
    }
}
