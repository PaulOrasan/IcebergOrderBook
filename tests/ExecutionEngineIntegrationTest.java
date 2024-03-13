import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ExecutionEngineIntegrationTest {

    private static final int NUMBER_OF_ORDERS = 1000;
    private static final int BUY_PRICE_BOUND = 90;
    private static final int AGGRESSIVE_PRICE = 95;
    private static final int SELL_PRICE_BOUND = 100;
    private static final int AGGRESSIVE_QUANTITY = 1000;

    private static final int ORDER_TIMESTAMP = 1000;
    private static final Random random = new Random();
    private static final LimitOrder LIMIT_ORDER = LimitOrder.newBuilderInstance()
            .withId(OrderTestUtils.ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(AGGRESSIVE_PRICE)
            .withQuantity(AGGRESSIVE_QUANTITY)
            .withTimestamp(ORDER_TIMESTAMP)
            .withAggressiveStatus(true)
            .build();
    private static final IcebergOrder ICEBERG_ORDER = IcebergOrder.newBuilderInstance()
            .withId(OrderTestUtils.ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(AGGRESSIVE_PRICE)
            .withQuantity(AGGRESSIVE_QUANTITY)
            .withTimestamp(ORDER_TIMESTAMP)
            .withAggressiveStatus(true)
            .withMaxPeakSize(100)
            .build();


    private OrderBook orderBook;
    private DataPublisher dataPublisher;
    private ExecutionEngine executionEngine;


    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        TradeGenerator tradeGenerator = new TradeGenerator();
        dataPublisher = new DataPublisher();
        executionEngine = new ExecutionEngine(orderBook, tradeGenerator, dataPublisher);
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void addOrder(final Order aggressiveOrder, final List<Order> passiveOrders, final List<TradeEvent> expectedTradeEvents) {
        passiveOrders.forEach(orderBook::insertOrder);
        executionEngine.addOrder(aggressiveOrder);

        final List<TradeEvent> actualTradeEvents = dataPublisher.getTradeEvents();

        assertEquals(expectedTradeEvents, actualTradeEvents);
    }

    @Test
    void addOrder_flowTest() {
        final Order passiveOrder = limitOrder(400).apply(LIMIT_ORDER);
        final IcebergOrder aggressiveIcebergOrder = IcebergOrder.builderFromOrder(ICEBERG_ORDER)
                .withSide(Side.SELL)
                .build();
        final LimitOrder aggressiveLimitOrder = LimitOrder.builderFromOrder(LIMIT_ORDER)
                .withQuantity(320)
                .build();

        executionEngine.addOrder(passiveOrder);

        assertTrue(dataPublisher.getTradeEvents().isEmpty());

        executionEngine.addOrder(LIMIT_ORDER);

        assertFalse(dataPublisher.getTradeEvents().isEmpty());
        assertEquals(LIMIT_ORDER.getId(), orderBook.getTopBuyOrder().getId());
        assertEquals(600, orderBook.getTopBuyOrder().getAvailableQuantity());

        executionEngine.addOrder(aggressiveIcebergOrder);

        assertEquals(aggressiveIcebergOrder.getId(), orderBook.getTopSellOrder().getId());
        assertEquals(aggressiveIcebergOrder.getMaxPeakSize(), orderBook.getTopSellOrder().getAvailableQuantity());
        assertEquals(300, orderBook.getTopSellOrder().getQuantity());

        executionEngine.addOrder(aggressiveLimitOrder);

        assertEquals(aggressiveIcebergOrder.getId(), orderBook.getTopSellOrder().getId());
        assertEquals(80, orderBook.getTopSellOrder().getAvailableQuantity());
        assertEquals(0, orderBook.getTopSellOrder().getQuantity());
    }

    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                scenario(LIMIT_ORDER, List.of(limitOrder(400), limitOrder(300), limitOrder(500)), expectedTrade(400), expectedTrade(300), expectedTrade(300)),
                scenario(LIMIT_ORDER, limitOrder(1000), expectedTrade(1000)),
                scenario(LIMIT_ORDER, icebergOrder(3000, 1500), expectedTrade(1000)),
                scenario(LIMIT_ORDER, icebergOrder(2000, 1000), expectedTrade(1000)),
                scenario(LIMIT_ORDER, icebergOrder(1000, 1000), expectedTrade(1000)),
                scenario(LIMIT_ORDER, List.of(icebergOrder(1000, 250, 4), icebergOrder(500, 250, 4), limitOrder(100, 3)), expectedTrade(500), expectedTrade(500)),
                scenario(LIMIT_ORDER, List.of(icebergOrder(1000, 250), limitOrder(500), icebergOrder(1000, 100)), expectedTrade(400), expectedTrade(500), expectedTrade(100)),
                scenario(LIMIT_ORDER, limitOrder(2000, -2)),
                //
                scenario(ICEBERG_ORDER, limitOrder(2000), expectedTrade(1000)),
                scenario(ICEBERG_ORDER, List.of(limitOrder(200), limitOrder(300), limitOrder(1000)), expectedTrade(200), expectedTrade(300), expectedTrade(500)),
                scenario(ICEBERG_ORDER, List.of(icebergOrder(1500, 400, 4), limitOrder(500, 3)), expectedTrade(1000)),
                scenario(ICEBERG_ORDER, List.of(icebergOrder(300, 100, 2), limitOrder(450, 2), icebergOrder(450, 150, 1)), expectedTrade(300), expectedTrade(450), expectedTrade(250)),
                scenario(ICEBERG_ORDER, List.of(icebergOrder(300, 100), limitOrder(300), icebergOrder(900, 150)), expectedTrade(300), expectedTrade(300), expectedTrade(400)),
                scenario(ICEBERG_ORDER, limitOrder(2000, -2))
        );
    }

    private static Arguments scenario(Order aggressiveOrder, Function<Order, Order> passiveOrderGenerator) {
        return Arguments.of(aggressiveOrder, List.of(passiveOrderGenerator.apply(aggressiveOrder)), List.of());
    }

    private static Arguments scenario(Order aggressiveOrder, Function<Order, Order> passiveOrderGenerator, BiFunction<Order, Order, TradeEvent> trade) {
        return scenario(aggressiveOrder, List.of(passiveOrderGenerator), trade);
    }

    @SafeVarargs
    private static Arguments scenario(Order aggressiveOrder, List<Function<Order, Order>> passiveOrderGenerators, BiFunction<Order, Order, TradeEvent>... trades) {
        final List<Order> passiveOrders = passiveOrderGenerators.stream()
                .map(generator -> generator.apply(aggressiveOrder))
                .collect(Collectors.toList());
        final List<TradeEvent> expectedTrades = new ArrayList<>();
        for (int i = 0; i < trades.length; i++) {
            expectedTrades.add(trades[i].apply(aggressiveOrder, passiveOrders.get(i)));
        }
        return Arguments.of(aggressiveOrder, passiveOrders, expectedTrades);
    }

    private static Function<Order, Order> limitOrder(int quantity) {
        return limitOrder(quantity, 1);
    }

    private static Function<Order, Order> limitOrder(int quantity, int priceDifference) {
        return order -> LimitOrder.builderFromOrder(order)
                .withId(random.nextInt())
                .withSide(order.isBuyOrder() ? Side.SELL : Side.BUY)
                .withPrice(order.isBuyOrder() ? order.getPrice() - priceDifference : order.getPrice() + priceDifference)
                .withQuantity(quantity)
                .withAggressiveStatus(false)
                .withTimestamp(TimeUtils.getCurrentTimestamp())
                .build();
    }

    private static Function<Order, Order> icebergOrder(int quantity, int maxPeakSize) {
        return icebergOrder(quantity, maxPeakSize, 1);
    }

    private static Function<Order, Order> icebergOrder(int quantity, int maxPeakSize, int priceDifference) {
        return order -> IcebergOrder.builderFromOrder(order)
                .withId(random.nextInt())
                .withSide(order.isBuyOrder() ? Side.SELL : Side.BUY)
                .withPrice(order.isBuyOrder() ? order.getPrice() - priceDifference : order.getPrice() + priceDifference)
                .withQuantity(quantity - maxPeakSize)
                .withAggressiveStatus(false)
                .withMaxPeakSize(maxPeakSize)
                .withCurrentPeakQuantity(maxPeakSize)
                .withTimestamp(TimeUtils.getCurrentTimestamp())
                .build();
    }

    private static BiFunction<Order, Order, TradeEvent> expectedTrade(int quantity) {
        return (aggressiveOrder, passiveOrder) -> new TradeEvent(aggressiveOrder.isBuyOrder() ? aggressiveOrder.getId() : passiveOrder.getId(),
                aggressiveOrder.isBuyOrder() ? passiveOrder.getId() : aggressiveOrder.getId(), passiveOrder.getPrice(), quantity, null, null);
    }
}
