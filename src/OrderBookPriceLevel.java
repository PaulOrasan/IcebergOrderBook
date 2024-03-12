import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OrderBookPriceLevel {

    private final int price;
    private final TreeSet<Order> orders;

    public OrderBookPriceLevel(int price) {
        this.price = price;
        this.orders = new TreeSet<>(Comparator.comparingLong(Order::getTimestamp));
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
        return orders.first();
    }

    public void removeTopOrder() {
        orders.pollFirst();
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
}
