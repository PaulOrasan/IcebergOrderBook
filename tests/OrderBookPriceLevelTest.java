import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class OrderBookPriceLevelTest {

    private OrderBookPriceLevel testPriceLevel = new OrderBookPriceLevel(OrderTestUtils.PRICE);


    @Test
    void addOrder() {
        final List<Order> orders = generateOrders(10);
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

    private static List<Order> generateOrders(final int numberOfOrders) {
        final List<Order> orders = new ArrayList<>();
        for (int i = 0; i < numberOfOrders; i++) {
            if (i % 2 == 0) {
                orders.add(OrderTestUtils.generateLimitOrder(i));
            } else {
                orders.add(OrderTestUtils.generateIcebergOrder(i));
            }
        }
        Collections.shuffle(orders);
        return orders;
    }
}
