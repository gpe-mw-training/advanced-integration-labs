package org.jboss.fuse.security.camel.tls;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private static Map<String, String> passwords = new HashMap<String, String>();

    static {
        passwords.put("mickey", "mouse");
        passwords.put("umperio", "bogarto");
        passwords.put("donald", "duck");
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {

        // get username and password
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(callbacks);
            String username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            String password = new String(tmpPassword);
            ((PasswordCallback)callbacks[1]).clearPassword();

            boolean isLogged=false;

            for (int i = 0; i < callbacks.length; i++) {
                String pass = passwords.get(username);
                if (pass != null && pass.equals(password)) {
                    isLogged=true;
                    break;
                }
            }

            if(!isLogged) {
                throw new LoginException("Login denied for user " + username);
            }
            // add roles
            if ("donald".equals(username)) {
                subject.getPrincipals().add(new MyRolePrincipal("user"));
            } else if ("mickey".equals(username)) {
                subject.getPrincipals().add(new MyRolePrincipal("user"));
                subject.getPrincipals().add(new MyRolePrincipal("admin"));
            }

        } catch (IOException ioe) {
            LoginException le = new LoginException(ioe.toString());
            le.initCause(ioe);
            throw le;
        } catch (UnsupportedCallbackException uce) {
            LoginException le = new LoginException("Error: " + uce.getCallback().toString()
                    + " not available to gather authentication information from the user");
            le.initCause(uce);
            throw le;
        }


        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject = null;
        callbackHandler = null;
        return true;
    }

}
