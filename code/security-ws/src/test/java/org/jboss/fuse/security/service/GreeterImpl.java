package org.jboss.fuse.security.service;

import java.util.logging.Logger;

import org.jboss.helloworld.Greeter;

@javax.jws.WebService(name = "Greeter", serviceName = "SOAPService",
        targetNamespace = "http://jboss.org/HelloWorld")
public class GreeterImpl implements Greeter {

    private static final Logger LOG = Logger.getLogger(GreeterImpl.class.getPackage().getName());

    /* (non-Javadoc)
     * @see org.objectweb.HelloWorld.Greeter#greetMe(java.lang.String)
     */
    public String greetMe(String me) {
        LOG.info("Executing operation greetMe");
        System.out.println("Executing operation greetMe");
        System.out.println("Message received: " + me + "\n");
        return "Hello " + me;
    }
}
