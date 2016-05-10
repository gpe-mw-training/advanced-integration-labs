package org.jboss.fuse.security.apiman.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface Configuration {

    String value() default "";

    /**
     * String array of policy configuration files on the classpath, such as: <code>/my-api-config.json</code>
     *
     * @return the classpath file path
     */
    String[] cpConfigFiles() default "";
}