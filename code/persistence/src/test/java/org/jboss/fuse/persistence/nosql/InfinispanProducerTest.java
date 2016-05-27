/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.fuse.persistence.nosql;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfinispanProducerTest extends InfinispanSupportTest {

    private static final String COMMAND_VALUE = "commandValue";
    private static final String COMMAND_KEY = "commandKey1";
    private static final long LIFESPAN_TIME = 5;
    private static final long LIFESPAN_FOR_MAX_IDLE = -1;
    private static final long MAX_IDLE_TIME = 3;

    @Test
    public void keyAndValueArePublishedWithDefaultOperation() throws Exception {

        Map<String, Object> headers = new HashMap<>();
        headers.put(InfinispanConstants.KEY, KEY_ONE);
        headers.put(InfinispanConstants.VALUE, VALUE_ONE);

        template.sendBodyAndHeaders("direct:start",null,headers);

        Object value = currentCache().get(KEY_ONE);
        assertEquals(VALUE_ONE, value.toString());
    }

    @Test
    public void publishKeyAndValueByExplicitlySpecifyingTheOperation() throws Exception {
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(InfinispanConstants.KEY, KEY_ONE);
                exchange.getIn().setHeader(InfinispanConstants.VALUE, VALUE_ONE);
                exchange.getIn().setHeader(InfinispanConstants.OPERATION, InfinispanConstants.PUT);
            }
        });

        Object value = currentCache().get(KEY_ONE);
        assertEquals(VALUE_ONE, value.toString());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .to("infinispan://localhost?cacheContainer=#cacheContainer");
            }
        };
    }
}
