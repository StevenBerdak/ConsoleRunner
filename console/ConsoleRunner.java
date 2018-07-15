package com.triquesoft.slydables.wordlibgen.console;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up a console environment in a way which does not block the main thread.
 */
public class ConsoleRunner extends Observable {

    public static final String KEEP_ALIVE_INTERRUPTED = "keep_alive_interrupted";
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
    private Observer mObserver;

    /**
     * Constructor.
     *
     * @param inputStream The input stream to use for reading commands.
     */
    public ConsoleRunner(String consoleName, InputStream inputStream, Observer observer) {
        mLogger = Logger.getLogger(consoleName);
        this.mInputStream = inputStream;
        this.mObserver = observer;
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
        mKeepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mKeepAlive) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        mObserver.update(ConsoleRunner.this, KEEP_ALIVE_INTERRUPTED);
                        e.printStackTrace();
                    }
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
     * Registers an observer to listen for events.
     * @param observer Observer to be registered.
     */
    public void registerObserver(Observer observer) {
        this.addObserver(observer);
    }

    /**
     * Unregisters an observer from listening for events.
     * @param observer Observer to be unregistered.
     */
    public void unregisterObserver(Observer observer) {
        this.deleteObserver(observer);
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
