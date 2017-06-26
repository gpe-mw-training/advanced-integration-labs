package org.jboss.fuse.bam.intercept;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;

public class InterceptTest extends CamelTestSupport {

    private static final String BODY1 = "You say Goodbye";
    private static final String BODY = "I say Hello";

    @Test
    public void testInterceptPred() throws Exception {


    @Test
    public void testInterceptPredNoop() throws Exception {


    }

    @Test
    public void testInterceptFromPred() throws Exception {

    }

    @Test
    public void testInterceptFromPredNoop() throws Exception {

    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        return jndi;
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
	

            }
        };
    }

}
