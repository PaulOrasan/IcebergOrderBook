// Do not modify or delete this file. It is used to verify your solution before submission.
// (c) GSA Capital

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class VerificationTest {
  
    @ParameterizedTest
    @MethodSource("verificationTestsProvider")
    public void runTest(String testId, String input, String expectedOutput) throws IOException {

        String stdOut = getOrderBookOutput(input);
        System.out.println(stdOut);

        assertEquals(expectedOutput, stdOut);
    }

    private static String getOrderBookOutput(String input) throws IOException {
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;

        try (
            ByteArrayInputStream newIn = new ByteArrayInputStream(input.getBytes());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true)
        ){
            System.setIn(newIn);
            System.setOut(ps);

            SETSOrderBookExercise.main(new String[0]);

            return baos.toString(StandardCharsets.UTF_8.name()).trim().replaceAll("\\r\\n?", "\n");

        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }
    }

    static Stream<Arguments> verificationTestsProvider() {
        return Stream.of(
            Arguments.of("NormalOrder", 
                ""
                .concat("B,1,1,1\n")
                .concat("S,2,2,1"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1|      1|       |             |          |\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1|      1|      2|            1|         2|\n")
                .concat("+-----------------------------------------------------------------+")
            ),
            Arguments.of("OrderWithComment", 
                ""
                .concat("\n")
                .concat("# Comment\n")
                .concat(" # Another valid comment\n")
                .concat("B,1,1,1\n")
                .concat("S,2,2,1"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1|      1|       |             |          |\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1|      1|      2|            1|         2|\n")
                .concat("+-----------------------------------------------------------------+")
            ),
            Arguments.of("OrderIdFormat", 
                ""
                .concat("B,123456789,1,1\n")
                .concat("S,123456780,2,1"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("| 123456789|            1|      1|       |             |          |\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("| 123456789|            1|      1|      2|            1| 123456780|\n")
                .concat("+-----------------------------------------------------------------+")
            ),
            Arguments.of("OrderPriceFormat", 
                ""
                .concat("B,1,12345,1\n")
                .concat("S,2,12346,1"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1| 12,345|       |             |          |\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1| 12,345| 12,346|            1|         2|\n")
                .concat("+-----------------------------------------------------------------+")
            ),
            Arguments.of("OrderVolumeFormat", 
                ""
                .concat("S,1,2,1234567890\n")
                .concat("B,2,1,1234567890"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|          |             |       |      2|1,234,567,890|         1|\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         2|1,234,567,890|      1|      2|1,234,567,890|         1|\n")
                .concat("+-----------------------------------------------------------------+")
            ),
            Arguments.of("SingleTrade", 
                ""
                .concat("B,1,1,2\n")
                .concat("S,2,2,1\n")
                .concat("S,3,1,1"),
                ""
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            2|      1|       |             |          |\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            2|      1|      2|            1|         2|\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("1,3,1,1\n")
                .concat("+-----------------------------------------------------------------+\n")
                .concat("| BUY                            | SELL                           |\n")
                .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
                .concat("+----------+-------------+-------+-------+-------------+----------+\n")
                .concat("|         1|            1|      1|      2|            1|         2|\n")
                .concat("+-----------------------------------------------------------------+")
            )
            // Arguments.of("MultipleTrades",
            //         ""
            //                 .concat("B,123,90,100\n")
            //                 .concat("S,124,100,80\n")
            //                 .concat("S,125,95,200\n")
            //                 .concat("B,126,100,400,50\n")
            //                 .concat("S,127,80,700,70\n")
            //                 .concat("B,128,85,1000"),
            //         ""
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|       123|          100|     90|       |             |          |\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|       123|          100|     90|    100|           80|       124|\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|       123|          100|     90|     95|          200|       125|\n")
            //                 .concat("|          |             |       |    100|           80|       124|\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("126,125,95,200\n")
            //                 .concat("126,124,100,80\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|       123|          100|     90|       |             |          |\n")
            //                 .concat("|       126|           50|    100|       |             |          |\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("126,127,100,120\n")
            //                 .concat("123,127,90,100\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|          |             |       |     80|           70|       127|\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("128,127,80,480\n")
            //                 .concat("+-----------------------------------------------------------------+\n")
            //                 .concat("| BUY                            | SELL                           |\n")
            //                 .concat("| Id       | Volume      | Price | Price | Volume      | Id       |\n")
            //                 .concat("+----------+-------------+-------+-------+-------------+----------+\n")
            //                 .concat("|       128|          520|     85|       |             |          |\n")
            //                 .concat("+-----------------------------------------------------------------+")
            //
            // ),
            //     Arguments.of("NewTest",
            //     ""
            //             .concat("B,1,101,500,100\n")
            //             .concat("B,2,101,200,50\n")
            //             .concat("B,3,100,300,150\n")
            //     ,
            //     "")
        );
    }
}