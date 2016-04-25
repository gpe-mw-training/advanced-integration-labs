package org.jboss.fuse.security.service;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PwdCallback implements CallbackHandler {

    private static Map<String, String> passwords = new HashMap<String, String>();

    static {
        passwords.put("cmoulliard", "drailluomc");
        passwords.put("integration", "secUr1t8"); // Alias of the certificate created within the keystore integration.jks file
        passwords.put("alice", "ecilla");
        passwords.put("jbride", "edirbj");
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    }
}
