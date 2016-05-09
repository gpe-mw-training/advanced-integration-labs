package org.jboss.fuse.security.oauth;

import com.squareup.okhttp.*;
import io.apiman.test.common.util.TestUtil;
import org.apache.commons.codec.binary.Base64;
import org.jboss.fuse.security.oauth.junit.Configuration;
import org.jboss.fuse.security.oauth.junit.GatewayTestUtil;
import org.jboss.fuse.security.oauth.junit.GatewayTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(GatewayTester.class)
public class BasicAuthenticationTest extends GatewayTestUtil {

    OkHttpClient client;
    Map<String,String> headers = new HashMap<String,String>();

    @Before
    public void setupHttpClient() {
        client = new OkHttpClient();
        {
            client.setFollowRedirects(false);
            client.setFollowSslRedirects(false);
        }
    }

    @Test
    @Configuration(cpConfigFiles = {
            "test-plan-data/policies/basic-auth-static/001-publish-api.resttest",
            "test-plan-data/policies/basic-auth-static/002-register-client.resttest" })
    public void SimpleTest() throws IOException {

        String expectedResponse = "{\n" + "  \"method\" : \"GET\",\n"
                + "  \"resource\" : \"/path/to/app/resource\",\n" + "  \"uri\" : \"/path/to/app/resource\",\n"
                + "  \"headers\" : {\n" + "    \"X-Authenticated-Identity\" : \"bwayne\"\n" + "  }\n" + "}\n";
        headers.put("X-API-Key","12345");
        runAndValidate(expectedResponse,"http://localhost:6060/gateway/Policy_BasicAuthStatic/echo/1.0.0/path/to/app/resource","","GET",headers,"bwayne","bwayne");
    }

    /**
     * Run a HTTP request using OkHTTP client and validate the response
     *
     * @param expectedResponse
     * @param uri
     * @param payload
     * @param httpMethod
     * @param requestHeaders
     * @param username
     * @param password
     * @throws IOException
     */
    public void runAndValidate(String expectedResponse, String uri, String payload, String httpMethod, Map<String, String> requestHeaders, String username, String password)
            throws IOException {

        String rawType = "application/json";
        MediaType mediaType = MediaType.parse(rawType);

        RequestBody body = null;
        if (payload != null && !payload.isEmpty()) {
            body = RequestBody.create(mediaType, payload.getBytes());
        }

        Request.Builder requestBuilder = new Request.Builder().url(uri.toString()).method(httpMethod, body);

        for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
            String value = TestUtil.doPropertyReplacement(entry.getValue());
            // Handle system properties that may be configured in the rest-test itself
            if (entry.getKey().startsWith("X-RestTest-System-Property")) {
                String[] split = value.split("=");
                System.setProperty(split[0], split[1]);
                continue;
            }
            requestBuilder.addHeader(entry.getKey(), value);
        }

        // Set up basic auth
        String authorization = createBasicAuthorization(username, password);
        if (authorization != null) {
            requestBuilder.addHeader("Authorization", authorization);
        }

        Response response = client.newCall(requestBuilder.build()).execute();
        Assert.assertEquals(200,response.code());
        Assert.assertEquals("OK",response.message());
    }

}
