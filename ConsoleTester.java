package console;

import java.sql.Timestamp;

public class ConsoleTester {

    public static void main(String[] args) {
        ConsoleRunner.start();
        ConsoleRunner.mapToFunction("time", flagArgs -> printTime());
        ConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag + " ");
        });

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
     * Prints the current system time.
     */
    static void printTime() {
        System.out.println(new Timestamp(System.currentTimeMillis()).toString());
    }
}
