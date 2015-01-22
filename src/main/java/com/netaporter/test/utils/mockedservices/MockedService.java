package com.netaporter.test.utils.mockedservices;

import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.behavior.Behavior;

/**
 * Date: 30/05/2013
 * Time: 16:05
 */
public class MockedService {

    private MockedServiceEnum serviceType;

    private Behavior behavior;

    public MockedServiceEnum getServiceType() {
        return serviceType;
    }

    public void setServiceType(MockedServiceEnum serviceType) {
        this.serviceType = serviceType;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public boolean equals(Object o) {
        return behavior.getClass().equals(o.getClass());
    }

    @Override
    public int hashCode() {
        return behavior.getClass().hashCode();
    }
}
