package org.jboss.fuse.persistence.nosql;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class InfinispanCacheTest extends InfinispanSupportTest {

    @Test
    public void insertCache() throws Exception {
    }

    @Test
    public void GetKeyFromCache() throws Exception {
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override public void configure() {
                from("direct:put")
                    .to("infinispan://localhost?cacheContainer=#cacheContainer&command=PUT")
                    .to("mock:put");

                from("direct:get")
                    .to("infinispan://localhost?cacheContainer=#cacheContainer&command=GET")
                    .to("mock:get");
            }
        };
    }
}
