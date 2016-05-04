package org.jboss.fuse.security.camel.policy;

import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.camel.spi.RouteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthenticationPolicy implements AuthorizationPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleAuthenticationPolicy.class);

    @Override
    public void beforeWrap(RouteContext routeContext, ProcessorDefinition<?> definition) {
    }

    @Override
    public Processor wrap(RouteContext routeContext, Processor processor) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Securing route {} using Shiro policy {}", routeContext.getRoute().getId(), this);
        }
        return new SimpleAuthenticationProcessor(processor, this);
    }
}
