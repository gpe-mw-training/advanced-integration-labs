package org.jboss.fuse.persistence.jpa;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.fuse.persistence.AbstractJpaTest;
import org.jboss.fuse.persistence.model.SendEmail;
import org.junit.Test;
import org.wildfly.extension.camel.CamelAware;

public class JpaConsumerTest extends AbstractJpaTest {

    @EndpointInject(uri = "mock:result")
    MockEndpoint mock;

    protected static final String SELECT_ALL_STRING = "select x from " + SendEmail.class.getName() + " x";

    @Test
    public void testInsertAndReceive() throws Exception {

        mock.expectedMessageCount(3);
        mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 3);

        template.sendBody("direct:start", new SendEmail("alpha"));
        template.sendBody("direct:start", new SendEmail("beta"));
        template.sendBody("direct:start", new SendEmail("dummy"));

        assertMockEndpointsSatisfied();

        SendEmail email = mock.getReceivedExchanges().get(2).getIn().getBody(SendEmail.class);
        assertEquals("dummy@somewhere.org", email.getAddress());
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