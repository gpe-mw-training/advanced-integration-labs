package org.jboss.fuse.security.camel.basic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.jboss.fuse.security.camel.common.BaseJettyTest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

public class BasicAuthCamelTest extends BaseJettyTest {

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("myAuthHandler", getSecurityHandler());
        return jndi;
    }

    private SecurityHandler getSecurityHandler() throws IOException {
        // Describe the Authentication Constraint to be applied (BASIC, DISGEST, NEGOTIATE, ...)
        Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, "user");
        constraint.setAuthenticate(true);

        // Map the Auth Contrainst with a Path
        ConstraintMapping cm = new ConstraintMapping();
        cm.setPathSpec("/*");
        cm.setConstraint(constraint);

        /* A security handler is a jetty handler that secures content behind a
           particular portion of a url space. The ConstraintSecurityHandler is a
           more specialized handler that allows matching of urls to different
           constraints. The server sets this as the first handler in the chain,
           effectively applying these constraints to all subsequent handlers in
           the chain.
           The BasicAuthenticator instance is the object that actually checks the credentials
        */
        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setAuthenticator(new BasicAuthenticator());
        sh.setConstraintMappings(Arrays.asList(new ConstraintMapping[] { cm }));

        //  The HashLogin is an implementation of a UserRealm that stores users and roles in-memory in HashMaps.
        HashLoginService loginService = new HashLoginService("MyRealm",
                "src/test/resources/org/jboss/fuse/security/basic/myrealm.props");
        sh.setLoginService(loginService);
        sh.setConstraintMappings(Arrays.asList(new ConstraintMapping[] { cm }));

        return sh;
    }

    @Test
    public void testHttpBasicAuthCamel() throws Exception {
        String out = template.requestBody(
                "http://localhost:{{port1}}/test?authMethod=Basic&authUsername=donald&authPassword=duck",
                "Hello World", String.class);
        assertEquals("Bye World", out);
    }


}
