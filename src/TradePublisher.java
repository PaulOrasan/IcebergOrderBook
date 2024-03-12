import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradePublisher {

    private final List<TradeEvent> events = new ArrayList<>();
    public TradePublisher() {

    }

    public void publishTrades(List<TradeEvent> events) {
        this.events.addAll(events);
    }

    public List<TradeEvent> getTradeEvents() {
        return Collections.unmodifiableList(events);
    }
}
