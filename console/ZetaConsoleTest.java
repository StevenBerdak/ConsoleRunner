package console;

import java.sql.Timestamp;
import java.util.Arrays;

public class ZetaConsoleTest {

    static boolean mKeepRunning = true;
    static ZetaConsole zetaConsole;

    public static void main(String[] args) {
        zetaConsole = new ZetaConsole("My Console", System.in);

        zetaConsole.start();

        zetaConsole.mapToFunction("time", flagArgs -> printTime());

        zetaConsole.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });

        zetaConsole.mapToFunction("exit", flagArgs -> {
            boolean yFlag = false;
            for (String flag : flagArgs) {
                if (flag.equals("y")) yFlag = true;
            }

            if (yFlag) exit();
            else System.out.println("You must enter a flag of 'y' to confirm (ex: \"exit -y\")");
        });

        zetaConsole.mapToFunction("printstrings", ConsoleTest::printStrings);

        zetaConsole.mapToFunction("stop", flagArgs -> stop());

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
        zetaConsole.stop();
    }
}
