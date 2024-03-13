import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataPublisher {

    private final OutputAdapter adapter;
    private final List<TradeEvent> events = new ArrayList<>();

    public DataPublisher(OutputAdapter adapter) {
        this.adapter = adapter;
    }

    public DataPublisher() {
        adapter = null;
    }

    public void publishTrades(List<TradeEvent> events) {
        this.events.addAll(events);
        if (adapter != null) {
            events.forEach(adapter::writeTrade);
        }
    }

    public void publishOrderBook(List<Order> buyOrders, List<Order> sellOrders) {
        if (adapter != null) {
            adapter.displayOrderBookHeader();
            int maxOrdersSize = Math.max(buyOrders.size(), sellOrders.size());
            for (int i = 0; i < maxOrdersSize; i++) {
                Order buyOrder = i < buyOrders.size() ? buyOrders.get(i) : null;
                Order sellOrder = i < sellOrders.size() ? sellOrders.get(i) : null;
                adapter.displayOrderPair(buyOrder, sellOrder);
            }
            adapter.displayFinalOrderBook();
        }
    }

    public List<TradeEvent> getTradeEvents() {
        return Collections.unmodifiableList(events);
    }
}
