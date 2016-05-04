package org.jboss.fuse.security.oauth.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate an apiman REST Test junit test.  The
 * value of the annotation should be a path to a REST Test test plan
 * XML file.
 *
 * @author eric.wittmann@redhat.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GatewayRestTestPlan {

    String value();

}
