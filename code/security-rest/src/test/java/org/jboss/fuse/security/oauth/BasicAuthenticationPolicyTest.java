package org.jboss.fuse.security.oauth;

import io.apiman.gateway.engine.beans.PolicyFailure;
import io.apiman.gateway.engine.beans.PolicyFailureType;
import io.apiman.gateway.engine.policies.BasicAuthenticationPolicy;
import io.apiman.test.common.mock.EchoResponse;
import io.apiman.test.policies.*;
import org.junit.Assert;
import org.junit.Test;

@TestingPolicy(BasicAuthenticationPolicy.class)
@SuppressWarnings("nls")
public class BasicAuthenticationPolicyTest extends ApimanPolicyTest {

    @Test
    @Configuration("{\r\n" +
            "    \"realm\" : \"TestRealm\",\r\n" +
            "    \"forwardIdentityHttpHeader\":\"X-Authenticated-Identity\",\r\n" +
            "    \"staticIdentity\" : {\r\n" +
            "      \"identities\" : [\r\n" +
            "        { \"username\" : \"ckent\", \"password\" : \"ckent123!\" },\r\n" +
            "        { \"username\" : \"bwayne\", \"password\" : \"bwayne123!\" },\r\n" +
            "        { \"username\" : \"dprince\", \"password\" : \"dprince123!\" }\r\n" +
            "      ]\r\n" +
            "    }\r\n" +
            "}")
    public void testStatic() throws Throwable {
        PolicyTestRequest request = PolicyTestRequest.build(PolicyTestRequestType.GET, "/some/resource");

        // Failure
        try {
            send(request);
            Assert.fail("Expected a failure response!");
        } catch (PolicyFailureError failure) {
            PolicyFailure policyFailure = failure.getFailure();
            Assert.assertNotNull(policyFailure);
            Assert.assertEquals(PolicyFailureType.Authentication, policyFailure.getType());
            Assert.assertEquals(10004, policyFailure.getFailureCode());
        }

        // Failure
        try {
            request.basicAuth("ckent", "invalid_password");
            send(request);
            Assert.fail("Expected a failure response!");
        } catch (PolicyFailureError failure) {
            PolicyFailure policyFailure = failure.getFailure();
            Assert.assertNotNull(policyFailure);
            Assert.assertEquals(PolicyFailureType.Authentication, policyFailure.getType());
            Assert.assertEquals(10003, policyFailure.getFailureCode());
        }

        // Success
        request.basicAuth("ckent", "ckent123!");
        PolicyTestResponse response = send(request);
        Assert.assertEquals(200, response.code());
        EchoResponse echo = response.entity(EchoResponse.class);
        Assert.assertNotNull(echo);
        String header = echo.getHeaders().get("X-Authenticated-Identity");
        Assert.assertNotNull(header);
        Assert.assertEquals("ckent", header);
    }

    @Test
    @Configuration("{\r\n" +
            "    \"realm\" : \"TestRealm\",\r\n" +
            "    \"requireBasicAuth\" : false,\r\n" +
            "    \"staticIdentity\" : {\r\n" +
            "      \"identities\" : [\r\n" +
            "        { \"username\" : \"ckent\", \"password\" : \"ckent123!\" },\r\n" +
            "        { \"username\" : \"bwayne\", \"password\" : \"bwayne123!\" },\r\n" +
            "        { \"username\" : \"dprince\", \"password\" : \"dprince123!\" }\r\n" +
            "      ]\r\n" +
            "    }\r\n" +
            "}")
    public void testBasicAuthNotRequired() throws Throwable {
        PolicyTestRequest request = PolicyTestRequest.build(PolicyTestRequestType.GET, "/some/resource");

        PolicyTestResponse response = send(request);
        EchoResponse echo = response.entity(EchoResponse.class);
        Assert.assertNotNull(echo);
        String header = echo.getHeaders().get("X-Authenticated-Identity");
        Assert.assertNull(header);
    }
}
