package org.jboss.fuse.security.wstrust;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class STSCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if (pc.getUsage() == WSPasswordCallback.DECRYPT 
                    || pc.getUsage() == WSPasswordCallback.SIGNATURE) {
                    if ("mystskey".equals(pc.getIdentifier())) {
                        pc.setPassword("stskpass");
                    }
                } else if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {
                    if ("alice".equals(pc.getIdentifier())) {
                        pc.setPassword("clarinet");
                    }
                }
            }
        }
    }
}

