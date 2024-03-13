import java.util.ArrayList;
import java.util.List;

public class ExecutionEngine {

    private final OrderBook orderBook;
    private final TradeGenerator generator;
    private final DataPublisher dataPublisher;

    public ExecutionEngine(OrderBook orderBook, TradeGenerator generator, DataPublisher dataPublisher) {
        this.orderBook = orderBook;
        this.generator = generator;
        this.dataPublisher = dataPublisher;
    }

    public void addOrder(final Order order) {
        final List<TradeEvent> events = handleTrade(order);
        dataPublisher.publishTrades(collapseEvents(events));
        dataPublisher.publishOrderBook(orderBook.getBuyOrders(), orderBook.getSellOrders());
    }

    private List<TradeEvent> handleTrade(final Order aggressiveOrder) {
        Order order = aggressiveOrder;
        final List<TradeEvent> events = new ArrayList<>();
        do {
            TradeEvent event = attemptTradeMatch(order);
            events.add(event);
            order = getOrderFromEvent(event, order.getSide());
        } while (order.isAggressive());
        orderBook.insertOrder(order);
        return events;
    }

    private TradeEvent attemptTradeMatch(Order aggressiveOrder) {
        Order passiveOrder = getOppositeSidePassiveOrder(aggressiveOrder);
        TradePrediction prediction = generator.generatePrediction(aggressiveOrder, passiveOrder);
        final TradeEvent event = buildEvent(aggressiveOrder, passiveOrder, prediction);
        if (prediction.isTradeMatch()) {
            orderBook.notifyTradeEvent(event);
        }
        return event;
    }

    private TradeEvent buildEvent(Order aggressiveOrder, Order passiveOrder, TradePrediction prediction) {
        return new TradeEvent(aggressiveOrder.isBuyOrder() ? aggressiveOrder : passiveOrder,
                aggressiveOrder.isBuyOrder() ? passiveOrder : aggressiveOrder, prediction
        );
    }

    private Order getOrderFromEvent(final TradeEvent event, final Side targetSide) {
        return Side.BUY.equals(targetSide) ? event.getBuyTradeResult().getPredictedOrder() : event.getSellTradeResult().getPredictedOrder();
    }

    private List<TradeEvent> collapseEvents(final List<TradeEvent> rawEvents) {
        final List<TradeEvent> collapsedEvents = new ArrayList<>();
        for (var event: rawEvents) {
            if (event.getTradedQuantity() == 0) {
                continue;
            }
            if (!isEventCollapsible(event, collapsedEvents)) {
                collapsedEvents.add(collapseEvent(event, rawEvents));
            }
        }
        return collapsedEvents;
    }

    private boolean isEventCollapsible(final TradeEvent event, final  List<TradeEvent> collapsedEvents) {
        return collapsedEvents.stream().anyMatch(e -> e.canBeCollapsed(event));
    }

    private TradeEvent collapseEvent(final TradeEvent targetEvent, final List<TradeEvent> rawEvents) {
        return rawEvents.stream()
                .filter(event -> targetEvent != event)
                .reduce(targetEvent, TradeEvent::collapseEvents);
    }

    private Order getOppositeSidePassiveOrder(final Order order) {
        if (Side.BUY.equals(order.getSide())) {
            return orderBook.getTopSellOrder();
        }
        return orderBook.getTopBuyOrder();
    }
}
