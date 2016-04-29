package org.jboss.fuse.security.wssecurity;

import org.apache.cxf.testutil.common.TestUtil;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.jboss.fuse.security.cxf.service.PwdCallback;

public class WSSecurityApp {

    public static final String PORT = TestUtil.getPortNumber(WSSecurityApp.class);

    public static void main(String[] args) throws Exception {
        WSSecurityTest ws = new WSSecurityTest();
        ws.setUpBus();
        ws.setUpWSEndpoint(PORT);

        // Setup the Interceptor for the Server
        String actions = WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN;
        ws.wsIn.setProperty(WSHandlerConstants.ACTION, actions);

        // Setup the Interceptor for the Client
        ws.wsOut.setProperty(WSHandlerConstants.ACTION, actions);
        ws.wsOut.setProperty(WSHandlerConstants.USER, "cmoulliard");
        ws.wsOut.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, PwdCallback.class.getName());

        System.out.println("Server ready...");

        Thread.sleep(5 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }

}
