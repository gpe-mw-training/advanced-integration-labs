package org.jboss.fuse.persistence.nosql;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.AfterClass;
import org.junit.Before;

public class InfinispanSupportTest extends CamelTestSupport {

    protected static final String KEY_ONE = "keyOne";
    protected static final String VALUE_ONE = "valueOne";
    protected static final String KEY_TWO = "keyTwo";
    protected static final String VALUE_TWO = "valueTwo";

    protected static BasicCacheContainer basicCacheContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        if (basicCacheContainer == null) {
            basicCacheContainer = new DefaultCacheManager();
            basicCacheContainer.start();
        }
        super.setUp();
    }

    @AfterClass
    public static void closeCache() {
        basicCacheContainer.stop();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("cacheContainer", basicCacheContainer);
        return registry;
    }

    protected BasicCache<Object, Object> currentCache() {
        return basicCacheContainer.getCache();
    }
}
