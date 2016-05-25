package org.jboss.fuse.persistence.jpa;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringRouteBuilder;
import org.jboss.fuse.persistence.AbstractJpaTest;
import org.jboss.fuse.persistence.model.SendEmail;
import org.junit.Test;

public class JpaConsumerTest extends AbstractJpaTest {

    protected static final String SELECT_ALL_STRING = "select x from " + SendEmail.class.getName() + " x";

    @Test
    public void testPreConsumed() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:start", new SendEmail("dummy"));

        assertMockEndpointsSatisfied();

        // @PreConsumed should change the dummy address
        SendEmail email = mock.getReceivedExchanges().get(0).getIn().getBody(SendEmail.class);
        assertEquals("dummy@somewhere.org", email.getAddress());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new SpringRouteBuilder() {
            public void configure() {
                from("direct:start").to("jpa://" + SendEmail.class.getName());

                from("jpa://" + SendEmail.class.getName()).to("mock:result");
            }
        };
    }

    @Override
    protected String routeXml() {
        return "org/jboss/fuse/persistence/jpa/springJpaRouteTest.xml";
    }

    @Override
    protected String selectAllString() {
        return SELECT_ALL_STRING;
    }
}