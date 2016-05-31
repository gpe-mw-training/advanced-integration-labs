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
        MockEndpoint mock = (MockEndpoint)context().getEndpoint("mock:put");
        mock.expectedMessageCount(2);

        Map<String, Object> cacheHeaders = new HashMap<String, Object>();
        cacheHeaders.put(InfinispanConstants.KEY, KEY_ONE);
        cacheHeaders.put(InfinispanConstants.VALUE, VALUE_ONE);

        template.sendBodyAndHeaders("direct:put",null,cacheHeaders);

        cacheHeaders.put(InfinispanConstants.KEY, KEY_TWO);
        cacheHeaders.put(InfinispanConstants.VALUE, VALUE_TWO);

        template.sendBodyAndHeaders("direct:put",null,cacheHeaders);

        Assert.assertEquals("valueOne",currentCache().get(KEY_ONE));
        Assert.assertEquals("valueTwo",currentCache().get(KEY_TWO));
        mock.assertIsSatisfied();
    }

    @Test
    public void GetKeyFromCache() throws Exception {
        MockEndpoint mock = (MockEndpoint)context().getEndpoint("mock:get");
        mock.expectedMessageCount(2);

        Assert.assertEquals("valueOne",currentCache().get(KEY_ONE));
        Assert.assertEquals("valueTwo",currentCache().get(KEY_TWO));

        Map<String, Object> cacheHeaders = new HashMap<String, Object>();
        cacheHeaders.put(InfinispanConstants.KEY, KEY_ONE);
        template.sendBodyAndHeaders("direct:get",null,cacheHeaders);

        cacheHeaders.put(InfinispanConstants.KEY, KEY_TWO);
        template.sendBodyAndHeaders("direct:get",null,cacheHeaders);

        String val1 = (String) mock.getExchanges().get(0).getIn().getHeader(InfinispanConstants.RESULT);
        String val2 = (String) mock.getExchanges().get(1).getIn().getHeader(InfinispanConstants.RESULT);
        Assert.assertEquals("valueOne",val1);
        Assert.assertEquals("valueTwo",val2);
        mock.assertIsSatisfied();
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
