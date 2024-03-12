import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LimitOrderTest {

    public static final int ORDER_ID = 12375;
    public static final int PRICE = 89;
    public static final int ORIGINAL_QUANTITY = 1000;
    public static final int ORIGINAL_TIMESTAMP = 10000;

    private static final LimitOrder PASSIVE_ORDER = LimitOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(false)
            .build();

    private static final LimitOrder AGGRESSIVE_ORDER = LimitOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(true)
            .build();

    @ParameterizedTest
    @MethodSource("predictionDataProvider")
    void generatePotentialResult(final LimitOrder inputOrder, final TradePrediction inputPrediction, final LimitOrder expectedResult) {
        final TradeResult actualResult = inputOrder.generatePotentialResult(inputPrediction);
        assertEquals(expectedResult, actualResult.getPredictedOrder());
    }

    @ParameterizedTest
    @MethodSource("quantityDataProvider")
    void getAvailableQuantity(final LimitOrder order, final int expectedResult) {
        assertEquals(expectedResult, order.getAvailableQuantity());
    }

    private static Stream<Arguments> quantityDataProvider() {
        return Stream.of(
                Arguments.of(PASSIVE_ORDER, ORIGINAL_QUANTITY),
                Arguments.of(AGGRESSIVE_ORDER, ORIGINAL_QUANTITY)
        );
    }

    private static Stream<Arguments> predictionDataProvider() {
        final TradePrediction partialTrade = new TradePrediction(PRICE, ORIGINAL_QUANTITY / 2);
        final TradePrediction fullTrade = new TradePrediction(PRICE, ORIGINAL_QUANTITY);


        return Stream.of(
                scenario(PASSIVE_ORDER, partialTrade, quantityChangesTo(ORIGINAL_QUANTITY / 2)),
                scenario(PASSIVE_ORDER, fullTrade, quantityChangesTo(0), timestampChangesTo(fullTrade.getTimestamp())),
                scenario(AGGRESSIVE_ORDER, partialTrade, quantityChangesTo(ORIGINAL_QUANTITY / 2)),
                scenario(AGGRESSIVE_ORDER, fullTrade, quantityChangesTo(0), timestampChangesTo(fullTrade.getTimestamp()))
        );
    }

    @SafeVarargs
    private static Arguments scenario(LimitOrder inputOrder, TradePrediction tradePrediction, Consumer<LimitOrder.Builder>... expectedChanges) {
        final LimitOrder.Builder expectedPredictionBuilder = LimitOrder.builderFromOrder(inputOrder);
        for (var change: expectedChanges) {
            change.accept(expectedPredictionBuilder);
        }
        return Arguments.of(inputOrder, tradePrediction, expectedPredictionBuilder.build());
    }

    private static Consumer<LimitOrder.Builder> quantityChangesTo(final int targetQuantity) {
        return (builder -> builder.withQuantity(targetQuantity));
    }

    private static Consumer<LimitOrder.Builder> timestampChangesTo(final long targetTimestamp) {
        return (builder -> builder.withTimestamp(targetTimestamp));
    }

    private static Consumer<LimitOrder.Builder> nothingChanges() {
        return (builder -> {});
    }
}
