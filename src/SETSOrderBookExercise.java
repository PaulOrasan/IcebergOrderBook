import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SETSOrderBookExercise {

    public static void main(String[] args) {

        // This is the entrypoint to your solution - add your code here. args will be empty.
        // Do not remove this file as it's required by our test harness to validate your code before submission.

        final InputAdapter adapter = new InputAdapter(new BufferedReader(new InputStreamReader(System.in)));
        final OutputAdapter outputAdapter = new OutputAdapter(new BufferedWriter(new OutputStreamWriter(System.out)));

        final OrderBook orderBook = new OrderBook();
        final TradeGenerator generator = new TradeGenerator();
        final DataPublisher publisher = new DataPublisher(outputAdapter);
        final ExecutionEngine engine = new ExecutionEngine(orderBook, generator, publisher);

        while (true) {
            final Order order = adapter.readNextOrder();
            if (order == null) {
                break;
            }
            engine.addOrder(order);
        }
    }
}