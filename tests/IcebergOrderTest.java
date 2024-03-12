import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class IcebergOrderTest {

    public static final int ORDER_ID = 12375;
    public static final int PRICE = 89;
    public static final int ORIGINAL_QUANTITY = 1000;
    public static final int ORIGINAL_TIMESTAMP = 10000;
    public static final int MAX_PEAK_SIZE = 100;

    private static final IcebergOrder PASSIVE_ORDER = IcebergOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(false)
            .withMaxPeakSize(MAX_PEAK_SIZE)
            .build();

    private static final IcebergOrder UNEVEN_PEAK_ORDER = IcebergOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.SELL)
            .withPrice(PRICE)
            .withQuantity(MAX_PEAK_SIZE / 2)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(false)
            .withMaxPeakSize(MAX_PEAK_SIZE)
            .build();

    private static final IcebergOrder LAST_PEAK_ORDER = IcebergOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.SELL)
            .withPrice(PRICE)
            .withQuantity(0)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(false)
            .withMaxPeakSize(MAX_PEAK_SIZE)
            .withCurrentPeakQuantity(MAX_PEAK_SIZE / 2)
            .build();

    private static final IcebergOrder AGGRESSIVE_ORDER = IcebergOrder.newBuilderInstance()
            .withId(ORDER_ID)
            .withSide(Side.BUY)
            .withPrice(PRICE)
            .withQuantity(ORIGINAL_QUANTITY)
            .withTimestamp(ORIGINAL_TIMESTAMP)
            .withAggressiveStatus(true)
            .withMaxPeakSize(MAX_PEAK_SIZE)
            .build();

    @ParameterizedTest
    @MethodSource("predictionDataProvider")
    void generatePotentialResult(final IcebergOrder inputOrder, final TradePrediction inputPrediction, final IcebergOrder expectedResult) {
        final TradeResult actualResult = inputOrder.generatePotentialResult(inputPrediction);

        assertEquals(expectedResult, actualResult.getPredictedOrder());
    }

    @ParameterizedTest
    @MethodSource("quantityDataProvider")
    void getAvailableQuantity(final IcebergOrder order, final int expectedResult) {
        assertEquals(expectedResult, order.getAvailableQuantity());
    }

    private static Stream<Arguments> quantityDataProvider() {
        return Stream.of(
                Arguments.of(PASSIVE_ORDER, MAX_PEAK_SIZE),
                Arguments.of(UNEVEN_PEAK_ORDER, MAX_PEAK_SIZE / 2),
                Arguments.of(LAST_PEAK_ORDER, MAX_PEAK_SIZE / 2),
                Arguments.of(AGGRESSIVE_ORDER, ORIGINAL_QUANTITY)
        );
    }

    private static Stream<Arguments> predictionDataProvider() {
        final TradePrediction partialPeakTrade = new TradePrediction(PRICE, MAX_PEAK_SIZE / 2, ORIGINAL_TIMESTAMP + 10);
        final TradePrediction fullPeakTrade = new TradePrediction(PRICE, MAX_PEAK_SIZE, ORIGINAL_TIMESTAMP + 25);
        final TradePrediction fullOrderTrade = new TradePrediction(PRICE, LAST_PEAK_ORDER.getMaxPeakSize(), ORIGINAL_TIMESTAMP + 15);
        final TradePrediction aggressiveTrade = new TradePrediction(PRICE, 3 * MAX_PEAK_SIZE, ORIGINAL_TIMESTAMP + 12);
        final TradePrediction noTrade = new TradePrediction(PRICE, 0, ORIGINAL_TIMESTAMP + 5);


        return Stream.of(
                scenario(PASSIVE_ORDER, partialPeakTrade, peakQuantityChangesTo(MAX_PEAK_SIZE / 2)),
                scenario(PASSIVE_ORDER, fullPeakTrade, quantityChangesTo(ORIGINAL_QUANTITY - MAX_PEAK_SIZE), peakQuantityChangesTo(MAX_PEAK_SIZE),
                        timestampChangesTo(fullPeakTrade.getTimestamp())),
                scenario(PASSIVE_ORDER, noTrade, nothingChanges()),
                scenario(UNEVEN_PEAK_ORDER, fullPeakTrade, quantityChangesTo(0), peakQuantityChangesTo(UNEVEN_PEAK_ORDER.getQuantity()),
                        timestampChangesTo(fullPeakTrade.getTimestamp())),
                scenario(LAST_PEAK_ORDER, fullOrderTrade, peakQuantityChangesTo(0), timestampChangesTo(fullOrderTrade.getTimestamp())),
                scenario(AGGRESSIVE_ORDER, aggressiveTrade, quantityChangesTo(ORIGINAL_QUANTITY - 3 * MAX_PEAK_SIZE)),
                scenario(AGGRESSIVE_ORDER, noTrade, becomesPassive(), peakQuantityChangesTo(MAX_PEAK_SIZE))
        );
    }

    @SafeVarargs
    private static Arguments scenario(IcebergOrder inputOrder, TradePrediction tradePrediction, Consumer<IcebergOrder.Builder>... expectedChanges) {
        final IcebergOrder.Builder expectedPredictionBuilder = IcebergOrder.builderFromOrder(inputOrder);
        for (var change: expectedChanges) {
            change.accept(expectedPredictionBuilder);
        }
        return Arguments.of(inputOrder, tradePrediction, expectedPredictionBuilder.build());
    }

    private static Consumer<IcebergOrder.Builder> quantityChangesTo(final int targetQuantity) {
        return (builder -> builder.withQuantity(targetQuantity));
    }

    private static Consumer<IcebergOrder.Builder> timestampChangesTo(final int targetTimestamp) {
        return (builder -> builder.withTimestamp(targetTimestamp));
    }

    private static Consumer<IcebergOrder.Builder> peakQuantityChangesTo(final int targetQuantity) {
        return (builder -> builder.withCurrentPeakQuantity(targetQuantity));
    }

    private static Consumer<IcebergOrder.Builder> becomesPassive() {
        return (builder -> builder.withAggressiveStatus(false));
    }

    private static Consumer<IcebergOrder.Builder> nothingChanges() {
        return (builder -> {});
    }
}
