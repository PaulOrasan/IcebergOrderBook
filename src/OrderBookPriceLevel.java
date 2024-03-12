import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBookPriceLevel {

    private final int price;
    private final PriorityQueue<Order> orders;

    public OrderBookPriceLevel(int price) {
        this.price = price;
        this.orders = new PriorityQueue<>(Comparator.comparingInt(Order::getTimestamp));
    }

    public int getPrice() {
        return price;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public Order getTopOrder() {
        return orders.peek();
    }

    public void removeTopOrder() {
        orders.poll();
    }
}
