package console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Sets up a console environment in a non-blocking manner on a separate thread.
 */
public class ConsoleRunner {

    private static final String LOG_TAG = "ConsoleRunner";
    private static final long DEFAULT_SLEEP_INTERVAL = 1000;
    private static Thread mThread;
    private static boolean mKeepAlive;
    private static HashMap<String, Caller> mPatternMap;
    private static long mSleepInterval = DEFAULT_SLEEP_INTERVAL;

    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleRunner() {

    }

    /**
     * Resets the console back to its initial state.
     */
    public static void reset() {
        stop();

        mPatternMap.clear();
        mSleepInterval = DEFAULT_SLEEP_INTERVAL;
    }

    /**
     * Starts the console to accept input.
     */
    public static void start() {
        if (null == mThread) {
            mThread = new Thread(new ConsoleTask());
            mThread.setDaemon(true);
        }

        mKeepAlive = true;
        if (!mThread.isAlive()) mThread.start();
        System.out.println(LOG_TAG + ": Console initialized...");
    }

    /**
     * Stops the console.
     */
    public static void stop() {
        mKeepAlive = false;
    }

    /**
     * Sets the sleep interval of time between console line reads.
     *
     * @param interval The interval to set.
     */
    public static void setSleepInterval(long interval) {
        mSleepInterval = interval;
    }

    /**
     * Maps the function to the pattern provided. Flag provided should not include hyphen '-'.
     *
     * @param pattern  The pattern to match.
     * @param function The function to be called.
     */
    public static void mapToFunction(String pattern, Caller function) {
        if (null == mPatternMap) mPatternMap = new HashMap<>();

        mPatternMap.put(pattern, function);
    }

    /**
     * A simple interface representation of an method call.
     */
    public interface Caller {

        void perform(String[] flagArgs);
    }

    /**
     * A simple task that awaits console input.
     */
    private static class ConsoleTask implements Runnable {

        @Override
        public void run() {
            Scanner in = new Scanner(System.in);
            boolean isValid;

            while (mKeepAlive) {
                isValid = true;

                try {
                    Thread.sleep(mSleepInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String command = in.nextLine();

                String[] tokens = command.split(" ");

                String[] flags = Arrays.copyOfRange(tokens, 1, tokens.length);

                for (int i = 0; i < flags.length; ++i) {
                    if ('-' != flags[i].charAt(0)) {
                        isValid = false;
                        break;
                    } else {
                        flags[i] = flags[i].substring(1, flags[i].length());
                    }
                }

                if (isValid && mPatternMap != null && mPatternMap.containsKey(tokens[0]))
                    mPatternMap.get(tokens[0]).perform(flags);
                else System.out.println(LOG_TAG + ": Command not recognized. Please " +
                        "check usage and try again. Proper syntax is <Command> -<flag> (ex: print -hello)");
            }
        }
    }
}
