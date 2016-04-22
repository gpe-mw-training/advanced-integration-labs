package org.jboss.fuse.security;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.net.URL;

@org.junit.Ignore
public class AbstractSecurityPolicyTest extends Assert {

    protected Bus bus;
    protected URL busURL;

    @Before
    public void setUpBus() throws Exception {
        if (bus == null) {
            bus = createBus(busURL);
        }
    }

    @After
    public void shutdownBus() {
        if (bus != null) {
            bus.shutdown(false);
            bus = null;
        }
        BusFactory.setDefaultBus(null);
    }

    protected Bus createBus(URL busURL) throws BusException {
        SpringBusFactory bf = new SpringBusFactory();
        Bus bus = bf.createBus(busURL.toString());
        BusFactory.setDefaultBus(bus);
        return bus;
    }

}
