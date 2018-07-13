package console;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up a console environment in a way which does not block the main thread.
 */
public class ConsoleRunner {

    private InputStream mInputStream;
    private Scanner mScanner;
    private Thread mThread;
    private String mCommand;
    private String[] mTokens, mFlags;
    private HashMap<String, Consumer<String[]>> mCommandMap;
    private Logger mLogger;
    private boolean mIsValid;
    private boolean mKeepAlive;

    /**
     * Constructor.
     *
     * @param inputStream The input stream to use for reading commands.
     */
    public ConsoleRunner(String consoleName, InputStream inputStream) {
        mLogger = Logger.getLogger(consoleName);
        this.mInputStream = inputStream;
    }

    /**
     * Resets the console back to its initial state.
     */
    public void reset() {
        stop();
        mThread = null;
        mCommand = null;
        mTokens = null;
        mFlags = null;
        mCommandMap.clear();
        mIsValid = false;
    }

    /**
     * Prepares to accept console input.
     */
    public void start() {
        mScanner = new Scanner(mInputStream);

        if (null == mThread) {
            mThread = new Thread(new ConsoleTask());
            mThread.setDaemon(true);
        }

        mKeepAlive = true;

        if (!mThread.isAlive()) mThread.start();
        mLogger.log(Level.INFO, "Console initialized...");
    }

    /**
     * Stops the console.
     */
    public void stop() {

        mKeepAlive = false;

        mThread.interrupt();
        mLogger.log(Level.INFO, "Console stopped");
    }

    /**
     * Map the function to the command provided. Flags matching should not include hyphen '-'.
     *
     * @param command  The command to match.
     * @param function The function to be called.
     */
    public void mapToFunction(String command, Consumer<String[]> function) {
        if (null == mCommandMap) mCommandMap = new HashMap<>();

        mCommandMap.put(command, function);
    }

    /**
     * Remove the function from the command map.
     *
     * @param command The command to be removed.
     */
    public void removeMapToFunction(String command) {
        mCommandMap.remove(command);
    }

    /**
     * A simple task that awaits console input.
     */
    private class ConsoleTask implements Runnable {

        @Override
        public void run() {
            while (mKeepAlive) {
                mIsValid = true;

                mCommand = mScanner.nextLine();

                mTokens = mCommand.split(" ");

                mFlags = Arrays.copyOfRange(mTokens, 1, mTokens.length);

                for (int i = 0; i < mFlags.length; ++i) {
                    if ('-' != mFlags[i].charAt(0)) {
                        mIsValid = false;
                        break;
                    } else {
                        mFlags[i] = mFlags[i].substring(1, mFlags[i].length());
                    }
                }

                try {
                    if (mIsValid && mCommandMap != null && mCommandMap.containsKey(mTokens[0]))
                        mCommandMap.get(mTokens[0]).accept(mFlags);
                    else if (!mCommand.equals("")) mLogger.log(Level.WARNING, "Command not recognized. Please " +
                            "check usage and try again. Proper syntax is <Command> -<flag> (ex: print -hello)");
                } catch (NullPointerException e) {
                    mLogger.log(Level.WARNING, "There was a problem with the specified command");
                }
            }
        }
    }
}
