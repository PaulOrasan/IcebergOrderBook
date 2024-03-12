import java.io.BufferedReader;
import java.io.IOException;

public class InputAdapter {


    private final BufferedReader reader;

    public InputAdapter(final BufferedReader reader) {
        this.reader = reader;
    }

    public Order readNextOrder() {
        try {
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    return null;
                }
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }
                break;
            }
            return convertDataLineToOrder(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Order convertDataLineToOrder(final String line) {
        final String[] tokens = line.split(",");
        final Side side = tokens[0].equals("B") ? Side.BUY : Side.SELL;
        final int id = Integer.parseInt(tokens[1]);
        final int price = Integer.parseInt(tokens[2]);
        final int quantity = Integer.parseInt(tokens[3]);
        final long timestamp = TimeUtils.getCurrentTimestamp();
        if (tokens.length > 4) {
            final int peakSize = Integer.parseInt(tokens[4]);
            return new IcebergOrder(id, side, price, quantity, timestamp, peakSize);
        }
        return new LimitOrder(id, side, price, quantity, timestamp);
    }
}
