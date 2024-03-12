import java.util.Objects;

public class TradePrediction {

    private final int predictedPrice;
    private final int predictedQuantity;
    private final long timestamp;

    public TradePrediction(int price, int quantity) {
        this.predictedPrice = price;
        this.predictedQuantity = quantity;
        this.timestamp = TimeUtils.getCurrentTimestamp();
    }

    public int getPredictedPrice() {
        return predictedPrice;
    }

    public int getPredictedQuantity() {
        return predictedQuantity;
    }

    public long getTimestamp() {
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
        return getPredictedPrice() == that.getPredictedPrice() && getPredictedQuantity() == that.getPredictedQuantity();
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
