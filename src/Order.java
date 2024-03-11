import java.util.Objects;

public abstract class Order {

    private final int id;
    private final Side side;
    private final int price;
    private final int quantity;
    private final int timestamp;
    private final boolean isAggressive;

    public Order(int id, Side side, int price, int quantity, int timestamp) {
        this(id, side, price, quantity, timestamp, true);
    }

    public Order(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive) {
        this.id = id;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.isAggressive = isAggressive;
    }

    public Order(final Builder builder) {
        this.id = builder.id;
        this.side = builder.side;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.timestamp = builder.timestamp;
        this.isAggressive = builder.isAggressive;
    }

    public int getId() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public boolean isAggressive() {
        return isAggressive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return getId() == order.getId() && getPrice() == order.getPrice() && getQuantity() == order.getQuantity()
                && getTimestamp() == order.getTimestamp() && isAggressive() == order.isAggressive() && getSide() == order.getSide()
                && getAvailableQuantity() == order.getAvailableQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSide(), getPrice(), getQuantity(), getTimestamp(), isAggressive());
    }

    public abstract int getAvailableQuantity();

    public abstract TradeResult generatePotentialResult(final TradePrediction prediction);

    public static abstract class Builder<T extends Builder<T>> {

        protected int id;
        protected Side side;
        protected int price;
        protected int quantity;
        protected int timestamp;
        protected boolean isAggressive;

        public Builder(int id, Side side, int price, int quantity, int timestamp, boolean isAggressive) {
            this.id = id;
            this.side = side;
            this.price = price;
            this.quantity = quantity;
            this.timestamp = timestamp;
            this.isAggressive = isAggressive;
        }

        public Builder() {}

        public T withId(int id) {
            this.id = id;
            return getThis();
        }

        public T withSide(Side side) {
            this.side = side;
            return getThis();
        }

        public T withPrice(int price) {
            this.price = price;
            return getThis();
        }

        public T withQuantity(int quantity) {
            this.quantity = quantity;
            return getThis();
        }

        public T withTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return getThis();
        }

        public T withAggressiveStatus(boolean aggressiveStatus) {
            this.isAggressive = aggressiveStatus;
            return getThis();
        }

        protected abstract T getThis();
    }
}
