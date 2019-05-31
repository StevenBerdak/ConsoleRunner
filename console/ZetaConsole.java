package console;

import java.io.File;
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
public class ZetaConsole {

    private InputStream mInputStream;
    private Scanner mScanner;
    private Thread mInputThread, mKeepAliveThread;
    private String mCommand;
    private String[] mTokens, mFlags;
    private HashMap<String, Consumer<String[]>> mCommandMap;
    private Logger mLogger;
    private boolean mIsValid;
    private boolean mAwaitInput;
    private boolean mKeepAlive;
    private File mWorkingDir;

    /**
     * Constructor.
     *
     * @param inputStream The input stream to use for reading commands.
     */
    public ZetaConsole(String consoleName, InputStream inputStream) {
        mLogger = Logger.getLogger(consoleName);
        this.mInputStream = inputStream;
    }

    /**
     * Get the logger attached to this instance.
     * @return A logger for logging.
     */
    public Logger getLogger() {
        return mLogger;
    }

    /**
     * Keeps the application process alive.
     */
    public void keepAlive() {
        mKeepAlive = true;
        mKeepAliveThread = new Thread(() -> {
            while (mKeepAlive) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                     e.printStackTrace();
                }
            }
        });
        mKeepAliveThread.isDaemon();
        mKeepAliveThread.start();
    }

    /**
     * Allows the application to end process on it's own.
     */
    public void stopKeepAlive() {
        mKeepAlive = false;
    }


    /**
     * Resets the console back to its initial state.
     */
    public void reset() {
        stop();
        mInputThread = null;
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

        if (null == mInputThread) {
            mInputThread = new Thread(new ConsoleTask());
            mInputThread.setDaemon(true);
        }

        mAwaitInput = true;

        if (!mInputThread.isAlive()) mInputThread.start();
        mLogger.log(Level.INFO, "Console initialized...");
    }

    /**
     * Stops the console.
     */
    public void stop() {

        mAwaitInput = false;

        mInputThread.interrupt();
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
     * Sets the current working directory which can be called using getmWorkingDirectory.
     * @param workingDirectory A string containing a path to a directory.
     */
    public void setWorkingDirectory(String workingDirectory) {
        File directory = new File(workingDirectory);
        if(directory.isDirectory()) {
            mWorkingDir = directory;
        } else {
            mLogger.log(Level.WARNING, workingDirectory + " is not a valid directory");
        }
    }

    /**
     * The current working directory.
     *
     * @return File pointing to the current working directory.
     */
    public File getmWorkingDirectory() {
        return mWorkingDir;
    }

    /**
     * A simple task that awaits console input.
     */
    private class ConsoleTask implements Runnable {

        @Override
        public void run() {
            while (mAwaitInput) {
                mIsValid = true;

                mCommand = mScanner.nextLine();

                mTokens = mCommand.split(" ");

                mFlags = Arrays.copyOfRange(mTokens, 1, mTokens.length);

                for (int i = 0; i < mFlags.length; ++i) {
                    if (mFlags[i].length() > 0 && '-' != mFlags[i].charAt(0)) {
                        mIsValid = false;
                        break;
                    } else {
                        mFlags[i] = mFlags[i].substring(1);
                    }
                }

                try {
                    if (mIsValid && mCommandMap != null && mCommandMap.containsKey(mTokens[0]))
                       mCommandMap.get(mTokens[0]).accept(mFlags);
                    else if (!mCommand.equals(""))
                        mLogger.log(Level.WARNING, "Command not recognized. Please " +
                                "check usage and try again. Proper syntax is <Command> -<flag> (ex: print -hello)");
                } catch (NullPointerException e) {
                    mLogger.log(Level.WARNING, "There was a problem with the specified command");
                }
            }
        }
    }
}
