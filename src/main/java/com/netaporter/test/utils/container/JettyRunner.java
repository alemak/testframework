package com.netaporter.test.utils.container;

import com.netaporter.test.utils.mockedservices.MockedServiceEnum;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 22/08/2013
 * Time: 12:43
 */
public class JettyRunner {
    static Logger logger = LoggerFactory.getLogger(JettyRunner.class);
    private static final Pattern PATTERN = Pattern.compile(".*:([^:]*/jetty-runner-[^:]*jar).*");
    private static final String DEFAULT_STARTED_PATTERN = ".*Started ServerConnector.*";
    private final CommandLine cmdLine;
    private ExecuteWatchdogWithEvent watchdog;
    private boolean running = false;

    public JettyRunner(
            String warPath,
            Integer port,
            Map<MockedServiceEnum, Integer> mockPorts
    ) {
        cmdLine = buildCommandLine(warPath, port, mockPorts);
    }

    public void startServer() {
        if (!running) {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            // We will kill the process after an hour in case it wasn't killed before
            String startedString = (System.getProperty("mockServers.startedPattern") != null) ?
                    System.getProperty("mockServers.startedPattern") : DEFAULT_STARTED_PATTERN;

            watchdog = new ExecuteWatchdogWithEvent(3600000);
            DefaultExecutor executor = new DefaultExecutor();

            executor.setExitValue(1);
            executor.setWatchdog(watchdog);

            try {
                watchdog.watchForEvent(executor, Pattern.compile(startedString), true);

                executor.execute(cmdLine, resultHandler);
                running = true;
            } catch (IOException e) {
                logger.warn("Server couldn't be started");
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        if (running) {
            watchdog.stopWatching();
            watchdog.destroyProcess();
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isStarted() throws InterruptedException, ExecutionException {
        return watchdog.hasEventOccurred();
    }

    private CommandLine buildCommandLine(String warPath, Integer port, Map<MockedServiceEnum, Integer> mockPorts) {
        CommandLine cmdLine = new CommandLine("java");

        for (MockedServiceEnum mockedServiceEnum : mockPorts.keySet()) {
            Integer mockPort = mockPorts.get(mockedServiceEnum);
            cmdLine.addArgument("-D" + mockedServiceEnum.getSchemeProp() + "=http");
            cmdLine.addArgument("-D" + mockedServiceEnum.getHostProp() + "=localhost");
            cmdLine.addArgument("-D" + mockedServiceEnum.getPortProp() + "=" + mockPort);
        }

        cmdLine.addArgument("-jar");
        cmdLine.addArgument(getJettyRunnerPath());
        cmdLine.addArgument("--port");
        cmdLine.addArgument(port.toString());
        cmdLine.addArgument(warPath);

        return cmdLine;
    }

    private String getJettyRunnerPath() {
        String projectRootDir = JettyServer.getProjectRootDir();
        String pathToLibsFolder = projectRootDir + "/build/libs";
        File libsFolder = new File(pathToLibsFolder);
        if (libsFolder.exists()) {
            String[] filesInFolder = libsFolder.list();
            for (String fileName : filesInFolder) {
                if (fileName.contains("jetty-runner")) {
                    return pathToLibsFolder + "/" + fileName;
                }
            }
            throw new RuntimeException("Jetty runner couldn't be found");
        } else {
            throw new RuntimeException("libs folder doesn't exist");
        }
    }

}
