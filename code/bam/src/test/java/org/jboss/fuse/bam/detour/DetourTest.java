package org.jboss.fuse.bam.detour;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;

public class DetourTest extends CamelTestSupport {
    
    private static final String BODY = "<order custId=\"123\"/>";
    private ControlBean controlBean;

    @Test
    public void testDetourSet() throws Exception {
    }

    @Test
    public void testDetourNotSet() throws Exception {      
    }    
    
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        controlBean = new ControlBean();
        jndi.bind("controlBean", controlBean);
        return jndi;
    }    

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
            }
        };
    }
    
    public final class ControlBean {
        private boolean detour;  

        public void setDetour(boolean detour) {
            this.detour = detour;
        }

        public boolean isDetour() {
            return detour;
        }
    }    
}
