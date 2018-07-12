package console;

import java.sql.Timestamp;
import java.util.Arrays;

public class ConsoleTest {

    public static void main(String[] args) {
        ConsoleRunner console = ConsoleRunner.getInstance();

        console.start();
        console.mapToFunction("time", flagArgs -> printTime());
        console.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });
        console.mapToFunction("printstrings", ConsoleTest::printStrings);

        keepAlive();
    }

    /**
     * Keep the application alive for testing purposes.
     */
    static void keepAlive() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A method that takes a String array as an argument and prints its contents.
     */
    static void printStrings(String[] strings) {
        System.out.println(Arrays.toString(strings));
    }

    /**
     * Prints the current system time.
     */
    static void printTime() {
        System.out.println(new Timestamp(System.currentTimeMillis()).toString());
    }
}
