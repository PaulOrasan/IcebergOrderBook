import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class OrderBookPriceLevelTest {

    private OrderBookPriceLevel testPriceLevel = new OrderBookPriceLevel(OrderTestUtils.PRICE);


    @Test
    void addOrder() {
        final List<Order> orders = OrderTestUtils.generateOrdersSamePrice(10);
        orders.forEach(testPriceLevel::addOrder);
        final List<Order> actualOrders = new ArrayList<>();
        while (!testPriceLevel.isEmpty()) {
            actualOrders.add(testPriceLevel.getTopOrder());
            testPriceLevel.removeTopOrder();
        }
        validateActualOrders(actualOrders);
    }

    private void validateActualOrders(final List<Order> orders) {
        final List<Integer> timestamps = orders.stream()
                .map(Order::getTimestamp)
                .collect(Collectors.toList());
        assertEquals(timestamps.stream()
                .sorted()
                .collect(Collectors.toList()), timestamps);
    }
}
