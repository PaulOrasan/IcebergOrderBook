public class OrderTestUtils {

    public static final int ORDER_ID = 12375;
    public static final int PRICE = 89;
    public static final int ORIGINAL_QUANTITY = 1000;
    public static final int ORIGINAL_TIMESTAMP = 10000;
    private static final int MAX_PEAK_SIZE = 100;

    public static LimitOrder generateLimitOrder(int timestamp) {
        return generateLimitOrder(ORDER_ID, Side.BUY, PRICE, ORIGINAL_QUANTITY, timestamp, false);
    }

    public static LimitOrder generateLimitOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive) {
        return new LimitOrder(id, side, price, quantity, timestamp, isAggressive);
    }

    public static IcebergOrder generateIcebergOrder(int timestamp) {
        return generateIcebergOrder(ORDER_ID, Side.BUY, PRICE, ORIGINAL_QUANTITY, timestamp, false, MAX_PEAK_SIZE);
    }

    public static IcebergOrder generateIcebergOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive,
                                                    int maxPeakQuantity) {
        return new IcebergOrder(id, side, price, quantity, timestamp, isAggressive, maxPeakQuantity);
    }


}
