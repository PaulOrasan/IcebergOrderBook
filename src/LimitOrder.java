public class LimitOrder extends Order{

    public LimitOrder(int id, Side side, int price, int quantity, int timestamp) {
        this(id, side, price, quantity, timestamp, true);
    }

    public LimitOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive) {
        super(id, side, price, quantity, timestamp, isAggressive);
    }

    private LimitOrder(final Builder builder) {
        super(builder);
    }

    @Override
    public int getAvailableQuantity() {
        return getQuantity();
    }

    @Override
    public TradeResult generatePotentialResult(TradePrediction prediction) {
        final boolean predictedAggressiveStatus = isAggressive() && prediction.isTradeMatch();
        final int predictedTimestamp = getAvailableQuantity() == prediction.getPredictedQuantity() ? prediction.getTimestamp() : getTimestamp();
        final int predictedQuantity = getQuantity() - prediction.getPredictedQuantity();

        final LimitOrder predictedOrder =  builderFromOrder(this)
                .withAggressiveStatus(predictedAggressiveStatus)
                .withQuantity(predictedQuantity)
                .withTimestamp(predictedTimestamp)
                .build();
        return new TradeResult(predictedOrder);
    }

    public static Builder newBuilderInstance() {
        return new Builder();
    }

    public static Builder builderFromOrder(final LimitOrder order) {
        return new Builder(order);
    }

    public static class Builder extends Order.Builder<Builder> {

        Builder(final LimitOrder order) {
            super(order.getId(), order.getSide(), order.getPrice(), order.getQuantity(), order.getTimestamp(), order.isAggressive());
        }

        Builder() {super();}


        @Override
        protected Builder getThis() {
            return this;
        }

        public LimitOrder build() {
            return new LimitOrder(getThis());
        }
    }
}
