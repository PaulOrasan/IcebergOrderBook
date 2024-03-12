public class ExecutionEngine {

    private final OrderBook orderBook;
    private final TradeGenerator generator;
    private final TradePublisher tradePublisher;

    public ExecutionEngine(OrderBook orderBook, TradeGenerator generator, TradePublisher tradePublisher) {
        this.orderBook = orderBook;
        this.generator = generator;
        this.tradePublisher = tradePublisher;
    }

    public void addOrder(final Order order) {
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
