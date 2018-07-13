package console;

import java.sql.Timestamp;
import java.util.Arrays;

public class ConsoleTest {

    static boolean mKeepRunning = true;
    static ConsoleRunner mMyConsoleRunner;

    public static void main(String[] args) {
        mMyConsoleRunner = new ConsoleRunner("My Console", System.in);

        mMyConsoleRunner.start();

        mMyConsoleRunner.mapToFunction("time", flagArgs -> printTime());

        mMyConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });

        mMyConsoleRunner.mapToFunction("exit", flagArgs -> {
            boolean yFlag = false;
            for (String flag : flagArgs) {
                if (flag.equals("y")) yFlag = true;
            }

            if (yFlag) exit();
            else System.out.println("You must enter a flag of 'y' to confirm (ex: \"exit -y\")");
        });

        mMyConsoleRunner.mapToFunction("printstrings", ConsoleTest::printStrings);

        mMyConsoleRunner.mapToFunction("stop", flagArgs -> stop());

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

    /**
     * Call stop on the ConsoleRunner.
     */
    static void stop() {
        mMyConsoleRunner.stop();
    }
}
