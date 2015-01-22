package com.netaporter.test.utils.container;

import org.apache.commons.exec.*;
import org.apache.commons.exec.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExecuteWatchdogWithEvent extends ExecuteWatchdog {
    static Logger logger = LoggerFactory.getLogger(ExecuteWatchdogWithEvent.class);

    private Future<Boolean> eventOccured;

    private Future<Boolean> continuousWatching;

    public ExecuteWatchdogWithEvent(long timeout) {
        super(timeout);
    }

    @Override public void timeoutOccured(Watchdog w) {
        // If the standard ExecuteWatchdog finishes, then
        // cancel the event watcher thread.
        stopWatching();

        super.timeoutOccured(w);
    }

    /**
     * Get whether the event has occurred. If the thread is still reading output
     * this will block until the event has been found or the output is finished.
     * Exceptions will be thrown if there is a problem with the watching thread.
     * @return true if event has occurred, false if output has finished without event
     * @throws ExecutionException if the watching thread through an exception
     * @throws InterruptedException if the watching thread was interrupted
     */
    public Boolean hasEventOccurred() throws ExecutionException, InterruptedException {
        return eventOccured.get();
    }

    /**
     * Stop any watching that is currently occuring
     */
    public void stopWatching() {
        if (eventOccured != null && !eventOccured.isDone() && !eventOccured.isCancelled()) {
            eventOccured.cancel(true);
        }

        if (continuousWatching != null && !continuousWatching.isDone() && !continuousWatching.isCancelled()) {
            continuousWatching.cancel(true);
        }
    }
    /**
     * Watch the output of the executor for an occurrence of startPattern.
     * This will happen in a separate thread, and once an occurrence has been found
     * hasEventOccurred can be used to get/wait until a match has occurred.
     * The event watching thread will be cancelled if the watchdog timeout occurs or
     * is interrupted.
     * @param executor Watch this executor
     * @param eventPattern for this pattern
     * @param executorOutput on this output stream
     * @param continueWatching if true, will continue watching after an event has occured,
     *                             and you should take care to call stopWatching() once you are done
     * @throws IOException
     */
    public void watchForEvent(Executor executor, Pattern eventPattern, PipedOutputStream executorOutput, boolean continueWatching) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(executorOutput)));

        Callable eventWatcher = new EventWatcher(reader, eventPattern);

        ExecutorService threadPool = new ScheduledThreadPoolExecutor(1);
        // ScheduledThreadPoolExecuter works on a FIFO basis, thus with a size of one,
        // indefinatedWatching will kick in immediately after the eventOccured has finished
        eventOccured = threadPool.submit(eventWatcher);

        if (continueWatching) {
            Callable continuousWatcher = new EventWatcher(reader, eventPattern);
            continuousWatching = threadPool.submit(continuousWatcher);
        }
    }

    /**
     * Watch the output of the executor for an occurrence of startPattern.
     * @param executor Watch this executor
     * @param eventPattern for this pattern
     * @throws IOException
     */
    public void watchForEvent(Executor executor, Pattern eventPattern, boolean continueWatching) throws IOException {
        PipedOutputStream executorOutput = new PipedOutputStream();
        ExecuteStreamHandler streamHandler = new PumpStreamHandler(executorOutput);
        executor.setStreamHandler(streamHandler);

        watchForEvent(executor, eventPattern, executorOutput, continueWatching);
    }

    /**
     * Read the lines of the output stream, returning true/false if a match is found
     */
    private class EventWatcher implements Callable {
        private Pattern eventPattern;
        private boolean watchingForSpecificEvent;
        private BufferedReader outputReader;

        /**
         * Construct a watcher to watch indefinately
         * @param reader stream to watch
         * @throws IOException
         */
        public EventWatcher(BufferedReader reader) throws IOException {
            this.watchingForSpecificEvent = false;
            this.outputReader = reader;
        }

        /**
         * Construct a watcher to watch until output matching eventPattern
         * @param reader stream to watch
         * @param eventPattern pattern to watch until
         * @throws IOException
         */
        public EventWatcher(BufferedReader reader, Pattern eventPattern) throws IOException {
            this.watchingForSpecificEvent = true;
            this.eventPattern = eventPattern;
            this.outputReader = reader;
        }

        @Override public Boolean call() throws IOException {

            String line;
            while((line = outputReader.readLine()) != null) {
                logger.info(line);
                if (this.watchingForSpecificEvent) {
                    Matcher matcher = eventPattern.matcher(line);
                    if (matcher.matches()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
