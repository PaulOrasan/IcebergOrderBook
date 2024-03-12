import static java.lang.Math.min;

public class TradeGenerator {


    private static final TradePrediction NO_TRADE = new TradePrediction(0, 0, 0);

    private TradeGenerator() {}

    public static TradePrediction generatePrediction(final Order aggressiveOrder, final Order passiveOrder) {
        final Order buySideOrder = aggressiveOrder.isBuyOrder() ? aggressiveOrder : passiveOrder;
        final Order sellSideOrder = aggressiveOrder.isBuyOrder() ? passiveOrder : aggressiveOrder;
        if (buySideOrder.getPrice() < sellSideOrder.getPrice()) {
            return NO_TRADE;
        }
        final int tradedQuantity = min(aggressiveOrder.getAvailableQuantity(), passiveOrder.getAvailableQuantity());
        return new TradePrediction(passiveOrder.getPrice(), tradedQuantity, aggressiveOrder.getTimestamp());
    }
}
