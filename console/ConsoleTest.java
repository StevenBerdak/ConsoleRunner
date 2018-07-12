package console;

import java.sql.Timestamp;
import java.util.Arrays;

public class ConsoleTest {

    public static boolean mKeepRunning = true;

    public static void main(String[] args) {
        ConsoleRunner.start();
        ConsoleRunner.mapToFunction("time", flagArgs -> printTime());
        ConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });
        ConsoleRunner.mapToFunction("exit", flagArgs -> {
            for (String flag : flagArgs) {
                if (flag.equals("y")) exit();
            }
        });
        ConsoleRunner.mapToFunction("printstrings", ConsoleTest::printStrings);

        keepAlive();
    }

    /**
     * Keep the application alive for testing purposes.
     */
    static void keepAlive() {
        while (mKeepRunning) {
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

    /**
     * Exit the application.
     */
    static void exit() {
        mKeepRunning = false;
    }
}
