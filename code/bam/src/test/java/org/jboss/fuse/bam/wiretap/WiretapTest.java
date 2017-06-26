package org.jboss.fuse.bam.wiretap;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

public class WiretapTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:wiretap-output") private MockEndpoint wiretapEndpoint;
    @EndpointInject(uri = "mock:wiretap-output-new") private MockEndpoint wiretapEndpointNew;

    @Test
    public void testWiretapWithCopy() throws Exception {

    }

    @Test
    public void testWiretapWithNew() throws Exception {

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/bam/wiretap/camelContext.xml");
    }
}
