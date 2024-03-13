import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class OrderBookTest {

    private static final int INVALID_ID = -1;
    private static final int NUMBER_OF_ORDERS = 1000;
    private static final int BUY_PRICE_BOUND = 90;
    private static final int SELL_SIDE_BOUND = 100;
    private static final int ORDER_TIMESTAMP = 1000;
    private static final int TRADE_TIMESTAMP = 2000;
    private static final Order AGGRESSIVE_ORDER = LimitOrder.newBuilderInstance()
            .withId(INVALID_ID)
            .withAggressiveStatus(true)
            .build();
    private OrderBook orderBook;
    private static List<Order> WARM_UP_ORDERS;


    @BeforeAll
    static void generalSetUp() {
        WARM_UP_ORDERS = OrderTestUtils.generateOrdersRandomPriceAndSide(NUMBER_OF_ORDERS, BUY_PRICE_BOUND, SELL_SIDE_BOUND);
    }

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        WARM_UP_ORDERS.forEach(orderBook::insertOrder);
    }

    @Test
    void insertOrder() {
        assertEquals(getExpectedBuyOrder(WARM_UP_ORDERS), orderBook.getTopBuyOrder());
        assertEquals(getExpectedSellOrder(WARM_UP_ORDERS), orderBook.getTopSellOrder());
    }

    @Test
    void getTopBuyOrder_emptyValue() {
        assertNull(new OrderBook().getTopBuyOrder());
    }

    @Test
    void getTopSellOrder_emptyValue() {
        assertNull(new OrderBook().getTopSellOrder());
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void notifyTradeEvent(final List<Order> inputOrders, final TradeEvent inputEvent, final Order expectedTopBuyOrder, final Order expectedTopSellOrder) {
        inputOrders.forEach(orderBook::insertOrder);

        orderBook.notifyTradeEvent(inputEvent);

        assertEquals(expectedTopBuyOrder, orderBook.getTopBuyOrder());
        assertEquals(expectedTopSellOrder, orderBook.getTopSellOrder());
    }

    private static Stream<Arguments> dataProvider() {
        final int price = (BUY_PRICE_BOUND + SELL_SIDE_BOUND) / 2;
        final LimitOrder limitOrder = LimitOrder.newBuilderInstance()
                .withId(OrderTestUtils.ORDER_ID)
                .withSide(Side.BUY)
                .withPrice(price)
                .withQuantity(OrderTestUtils.ORIGINAL_QUANTITY)
                .withTimestamp(ORDER_TIMESTAMP)
                .withAggressiveStatus(false)
                .build();
        final LimitOrder sellLimitOrder = LimitOrder.builderFromOrder(limitOrder)
                .withSide(Side.SELL)
                .build();
        final IcebergOrder topIcebergOrder = IcebergOrder.newBuilderInstance()
                .withId(OrderTestUtils.ORDER_ID)
                .withSide(Side.BUY)
                .withPrice(price)
                .withQuantity(OrderTestUtils.ORIGINAL_QUANTITY - IcebergOrderTest.MAX_PEAK_SIZE)
                .withTimestamp(ORDER_TIMESTAMP)
                .withAggressiveStatus(false)
                .withMaxPeakSize(IcebergOrderTest.MAX_PEAK_SIZE)
                .withCurrentPeakQuantity(IcebergOrderTest.MAX_PEAK_SIZE)
                .build();
        final IcebergOrder secondIcebergOrder = IcebergOrder.builderFromOrder(topIcebergOrder)
                .withTimestamp((ORDER_TIMESTAMP + TRADE_TIMESTAMP) / 2)
                .build();
        final List<Order> icebergOrders = List.of(topIcebergOrder, secondIcebergOrder);
        final IcebergOrder sellTopIcebergOrder = IcebergOrder.builderFromOrder(topIcebergOrder)
                .withSide(Side.SELL)
                .build();
        final IcebergOrder sellSecondIcebergOrder = IcebergOrder.builderFromOrder(secondIcebergOrder)
                .withSide(Side.SELL)
                .build();
        final List<Order> sellIcebergOrders = List.of(sellTopIcebergOrder, sellSecondIcebergOrder);

        return Stream.of(
                // BUY-SIDE
                scenario(limitOrder, partialTrade(limitOrder), partialTrade(limitOrder), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(limitOrder, fullTrade(limitOrder), getExpectedBuyOrder(WARM_UP_ORDERS), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(topIcebergOrder, partialPeakTrade(topIcebergOrder), partialPeakTrade(topIcebergOrder), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(topIcebergOrder, fullPeakTrade(topIcebergOrder), fullPeakTrade(topIcebergOrder), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(topIcebergOrder, fullTrade(topIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(icebergOrders, partialPeakTrade(topIcebergOrder), partialPeakTrade(topIcebergOrder), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(icebergOrders, fullPeakTrade(topIcebergOrder), secondIcebergOrder, getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(icebergOrders, fullTrade(topIcebergOrder), secondIcebergOrder, getExpectedSellOrder(WARM_UP_ORDERS)),

                // SELL-SIDE
                scenario(sellLimitOrder, partialTrade(sellLimitOrder), getExpectedBuyOrder(WARM_UP_ORDERS), partialTrade(sellLimitOrder)),
                scenario(sellLimitOrder, fullTrade(sellLimitOrder), getExpectedBuyOrder(WARM_UP_ORDERS), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(sellTopIcebergOrder, partialPeakTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), partialPeakTrade(sellTopIcebergOrder)),
                scenario(sellTopIcebergOrder, fullPeakTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), fullPeakTrade(sellTopIcebergOrder)),
                scenario(sellTopIcebergOrder, fullTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), getExpectedSellOrder(WARM_UP_ORDERS)),
                scenario(sellIcebergOrders, partialPeakTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), partialPeakTrade(sellTopIcebergOrder)),
                scenario(sellIcebergOrders, fullPeakTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), sellSecondIcebergOrder),
                scenario(sellIcebergOrders, fullTrade(sellTopIcebergOrder), getExpectedBuyOrder(WARM_UP_ORDERS), sellSecondIcebergOrder)

        );
    }

    private static Arguments scenario(Order inputOrder, Order expectedOrderChange, Order expectedBuyOrder, Order expectedSellOrder) {
        return scenario(List.of(inputOrder), buildTradeEvent(expectedOrderChange), expectedBuyOrder, expectedSellOrder);
    }

    private static Arguments scenario(List<Order> inputOrders, Order expectedOrderChange, Order expectedBuyOrder, Order expectedSellOrder) {
        return scenario(inputOrders, buildTradeEvent(expectedOrderChange), expectedBuyOrder, expectedSellOrder);
    }

    private static Arguments scenario(List<Order> inputOrders, TradeEvent inputEvent, Order expectedBuyOrder, Order expectedSellOrder) {
        return Arguments.of(inputOrders, inputEvent, expectedBuyOrder, expectedSellOrder);
    }

    private static Order partialTrade(final LimitOrder limitOrder) {
        return LimitOrder.builderFromOrder(limitOrder)
                .withQuantity(limitOrder.getQuantity() / 2)
                .build();
    }

    private static Order fullTrade(final LimitOrder limitOrder) {
        return LimitOrder.builderFromOrder(limitOrder)
                .withQuantity(0)
                .withTimestamp(TRADE_TIMESTAMP)
                .build();
    }

    private static Order partialPeakTrade(final IcebergOrder icebergOrder) {
        return IcebergOrder.builderFromOrder(icebergOrder)
                .withCurrentPeakQuantity(icebergOrder.getAvailableQuantity() / 2)
                .build();
    }

    private static Order fullPeakTrade(final IcebergOrder icebergOrder) {
        return IcebergOrder.builderFromOrder(icebergOrder)
                .withQuantity(icebergOrder.getQuantity() - icebergOrder.getMaxPeakSize())
                .withCurrentPeakQuantity(icebergOrder.getMaxPeakSize())
                .withTimestamp(TRADE_TIMESTAMP)
                .build();
    }

    private static Order fullTrade(final IcebergOrder icebergOrder) {
        return IcebergOrder.builderFromOrder(icebergOrder)
                .withQuantity(0)
                .withCurrentPeakQuantity(0)
                .withTimestamp(TRADE_TIMESTAMP)
                .build();
    }

    private static TradeEvent buildTradeEvent(final Order predictedOrder) {
        final Order buySidePrediction = predictedOrder.isBuyOrder() ? predictedOrder : AGGRESSIVE_ORDER;
        final Order sellSidePrediction = !predictedOrder.isBuyOrder() ? predictedOrder : AGGRESSIVE_ORDER;
        return new TradeEvent(buySidePrediction.getId(), sellSidePrediction.getId(), buySidePrediction.getPrice(), buySidePrediction.getQuantity(),
                new TradeResult(buySidePrediction), new TradeResult(sellSidePrediction));
    }

    private static Order getExpectedBuyOrder(final List<Order> inputOrders) {
        return inputOrders.stream()
                .filter(order -> Side.BUY.equals(order.getSide()))
                .max((order1, order2) -> {
                    if (order1.getPrice() == order2.getPrice()) {
                        return -Long.compare(order1.getTimestamp(), order2.getTimestamp());
                    }
                    return Integer.compare(order1.getPrice(), order2.getPrice());
                })
                .orElseThrow();
    }

    private static Order getExpectedSellOrder(final List<Order> inputOrders) {
        return inputOrders.stream()
                .filter(order -> Side.SELL.equals(order.getSide()))
                .max((order1, order2) -> {
                    if (order1.getPrice() == order2.getPrice()) {
                        return -Long.compare(order1.getTimestamp(), order2.getTimestamp());
                    }
                    return -Integer.compare(order1.getPrice(), order2.getPrice());
                })
                .orElseThrow();
    }
}
