package com.netaporter.test.utils.mockedservices;

import com.xebialabs.restito.server.StubServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: a.kogan@london.net-a-porter.com
 * Date: 14/03/2013
 * Time: 15:36
 */
@Component
public class RestitoMock {
    static Logger logger = LoggerFactory.getLogger(RestitoMock.class);
    @Value("#{mockedServices}")
    private List<MockedService> mockedServices;

    /**
     * All the running mocks indexed by their Behaviour Class. Only one mock per class.
     * Can contain more than one mock for the same service for different tests
     */
    private Map<MockedService, StubServer> runningMocks = new HashMap();

    @PostConstruct
    public void start() {
        for (MockedService mockedService : mockedServices) {
            runningMocks.put(mockedService, new StubServer(mockedService.getBehavior()).run());
        }
        logger.info(runningMocks.size() + " Restito servers started");
    }

    @PreDestroy
    public void stop() {
        for (StubServer mockedService : runningMocks.values()) {
            mockedService.stop();
        }
       logger.info("Restito servers stopped");
    }

    /**
     * All the mock mockedServices.
     */
    public List<MockedService> getMockedServices() {
        return mockedServices;
    }

    public void setMockedServices(List<MockedService> mockedServices) {
        this.mockedServices = mockedServices;
    }

    /**
     * All the running mocks indexed by their Behaviour Class. Only one mock per class.
     * Can contain more than one mock for the same service for different tests
     */
    public Map<MockedService, StubServer> getRunningMocks() {
        return runningMocks;
    }

    public void setRunningMocks(Map<MockedService, StubServer> runningMocks) {
        this.runningMocks = runningMocks;
    }
}
