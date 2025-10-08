package energon.nebulalib.entity.data;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDataEvent {
    public record NLibRegisterAttributes_Pre() implements RecordEvent {
        public static final EventBus<@NotNull NLibRegisterAttributes_Pre> BUS = EventBus.create(NLibRegisterAttributes_Pre.class);
    }

    public record NLibRegisterAttributes_Post() implements RecordEvent {
        public static final EventBus<@NotNull NLibRegisterAttributes_Post> BUS = EventBus.create(NLibRegisterAttributes_Post.class);
    }
}
