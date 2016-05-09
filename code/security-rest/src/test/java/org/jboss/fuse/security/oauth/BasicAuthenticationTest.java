package org.jboss.fuse.security.oauth;

import org.jboss.fuse.security.oauth.junit.Configuration;
import org.jboss.fuse.security.oauth.junit.GatewayTestSupport;
import org.jboss.fuse.security.oauth.junit.GatewayTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(GatewayTester.class)
public class BasicAuthenticationTest extends GatewayTestSupport {

    Map<String,String> headers = new HashMap<String,String>();

    @Test
    @Configuration(cpConfigFiles = {
            "test-plan-data/policies/basic-auth-static/001-publish-api.resttest",
            "test-plan-data/policies/basic-auth-static/002-register-client.resttest" })
    public void testBasicAuthenticationAgainstApi() throws IOException {

        String expectedResponse = "{\n" + "  \"method\" : \"GET\",\n"
                + "  \"resource\" : \"/path/to/app/resource\",\n" + "  \"uri\" : \"/path/to/app/resource\",\n"
                + "  \"headers\" : {\n" + "    \"X-Authenticated-Identity\" : \"bwayne\"\n" + "  }\n" + "}\n";
        headers.put("X-API-Key","12345");
        runAndValidate(expectedResponse,"http://localhost:6060/gateway/Policy_BasicAuthStatic/echo/1.0.0/path/to/app/resource","","GET",headers,"bwayne","bwayne");
    }

}
