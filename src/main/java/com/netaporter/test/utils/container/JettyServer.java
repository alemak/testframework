package com.netaporter.test.utils.container;

import com.google.common.collect.Maps;
import com.netaporter.test.utils.mockedservices.MockedService;
import com.netaporter.test.utils.mockedservices.MockedServiceEnum;
import com.netaporter.test.utils.mockedservices.RestitoMock;
import com.xebialabs.restito.server.StubServer;
import org.apache.mina.util.AvailablePortFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 15/03/2013
 * Time: 15:54
 */
@Component(value = "jettyServer")
public class JettyServer {
    static Logger logger = LoggerFactory.getLogger(JettyServer.class);
    public final static int DEFAULT_PORT = 7777;

    private static final Pattern LOCAL_USERDIR_PATTERN = Pattern.compile("(.+-service)/?.*$");
    private static final Pattern JENKINS_USERDIR_PATTERN = Pattern.compile("(.+)/.+-testing/?.*$");

    private Map<MockedServiceEnum, StubServer> stubServersInUse = new HashMap<MockedServiceEnum, StubServer>();

    @Autowired
    private RestitoMock restitoMocks;

    // Make sure to use a property name without periods in it.
    // See http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/core/env/SystemEnvironmentPropertySource.html
    @Value("${war_path}")
    private String warPath;
    private Integer port;

    @Value("${container.auto.start}")
    private String autoStartStr;

    private JettyRunner runner;

    @PostConstruct
    public void setup() {
        for(MockedService mockedService: restitoMocks.getMockedServices()) {
            StubServer stubServer = restitoMocks.getRunningMocks().get(mockedService);
            setMockedService(mockedService.getServiceType(), stubServer);
        }

        if (isAutoStart()) {
            startService();
        } else {
            // starting the service assigns a port, but if not autostarting then need a value for clients
            port = AvailablePortFinder.getNextAvailable(DEFAULT_PORT);
        }

    }

    /**
     * Starts the service if not already running
     */
    public void startService() {
        if (! isRunning()) {
            port = AvailablePortFinder.getNextAvailable(DEFAULT_PORT);

            runner = new JettyRunner(
                    getWarAbsolutePath(),
                    port,
                    getMocksPorts()
            );

            runner.startServer();

            // We wait some time for the application to start
            try {
                if (runner.isStarted()) {
                   logger.info("Server started");
                } else {
                    logger.warn("Server did not start successfully");
                    stopService();
                }
            } catch (ExecutionException e) {
                logger.warn("Unable to monitor server startup");
                e.printStackTrace();
            } catch (InterruptedException e) {
                logger.warn("Server startup was interrupted");
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void stopService() {
        if (isRunning()) {
            runner.stopServer();
        }
    }

    public void restartService() {
        stopService();
        startService();
    }

    public boolean isRunning() {
        return (runner != null && runner.isRunning());
    }

    public int getPort() {
        return this.port;
    }

    /**
     * Sets the mock for the given service and restarts the server in case it is running for it to register the changes
     * Will restart the service.
     * @param serviceEnum the service enum
     * @param mockServer the Restito stub server
     */
    public void setMockedService(MockedServiceEnum serviceEnum, StubServer mockServer) {
        setMockedService(serviceEnum, mockServer, true);
    }

    /**
     * Sets the mock for the given service and restarts the server in case it is running for it to register the changes
     * @param serviceEnum the service enum
     * @param mockServer the Restito stub server
     * @param restartIfRunning If true, restart the service if already running
     */
    public void setMockedService(MockedServiceEnum serviceEnum, StubServer mockServer, boolean restartIfRunning) {
        stubServersInUse.put(serviceEnum, mockServer);
        if (restartIfRunning && isRunning()) {
            restartService();
        }
    }

    /**
     * All the mocks current in use by the service
     */
    public Map<MockedServiceEnum, StubServer> getStubServersInUse() {
        return Collections.unmodifiableMap(stubServersInUse);
    }

    private Map<MockedServiceEnum, Integer> getMocksPorts() {
        if (!stubServersInUse.isEmpty()) {
            Map<MockedServiceEnum, Integer> mockPorts = Maps.newHashMapWithExpectedSize(stubServersInUse.size());
            for (MockedServiceEnum mockedServiceEnum : stubServersInUse.keySet()) {
                mockPorts.put(mockedServiceEnum, stubServersInUse.get(mockedServiceEnum).getPort());
            }
            return mockPorts;
        } else {
            return Collections.emptyMap();
        }
    }

    private String getWarAbsolutePath() {
        System.out.println("Absolute WAR path = " + getProjectRootDir() + warPath);
        return getProjectRootDir() + warPath;
    }

    public static String getProjectRootDir() {
       logger.info("user.dir = " + System.getProperty("user.dir"));
        Matcher localUserDirMatcher = LOCAL_USERDIR_PATTERN.matcher(System.getProperty("user.dir"));
        if (localUserDirMatcher.matches()) {
            return localUserDirMatcher.group(1);
        } else {
            Matcher jenkinsUserDirMatcher = JENKINS_USERDIR_PATTERN.matcher(System.getProperty("user.dir"));
            if (jenkinsUserDirMatcher.matches()) {
                return jenkinsUserDirMatcher.group(1);
            }
        }
        throw new RuntimeException("Project root folder could not be matched. Jenkins can't be started");
    }

    private boolean isAutoStart() {
        // default true
        return (autoStartStr == null) || (!autoStartStr.trim().equalsIgnoreCase("false"));
    }
}
