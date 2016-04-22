package org.jboss.fuse.security;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

@org.junit.Ignore
public class AbstractSecurityTest extends Assert {

    protected Bus bus;

    @Before
    public void setUpBus() throws Exception {
        if (bus == null) {
            bus = createBus();
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

    protected Bus createBus() throws BusException {
        return BusFactory.newInstance().createBus();
    }

}
