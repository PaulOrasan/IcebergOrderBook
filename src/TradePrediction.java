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
}
