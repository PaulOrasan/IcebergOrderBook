public class TradeEvent {

    private final int buyOrderId;
    private final int sellOrderId;
    private final int tradedPrice;
    private final int tradedQuantity;
    private final TradeResult buyTradeResult;
    private final TradeResult sellTradeResult;

    public TradeEvent(final Order buyOrder, final Order sellOrder, final TradePrediction tradePrediction) {
        this.buyOrderId = buyOrder.getId();
        this.sellOrderId = sellOrder.getId();
        this.tradedPrice = tradePrediction.getPredictedPrice();
        this.tradedQuantity = tradePrediction.getPredictedQuantity();
        this.buyTradeResult = buyOrder.generatePotentialResult(tradePrediction);
        this.sellTradeResult = sellOrder.generatePotentialResult(tradePrediction);
    }

    TradeEvent(int buyOrderId, int sellOrderId, int tradedPrice, int tradedQuantity, TradeResult buyTradeResult, TradeResult sellTradeResult) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.tradedPrice = tradedPrice;
        this.tradedQuantity = tradedQuantity;
        this.buyTradeResult = buyTradeResult;
        this.sellTradeResult = sellTradeResult;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public int getTradedPrice() {
        return tradedPrice;
    }

    public int getTradedQuantity() {
        return tradedQuantity;
    }

    public TradeResult getBuyTradeResult() {
        return buyTradeResult;
    }

    public TradeResult getSellTradeResult() {
        return sellTradeResult;
    }
}
