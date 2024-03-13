import java.io.BufferedWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class OutputAdapter {

    private static final String HEADER_BOTTOM_ROW_FORMAT = "|%-10s|%-13s|%-7s|%-7s|%-13s|%-10s|%n";
    private static final String FULL_ORDER_ROW_FORMAT = "|%10s|%13s|%7s|%7s|%13s|%10s|%n";
    private static final String HORIZONTAL_BORDER = "+-----------------------------------------------------------------+\n";
    private static final String HEADER_BOTTOM_BORDER = "+----------+-------------+-------+-------+-------------+----------+\n";
    private static final String HEADER_TOP_ROW_FORMAT = "|%-32s|%-32s|%n";
    private static final String DEFAULT_VALUE = "";
    private static final String TRADE_FORMAT = "%d,%d,%d,%d\n";
    private final BufferedWriter writer;

    public OutputAdapter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void writeTrade(final TradeEvent tradeEvent) {
        try {
            writer.write(convertTradeToString(tradeEvent));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Unexpected IO error");
        }
    }


    public void displayOrderBookHeader() {
        try {
            writer.write(HORIZONTAL_BORDER);
            writer.write(String.format(HEADER_TOP_ROW_FORMAT, " BUY", " SELL"));
            writer.write(String.format(HEADER_BOTTOM_ROW_FORMAT, " Id", " Volume", " Price", " Price", " Volume", " Id"));
            writer.write(HEADER_BOTTOM_BORDER);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Unexpected IO error");
        }
    }

    public void displayOrderPair(final Order buyOrder, final Order sellOrder) {
        try {
            writer.write(convertOrderPair(buyOrder, sellOrder));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Unexpected IO error");
        }
    }

    private String convertOrderPair(Order buyOrder, Order sellOrder) {
        return String.format(FULL_ORDER_ROW_FORMAT, convertId(buyOrder), convertVolume(buyOrder), convertPrice(buyOrder), convertPrice(sellOrder),
                convertVolume(sellOrder), convertId(sellOrder)
        );
    }

    public void displayFinalOrderBook() {
        try {
            writer.write(HORIZONTAL_BORDER);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertTradeToString(final TradeEvent event) {
        return String.format(TRADE_FORMAT, event.getBuyOrderId(), event.getSellOrderId(), event.getTradedPrice(), event.getTradedQuantity());
    }

    private String convertId(final Order order) {
        if (order == null) {
            return DEFAULT_VALUE;
        }
        return String.valueOf(order.getId());
    }

    private String convertVolume(final Order order) {
        if (order == null) {
            return DEFAULT_VALUE;
        }
        return NumberFormat.getInstance().format(order.getAvailableQuantity());
    }

    private String convertPrice(final Order order) {
        if (order == null) {
            return DEFAULT_VALUE;
        }
        return NumberFormat.getInstance().format(order.getPrice());
    }
}
