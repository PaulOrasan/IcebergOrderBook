import java.util.ArrayList;
import java.util.List;

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
        // Order aggressiveOrder = order;
        // Order passiveOrder = getOppositeSideCandidateOrder(aggressiveOrder);
        // TradePrediction prediction = generator.generatePrediction(aggressiveOrder, passiveOrder);
        // List<TradeEvent> events = new ArrayList<>();
        // while (prediction.isTradeMatch()) {
        //     final TradeEvent event = new TradeEvent(aggressiveOrder.isBuyOrder() ? aggressiveOrder : passiveOrder,
        //             aggressiveOrder.isBuyOrder() ? passiveOrder : aggressiveOrder, prediction);
        //     events.add(event);
        //     orderBook.notifyTradeEvent(event);
        //     final Order buySideOrder = event.getBuyTradeResult().getPredictedOrder();
        //     final Order sellSideOrder = event.getSellTradeResult().getPredictedOrder();
        //     aggressiveOrder = buySideOrder.isAggressive() ? buySideOrder : sellSideOrder;
        //     passiveOrder = getOppositeSideCandidateOrder(aggressiveOrder);
        //     prediction = generator.generatePrediction(aggressiveOrder, passiveOrder);
        // }
        // if (aggressiveOrder.getAvailableQuantity() > 0) {
        //     orderBook.insertOrder(aggressiveOrder);
        // }
        // tradePublisher.publishTrades(collapseEvents(events));

        Order aggressiveOrder = order;
        final List<TradeEvent> events = new ArrayList<>();
        while (true) {
            Order passiveOrder = getOppositeSideCandidateOrder(aggressiveOrder);
            TradePrediction prediction = generator.generatePrediction(aggressiveOrder, passiveOrder);
            final TradeEvent event = buildEvent(aggressiveOrder, passiveOrder, prediction);
            aggressiveOrder = getOrderFromEvent(event, aggressiveOrder.getSide());
            if (prediction.isTradeMatch()) {
                orderBook.notifyTradeEvent(event);
                events.add(event);
            } else {
                break;
            }
        }
        if (aggressiveOrder.getAvailableQuantity() > 0) {
            orderBook.insertOrder(aggressiveOrder);
        }
        tradePublisher.publishTrades(collapseEvents(events));
    }

    private TradeEvent buildEvent(Order aggressiveOrder, Order passiveOrder, TradePrediction prediction) {
        return new TradeEvent(aggressiveOrder.isBuyOrder() ? aggressiveOrder : passiveOrder,
                aggressiveOrder.isBuyOrder() ? passiveOrder : aggressiveOrder, prediction
        );
    }

    private Order getOrderFromEvent(final TradeEvent event, final Side targetSide) {
        return Side.BUY.equals(targetSide) ? event.getBuyTradeResult().getPredictedOrder() : event.getSellTradeResult().getPredictedOrder();
    }

    private Order getPassiveOrderFromEvent(final TradeEvent event) {
        return !event.getBuyTradeResult().getPredictedOrder().isAggressive()
                ? event.getBuyTradeResult().getPredictedOrder()
                : event.getSellTradeResult().getPredictedOrder();
    }

    private List<TradeEvent> collapseEvents(List<TradeEvent> rawEvents) {
        final List<TradeEvent> collapsedEvents = new ArrayList<>();
        for (var event: rawEvents) {
            if (!collapsedEvents.stream().anyMatch(e -> e.canBeCollapsed(event))) {
                collapsedEvents.add(rawEvents.stream()
                                .filter(ev -> ev != event)
                        .reduce(event, TradeEvent::collapseEvents));
            }
        }
        return collapsedEvents;
    }

    private Order getOppositeSideCandidateOrder(final Order order) {
        if (Side.BUY.equals(order.getSide())) {
            return orderBook.getTopSellOrder();
        }
        return orderBook.getTopBuyOrder();
    }
}
