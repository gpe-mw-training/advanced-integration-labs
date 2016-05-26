package org.jboss.fuse.persistence.nosql;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class InfinispanConsumerTest extends InfinispanSupportTest {

    @EndpointInject(uri = "mock:created")
    private MockEndpoint mockCreated;

    @EndpointInject(uri = "mock:removed")
    private MockEndpoint mockRemoved;

    @Test
    public void consumerReceivedPreAndPostEntryCreatedEventNotifications() throws Exception {
        mockCreated.expectedMessageCount(2);

        mockCreated.message(0).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_CREATED");
        mockCreated.message(0).outHeader(InfinispanConstants.IS_PRE).isEqualTo(true);
        mockCreated.message(0).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockCreated.message(0).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        mockCreated.message(1).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_CREATED");
        mockCreated.message(1).outHeader(InfinispanConstants.IS_PRE).isEqualTo(false);
        mockCreated.message(1).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockCreated.message(1).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        currentCache().put(KEY_ONE, VALUE_ONE);
        mockCreated.assertIsSatisfied();
    }

    @Test
    public void consumerReceivedPreAndPostEntryRemoveEventNotifications() throws Exception {
        currentCache().put(KEY_ONE, VALUE_ONE);

        mockRemoved.expectedMessageCount(2);

        mockRemoved.message(0).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_REMOVED");
        mockRemoved.message(0).outHeader(InfinispanConstants.IS_PRE).isEqualTo(true);
        mockRemoved.message(0).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockRemoved.message(0).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        mockRemoved.message(1).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_REMOVED");
        mockRemoved.message(1).outHeader(InfinispanConstants.IS_PRE).isEqualTo(false);
        mockRemoved.message(1).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockRemoved.message(1).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        currentCache().remove(KEY_ONE);
        mockRemoved.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override public void configure() {
                from("infinispan://localhost?cacheContainer=#cacheContainer&sync=false&eventTypes=CACHE_ENTRY_CREATED")
                        .to("mock:created");

                from("infinispan://localhost?cacheContainer=#cacheContainer&sync=false&eventTypes=CACHE_ENTRY_REMOVED")
                        .to("mock:removed");
            }
        };
    }
}
