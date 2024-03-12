import java.util.Objects;

public class TradePrediction {

    private final int predictedPrice;
    private final int predictedQuantity;
    private final int timestamp;

    public TradePrediction(int price, int quantity, int timestamp) {
        this.predictedPrice = price;
        this.predictedQuantity = quantity;
        this.timestamp = timestamp;
    }

    public int getPredictedPrice() {
        return predictedPrice;
    }

    public int getPredictedQuantity() {
        return predictedQuantity;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public boolean isTradeMatch() {
        return predictedQuantity > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradePrediction that = (TradePrediction) o;
        return getPredictedPrice() == that.getPredictedPrice() && getPredictedQuantity() == that.getPredictedQuantity()
                && getTimestamp() == that.getTimestamp();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPredictedPrice(), getPredictedQuantity(), getTimestamp());
    }

    @Override
    public String toString() {
        return "TradePrediction{" + "predictedPrice=" + predictedPrice + ", predictedQuantity=" + predictedQuantity + ", timestamp=" + timestamp
                + '}';
    }
}
