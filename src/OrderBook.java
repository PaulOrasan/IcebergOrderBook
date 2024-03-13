import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class OrderBook {

    private final TreeMap<Integer, OrderBookPriceLevel> buyOrders;
    private final TreeMap<Integer, OrderBookPriceLevel> sellOrders;

    public OrderBook() {
        buyOrders = new TreeMap<>(Comparator.reverseOrder());
        sellOrders = new TreeMap<>(Comparator.naturalOrder());
    }

    public Order getTopBuyOrder() {
        if (!buyOrders.isEmpty()) {
            return buyOrders.firstEntry().getValue().getTopOrder();
        }
        return null;
    }

    public List<Order> getBuyOrders() {
        final List<Order> orders = new ArrayList<>();
        buyOrders.descendingKeySet()
                .forEach(price -> orders.addAll(buyOrders.get(price).getOrders()));
        return orders;
    }

    public Order getTopSellOrder() {
        if (!sellOrders.isEmpty()) {
            return sellOrders.firstEntry().getValue().getTopOrder();
        }
        return null;
    }

    public List<Order> getSellOrders() {
        final List<Order> orders = new ArrayList<>();
        sellOrders.keySet()
                .forEach(price -> orders.addAll(sellOrders.get(price).getOrders()));
        return orders;
    }

    public void insertOrder(final Order order) {
        insertOrder(order.isBuyOrder() ? buyOrders : sellOrders, order);
    }

    public void notifyTradeEvent(final TradeEvent event) {
        if (getTopBuyOrder().getId() == event.getBuyTradeResult().getPredictedOrder().getId()) {
            removeTopOrder(buyOrders);
            if (event.getBuyTradeResult().getPredictedOrder().getAvailableQuantity() > 0) {
                insertOrder(event.getBuyTradeResult().getPredictedOrder());
            }
        }
        if (getTopSellOrder().getId() == event.getSellTradeResult().getPredictedOrder().getId()) {
            removeTopOrder(sellOrders);
            if (event.getSellTradeResult().getPredictedOrder().getAvailableQuantity() > 0) {
                insertOrder(event.getSellTradeResult().getPredictedOrder());
            }
        }
    }

    private void removeTopOrder(final TreeMap<Integer, OrderBookPriceLevel> orders) {
        var topEntry = orders.firstEntry();
        orders.remove(topEntry.getKey());
        topEntry.getValue().removeTopOrder();
        if (!topEntry.getValue().isEmpty()) {
            orders.put(topEntry.getKey(), topEntry.getValue());
        }
    }

    private void insertOrder(final TreeMap<Integer, OrderBookPriceLevel> orders, final Order orderToBeInserted) {
        if (orderToBeInserted.getAvailableQuantity() == 0) {
            return;
        }
        if (!orders.containsKey(orderToBeInserted.getPrice())) {
            orders.put(orderToBeInserted.getPrice(), new OrderBookPriceLevel(orderToBeInserted.getPrice()));
        }
        orders.get(orderToBeInserted.getPrice()).addOrder(orderToBeInserted);
    }
}
