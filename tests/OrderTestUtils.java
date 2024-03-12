import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderTestUtils {

    public static final int ORDER_ID = 12375;
    public static final int PRICE = 89;
    public static final int ORIGINAL_QUANTITY = 1000;
    public static final int ORIGINAL_TIMESTAMP = 10000;
    private static final int MAX_PEAK_SIZE = 100;


    public static List<Order> generateOrdersSamePrice(final int numberOfOrders) {
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

    public static List<Order> generateOrdersRandomPriceAndSide(final int numberOfOrders, final int buyPriceBound, final int sellPriceBound) {
        final Random randomNumberGenerator = new Random();
        final List<Order> orders = new ArrayList<>();
        for (int i = 0; i < numberOfOrders; i++) {
            if (i % 2 == 0) {
                final Side side = i % 4 == 0 ? Side.BUY : Side.SELL;
                orders.add(generateLimitOrder(side, getRandomPriceBySide(side, randomNumberGenerator, buyPriceBound, sellPriceBound), i));
            } else {
                final Side side = i % 3 == 0 ? Side.BUY : Side.SELL;
                orders.add(generateIcebergOrder(side, getRandomPriceBySide(side, randomNumberGenerator, buyPriceBound, sellPriceBound), i));
            }
        }
        Collections.shuffle(orders);
        return orders;
    }

    private static int getRandomPriceBySide(Side side, Random generator, int buyPriceBound, int sellPriceBound) {
        if (Side.BUY.equals(side)) {
            return generator.nextInt(buyPriceBound);
        }
        return sellPriceBound + generator.nextInt(buyPriceBound);
    }

    public static LimitOrder generateLimitOrder(Side side, int price, int timestamp) {
        return generateLimitOrder(timestamp, side, price, ORIGINAL_QUANTITY, timestamp, false);
    }

    public static LimitOrder generateLimitOrder(int timestamp) {
        return generateLimitOrder(timestamp, Side.BUY, PRICE, ORIGINAL_QUANTITY, timestamp, false);
    }

    public static LimitOrder generateLimitOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive) {
        return new LimitOrder(id, side, price, quantity, timestamp, isAggressive);
    }

    public static IcebergOrder generateIcebergOrder(Side side, int price, int timestamp) {
        return generateIcebergOrder(timestamp, side, price, ORIGINAL_QUANTITY, timestamp, false, MAX_PEAK_SIZE);
    }

    public static IcebergOrder generateIcebergOrder(int timestamp) {
        return generateIcebergOrder(timestamp, Side.BUY, PRICE, ORIGINAL_QUANTITY, timestamp, false, MAX_PEAK_SIZE);
    }

    public static IcebergOrder generateIcebergOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive,
                                                    int maxPeakQuantity) {
        return new IcebergOrder(id, side, price, quantity, timestamp, isAggressive, maxPeakQuantity);
    }
}
