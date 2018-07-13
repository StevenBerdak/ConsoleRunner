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
    private static Thread mThread;
    private static boolean mKeepAlive;
    private static HashMap<String, Consumer<String[]>> mCommandMap;
    private static long mSleepInterval = DEFAULT_SLEEP_INTERVAL;

    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleRunner() {

    }

    /**
     * Free up all attached resources.
     */
    public void destroy() {
        stop();
        mThread = null;
        mCommandMap = null;
    }

    /**
     * Resets the console back to its initial state.
     */
    public static void reset() {
        stop();
        mCommandMap.clear();
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
     * Map the function to the command provided. Flags matching should not include hyphen '-'.
     *
     * @param command  The command to match.
     * @param function The function to be called.
     */
    public static void mapToFunction(String command, Consumer<String[]> function) {
        if (null == mCommandMap) mCommandMap = new HashMap<>();

        mCommandMap.put(command, function);
    }

    /**
     * Remove the function from the command map.
     *
     * @param command The command to be removed.
     */
    public static void removeMapToFunction(String command) {
        mCommandMap.remove(command);
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

                try {
                    if (isValid && mCommandMap != null && mCommandMap.containsKey(tokens[0]))
                        mCommandMap.get(tokens[0]).accept(flags);
                    else System.out.println(LOG_TAG + ": Command not recognized. Please " +
                            "check usage and try again. Proper syntax is <Command> -<flag> (ex: print -hello)");
                } catch (NullPointerException e) {
                    System.out.println("There was a problem with the specified command");
                }
            }
        }
    }
}
