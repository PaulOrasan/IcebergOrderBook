import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LimitOrderTest {

    private static final int ID = 12375;
    private static final int PRICE = 89;
    private static final int ORIGINAL_QUANTITY = 1000;
    private static final int ORIGINAL_TIMESTAMP = 10000;

    private static final LimitOrder PASSIVE_ORDER = LimitOrder.newBuilderInstance()
            .withId(ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(false)
            .build();

    private static final LimitOrder AGGRESSIVE_ORDER = LimitOrder.newBuilderInstance()
            .withId(ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(true)
            .build();

    @ParameterizedTest
    @MethodSource("dataProvider")
    void generatePotentialResult(final LimitOrder inputOrder, final TradePrediction inputPrediction, final LimitOrder expectedResult) {
        final TradeResult actualResult = inputOrder.generatePotentialResult(inputPrediction);

        assertEquals(expectedResult, actualResult.getPredictedOrder());
    }

    private static Stream<Arguments> dataProvider() {
        final TradePrediction partialTrade = new TradePrediction(PRICE, ORIGINAL_QUANTITY / 2, ORIGINAL_TIMESTAMP + 10);
        final TradePrediction fullTrade = new TradePrediction(PRICE, ORIGINAL_QUANTITY, ORIGINAL_TIMESTAMP + 25);


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

    private static Consumer<LimitOrder.Builder> timestampChangesTo(final int targetTimestamp) {
        return (builder -> builder.withTimestamp(targetTimestamp));
    }

    private static Consumer<LimitOrder.Builder> nothingChanges() {
        return (builder -> {});
    }
}