import java.io.BufferedWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class OutputAdapter {

    private static final int ID_WIDTH = 10;
    private static final int VOLUME_WIDTH = 13;
    private static final int PRICE_WIDTH = 7;
    private static final int TOTAL_WIDTH = ID_WIDTH + VOLUME_WIDTH + PRICE_WIDTH + 7; // Including formatting marks
    public static final String FULL_ROW_FORMAT = "|%-10s|%-13s|%-7s|%-7s|%-13s|%-10s|%n";
    public static final String FULL_ORDER_ROW_FORMAT = "|%10s|%13s|%7s|%7s|%13s|%10s|%n";
    private final BufferedWriter writer;

    public OutputAdapter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void writeTrade(final TradeEvent tradeEvent) {
        try {
            writer.write(convertTradeToString(tradeEvent));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void displayOrderBookHeader() {
        // Display headers
        try {
            writer.write("+-----------------------------------------------------------------+\n");
            writer.write(String.format("|%-32s|%-32s|%n", " BUY", " SELL"));
            writer.write(String.format(FULL_ROW_FORMAT, " Id", " Volume", " Price", " Price", " Volume", " Id"));
            writer.write("+----------+-------------+-------+-------+-------------+----------+\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayOrderPair(final Order buyOrder, final Order sellOrder) {
        try {
            final NumberFormat formatter = NumberFormat.getInstance();
            if (buyOrder != null && sellOrder != null) {
                writer.write(
                        String.format(FULL_ORDER_ROW_FORMAT, buyOrder.getId(), formatter.format(buyOrder.getAvailableQuantity()), formatter.format(buyOrder.getPrice()),
                                formatter.format(sellOrder.getPrice()), formatter.format(sellOrder.getAvailableQuantity()), sellOrder.getId()
                        ));
            }
            if (buyOrder == null && sellOrder != null) {
                writer.write(String.format(FULL_ORDER_ROW_FORMAT, "", "", "", formatter.format(sellOrder.getPrice()), formatter.format(sellOrder.getAvailableQuantity()), sellOrder.getId()));
            }
            if (buyOrder != null && sellOrder == null) {
                writer.write(String.format(FULL_ORDER_ROW_FORMAT, buyOrder.getId(), formatter.format(buyOrder.getAvailableQuantity()), formatter.format(buyOrder.getPrice()), "", "", ""));
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayFinalOrderBook() {
        try {
            writer.write("+-----------------------------------------------------------------+\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertTradeToString(final TradeEvent event) {
        return String.format("%d,%d,%d,%d\n", event.getBuyOrderId(), event.getSellOrderId(), event.getTradedPrice(), event.getTradedQuantity());
    }
}
