public class ExecutionEngine {

    private final OrderBook orderBook;

    public ExecutionEngine(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public void addOrder(final Order order) {
        final Order oppositeSideOrder = getOppositeSideCandidateOrder(order);
    }

    private TradePrediction generatePrediction(final Order buyOrder, final Order sellOrder) {
        return null;
    }

    private TradeEvent applyPrediction(final Order buyOrder, final Order sellOrder, final TradePrediction prediction) {
        return new TradeEvent(buyOrder, sellOrder, prediction);
    }

    private Order getOppositeSideCandidateOrder(final Order order) {
        if (Side.BUY.equals(order.getSide())) {
            return orderBook.getTopSellOrder();
        }
        return orderBook.getTopBuyOrder();
    }
}
