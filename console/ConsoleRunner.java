package console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Sets up a console environment in a non-blocking manner on a separate thread.
 */
public class ConsoleRunner {

    private static final String LOG_TAG = "ConsoleRunner";
    private static final long DEFAULT_SLEEP_INTERVAL = 1000;
    private static ConsoleRunner mInstance;
    private Thread mThread;
    private boolean mKeepAlive;
    private HashMap<String, Consumer<String[]>> mPatternMap;
    private long mSleepInterval = DEFAULT_SLEEP_INTERVAL;

    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleRunner() {

    }

    /**
     * Provides an instance of ConsoleRunner or creates a new one if necessary.
     *
     * @return An instance of ConsoleRunner.
     */
    public static ConsoleRunner getInstance() {
        if (null == mInstance) {
            mInstance = new ConsoleRunner();
        }

        return mInstance;
    }

    /**
     * Resets the console back to its initial state.
     */
    public void reset() {
        stop();
        mPatternMap.clear();
        mSleepInterval = DEFAULT_SLEEP_INTERVAL;
    }

    /**
     * Starts the console to accept input.
     */
    public void start() {
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
    public void stop() {
        mKeepAlive = false;
    }

    /**
     * Sets the sleep interval of time between console line reads.
     *
     * @param interval The interval to set.
     */
    public void setSleepInterval(long interval) {
        mSleepInterval = interval;
    }

    /**
     * Maps the function to the pattern provided. Flag provided should not include hyphen '-'.
     *
     * @param pattern  The pattern to match.
     * @param function The function to be called.
     */
    public void mapToFunction(String pattern, Consumer<String[]> function) {
        if (null == mPatternMap) mPatternMap = new HashMap<>();

        mPatternMap.put(pattern, function);
    }

    /**
     * A simple task that awaits console input.
     */
    private class ConsoleTask implements Runnable {

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
                    mPatternMap.get(tokens[0]).accept(flags);
                else System.out.println(LOG_TAG + ": Command not recognized. Please " +
                        "check usage and try again. Proper syntax is <Command> -<flag> (ex: print -hello)");
            }
        }
    }
}
