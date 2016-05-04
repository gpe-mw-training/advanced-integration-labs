package org.jboss.fuse.security.oauth;

import org.jboss.fuse.security.oauth.junit.GatewayRestTestPlan;
import org.jboss.fuse.security.oauth.junit.GatewayRestTester;
import org.junit.runner.RunWith;

/**
 *
 *
 */
@RunWith(GatewayRestTester.class)
@GatewayRestTestPlan("test-plans/policies/basic-auth-testPlan.xml")
public class Policy_BasicAuthTest {

}
