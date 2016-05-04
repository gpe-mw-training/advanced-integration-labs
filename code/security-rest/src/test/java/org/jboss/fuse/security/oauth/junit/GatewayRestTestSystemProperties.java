package org.jboss.fuse.security.oauth.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * System properties that should be set at the beginning of a rest test.  Old
 * values of these system properties will be restored when the test completes.
 *
 * @author eric.wittmann@redhat.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GatewayRestTestSystemProperties {

    String [] value();

}
