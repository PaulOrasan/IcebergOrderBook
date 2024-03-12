import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBook {

    private final PriorityQueue<OrderBookPriceLevel> buyOrders;
    private final PriorityQueue<OrderBookPriceLevel> sellOrders;

    public OrderBook() {
        buyOrders = new PriorityQueue<>(Comparator.comparingInt(OrderBookPriceLevel::getPrice));
        sellOrders = new PriorityQueue<>(Comparator.comparingInt(OrderBookPriceLevel::getPrice).reversed());
    }

    public Order getTopBuyOrder() {
        if (!buyOrders.isEmpty()) {
            return buyOrders.peek().getTopOrder();
        }
        return null;
    }

    public Order getTopSellOrder() {
        if (!sellOrders.isEmpty()) {
            return sellOrders.peek().getTopOrder();
        }
        return null;
    }

    public void insertOrder(final Order order) {
    }

    public void notifyTradeEvent(final TradeEvent event) {

    }
}
