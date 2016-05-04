package org.jboss.fuse.security.camel.policy;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SimpleAuthenticationProcessor extends DelegateAsyncProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleAuthenticationProcessor.class);
    private static Map<String, String> passwords = new HashMap<String, String>();

    private final SimpleAuthenticationPolicy policy;

    static {
        passwords.put("mickey", "mouse");
        passwords.put("umperio", "bogarto");
        passwords.put("donald", "duck");
    }

    public SimpleAuthenticationProcessor(Processor processor, SimpleAuthenticationPolicy policy) {
        super(processor);
        this.policy = policy;
    }

    @Override public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            applySecurityPolicy(exchange);
        } catch (Exception e) {
            // exception occurred so break out
            exchange.setException(e);
            callback.done(true);
            return true;
        }

        return super.process(exchange, callback);
    }

    private void applySecurityPolicy(Exchange exchange) {
        try {
            String auth = (String) exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION);

            // Extract 'Base ' string from the Header Authorization
            String base64 = auth.substring(6,auth.length());

            // Decrypt the base64 text
            String decoded = new String(Base64.getDecoder().decode(base64), "UTF-8");

            String[] values = decoded.split(":");

            login(values[0],values[1]);
            LOG.debug("Current user {} successfully authenticated", "");

        } catch (NullPointerException ne) {
            throw new AuthenticationException("Authorization Header is not present or can't be decoded");
        } catch (AuthenticationException ae) {
            throw new AuthenticationException("Authentication Failed. There is no user with username and/or password", ae.getCause());
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException("Base64 String can't be decoded", e.getCause());
        }
    }

    private void login(String user, String password) throws AuthenticationException {
        boolean isLogged = false;

        String pass = passwords.get(user);
        if (pass != null && pass.equals(password)) {
            isLogged = true;
        }

        if (!isLogged) {
            throw new AuthenticationException("Authentication Failed. There is no user with username and/or password");
        }
    }

}
