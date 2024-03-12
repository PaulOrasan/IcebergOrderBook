import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TradeGeneratorTest {

    private static final int PRICE = 100;
    private static final int QUANTITY = 100;
    private static final int TIMESTAMP = 10;

    private final TradeGenerator generator = new TradeGenerator();

    @ParameterizedTest
    @MethodSource("dataProvider")
    void generatePrediction(final Order aggressiveOrder, final Order passiveOrder, final TradePrediction expectedTrade) {
        assertEquals(expectedTrade, generator.generatePrediction(aggressiveOrder, passiveOrder));
    }

    private static Stream<Arguments> dataProvider() {
        Order aggressiveBuyOrder = buildBuyOrder(PRICE, QUANTITY, TIMESTAMP);
        Order aggressiveSellOrder = buildSellOrder(PRICE, QUANTITY, TIMESTAMP);
        return Stream.of(
                scenarioNoTrade(aggressiveBuyOrder, buildSellOrder(PRICE + 10, QUANTITY, TIMESTAMP)),
                scenario(aggressiveBuyOrder, buildSellOrder(PRICE - 10, QUANTITY, TIMESTAMP / 2), QUANTITY),
                scenario(aggressiveBuyOrder, buildSellOrder(PRICE - 10, QUANTITY / 2, TIMESTAMP / 2), QUANTITY / 2),
                scenario(aggressiveBuyOrder, buildSellOrder(PRICE - 10, QUANTITY * 2, TIMESTAMP / 2), QUANTITY),
                scenarioNoTrade(aggressiveSellOrder, buildBuyOrder(PRICE - 10, QUANTITY, TIMESTAMP)),
                scenario(aggressiveSellOrder, buildBuyOrder(PRICE + 10, QUANTITY, TIMESTAMP / 2), QUANTITY),
                scenario(aggressiveSellOrder, buildBuyOrder(PRICE + 10, QUANTITY / 2, TIMESTAMP / 2), QUANTITY / 2),
                scenario(aggressiveSellOrder, buildBuyOrder(PRICE + 10, QUANTITY * 2, TIMESTAMP / 2), QUANTITY)
        );
    }

    private static Arguments scenarioNoTrade(final Order aggressiveOrder, Order passiveOrder) {
        return scenario(aggressiveOrder, passiveOrder, new TradePrediction(0, 0, 0));
    }

    private static Arguments scenario(Order aggressiveOrder, Order passiveOrder, int tradeQuantity) {
        return scenario(aggressiveOrder, passiveOrder, new TradePrediction(passiveOrder.getPrice(), tradeQuantity, aggressiveOrder.getTimestamp()));
    }
    private static Arguments scenario(final Order aggressiveOrder, Order passiveOrder, TradePrediction tradePrediction) {
        return Arguments.of(aggressiveOrder, passiveOrder, tradePrediction);
    }

    private static Order buildBuyOrder(int price, int quantity, int timestamp) {
        return buildOrder(Side.BUY, price, quantity, timestamp);
    }

    private static Order buildSellOrder(int price, int quantity, int timestamp) {
        return buildOrder(Side.SELL, price, quantity, timestamp);
    }

    private static Order buildOrder(Side side, int price, int quantity, int timestamp) {
        return LimitOrder.newBuilderInstance()
                .withSide(side)
                .withPrice(price)
                .withQuantity(quantity)
                .withTimestamp(timestamp)
                .build();
    }
}
