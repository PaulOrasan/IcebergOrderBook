import java.util.Objects;

public class IcebergOrder extends Order{

    private final int maxPeakSize;
    private final int currentPeakQuantity;

    public IcebergOrder(int id, Side side, int price, int quantity, int timestamp, int maxPeakSize) {
        this(id, side, price, quantity, timestamp, true, maxPeakSize, 0);
    }

    public IcebergOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive, int maxPeakSize) {
        this(id, side, price, quantity, timestamp, isAggressive, maxPeakSize, isAggressive ? 0 : maxPeakSize);
    }

    private IcebergOrder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive, int maxPeakSize, int currentPeakQuantity) {
        super(id, side, price, quantity, timestamp, isAggressive);
        this.maxPeakSize = maxPeakSize;
        this.currentPeakQuantity = currentPeakQuantity;
    }

    private IcebergOrder(final Builder builder) {
        super(builder);
        this.maxPeakSize= builder.maxPeakSize;
        this.currentPeakQuantity = builder.currentPeakQuantity;
    }

    public int getMaxPeakSize() {
        return maxPeakSize;
    }

    public int getCurrentPeakQuantity() {
        return currentPeakQuantity;
    }

    @Override
    public int getAvailableQuantity() {
        return getCurrentPeakQuantity();
    }

    @Override
    public TradeResult generatePotentialResult(TradePrediction prediction) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        IcebergOrder that = (IcebergOrder) o;
        return getMaxPeakSize() == that.getMaxPeakSize() && getCurrentPeakQuantity() == that.getCurrentPeakQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMaxPeakSize(), getCurrentPeakQuantity());
    }

    public static Builder newBuilderInstance() {
        return new Builder();
    }

    public static Builder builderFromOrder(final IcebergOrder order) {
        return new Builder(order);
    }

    public static class Builder extends Order.Builder<Builder> {

        private int maxPeakSize;
        private int currentPeakQuantity;

        public Builder(final IcebergOrder order) {
            super(order.getId(), order.getSide(), order.getPrice(), order.getQuantity(), order.getTimestamp(), order.isAggressive());
            this.maxPeakSize = order.getMaxPeakSize();
            this.currentPeakQuantity = order.getCurrentPeakQuantity();
        }

        public Builder() {}

        public Builder withMaxPeakSize(int maxPeakSize) {
            this.maxPeakSize = maxPeakSize;
            this.currentPeakQuantity = maxPeakSize;
            return getThis();
        }

        public Builder withCurrentPeakQuantity(int currentPeakQuantity) {
            this.currentPeakQuantity = currentPeakQuantity;
            return getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public IcebergOrder build() {
            return new IcebergOrder(getThis());
        }
    }
}
