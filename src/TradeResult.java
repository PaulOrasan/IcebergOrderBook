public class TradeResult {

    private final Order predictedOrder;

    public TradeResult(Order predictedOrder) {
        this.predictedOrder = predictedOrder;
    }

    public Order getPredictedOrder() {
        return predictedOrder;
    }
}
