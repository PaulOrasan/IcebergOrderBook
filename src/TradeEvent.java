import java.util.Objects;

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

    public boolean canBeCollapsed(TradeEvent otherEvent) {
        return getBuyOrderId() == otherEvent.getBuyOrderId() && getSellOrderId() == otherEvent.getSellOrderId() && getTradedPrice() == otherEvent.getTradedPrice();
    }

    public TradeEvent collapseEvents(TradeEvent otherEvent) {
        if (canBeCollapsed(otherEvent)) {
            return new TradeEvent(getBuyOrderId(), getSellOrderId(), getTradedPrice(), getTradedQuantity() + otherEvent.getTradedQuantity(), null, null);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradeEvent that = (TradeEvent) o;
        return getBuyOrderId() == that.getBuyOrderId() && getSellOrderId() == that.getSellOrderId() && getTradedPrice() == that.getTradedPrice()
                && getTradedQuantity() == that.getTradedQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBuyOrderId(), getSellOrderId(), getTradedPrice(), getTradedQuantity());
    }

    @Override
    public String toString() {
        return "TradeEvent{" + "buyOrderId=" + buyOrderId + ", sellOrderId=" + sellOrderId + ", tradedPrice=" + tradedPrice + ", tradedQuantity="
                + tradedQuantity + '}';
    }
}
