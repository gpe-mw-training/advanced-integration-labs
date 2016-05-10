package org.jboss.fuse.security.apiman.junit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.*;
import io.apiman.test.common.json.JsonArrayOrderingType;
import io.apiman.test.common.json.JsonCompare;
import io.apiman.test.common.json.JsonMissingFieldType;
import io.apiman.test.common.resttest.RestTest;
import io.apiman.test.common.util.TestUtil;
import io.apiman.test.common.util.TestVariableResolver;
import io.apiman.test.common.util.TestVariableResolverFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.*;
import org.junit.Assert;
import org.junit.Before;
import org.mvel2.MVEL;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GatewayTestSupport extends CamelTestSupport {

    private static Set<String> resetSysProps = new HashSet<>();
    private static Logger logger = LoggerFactory.getLogger(GatewayTestSupport.class);

    OkHttpClient client;

    @Before
    public void setupHttpClient() {
        client = new OkHttpClient();
        {
            client.setFollowRedirects(false);
            client.setFollowSslRedirects(false);
        }
    }

    /**
     * Create the basic auth header value.
     * @param username
     * @param password
     */
    public static String createBasicAuthorization(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            return null;
        }
        username = GatewayTestSupport.doPropertyReplacement(username);
        password = GatewayTestSupport.doPropertyReplacement(password);
        String val = username + ":" + password;
        return "Basic " + org.apache.commons.codec.binary.Base64.encodeBase64String(val.getBytes()).trim();
    }

    /**
     * Provides Ant-style property replacement support.  This method looks for ${property-name}
     * formatted text and replaces the property with its value.  Values are looked up from
     * the system properties.
     * @param line the line being processed
     * @return the line with all properties replaced
     */
    public static String doPropertyReplacement(String line) {
        if (line == null) {
            return line;
        }
        String rval = line;
        int sidx;
        while ( (sidx = rval.indexOf("${")) != -1 ) {
            int eidx = rval.indexOf('}', sidx);
            String substring = rval.substring(sidx + 2, eidx);
            String propName = substring.trim();
            String defaultValue = "";
            if (propName.contains(":")) {
                int cidx = propName.indexOf(':');
                defaultValue = propName.substring(cidx+1).trim();
                propName = propName.substring(0, cidx).trim();
            }
            String propVal = System.getProperty(propName, defaultValue);
            rval = rval.replace("${" + substring + "}", propVal);
        }
        return rval.replace("@{", "${");
    }

    /**
     * Gets the absolute URL to use to invoke a rest API at a given path.
     *
     * @param path
     * @throws URISyntaxException
     */
    protected static URI getUri(String baseApiUrl, String path) throws URISyntaxException {
        if (baseApiUrl.endsWith("/")) {
            baseApiUrl = baseApiUrl.substring(0, baseApiUrl.length() - 1);
        }
        if (path == null) {
            return new URI(baseApiUrl);
        } else {
            return new URI(baseApiUrl + path);
        }
    }

    /**
     * Loads a rest test from a classpath resource.
     * @param resourcePath
     * @param cl
     */
    public static final RestTest loadRestTest(String resourcePath, ClassLoader cl) {
        InputStream is = null;
        try {
            URL url = cl.getResource(resourcePath);
            if (url == null)
                throw new RuntimeException("Rest Test not found: " + resourcePath);
            is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return parseRestTest(reader);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Parse a *.resttest file.  The format of a *.resttest file is:
     *
     * <pre>
     *     METHOD /path/to/resource username/password
     *     Request-Header-1: value
     *     Request-Header-2: value
     *
     *     **Request Payload**
     *     ----
     *     ResponseStatusCode
     *     Response-Header-1: expected-value
     *     Response-Header-2: expected-value
     *
     *     **Response Payload**
     * </pre>
     * @param reader
     * @throws IOException
     */
    private static RestTest parseRestTest(BufferedReader reader) throws IOException {
        RestTest rval = new RestTest();

        try {
            // METHOD, Path, Username/Password
            String line = reader.readLine();
            String [] split = line.split(" ");
            rval.setRequestMethod(split[0]);
            rval.setRequestPath(split[1]);
            if (split.length >= 3) {
                String userpass = split[2];
                split = userpass.split("/");
                rval.setUsername(split[0]);
                rval.setPassword(split[1]);
            }

            // Request Headers
            line = reader.readLine();
            if (!line.trim().startsWith("----")) {
                while (line.trim().length() > 0) {
                    int idx = line.indexOf(':');
                    String headerName = line.substring(0, idx).trim();
                    String headerValue = line.substring(idx + 1).trim();
                    rval.getRequestHeaders().put(headerName, headerValue);
                    line = reader.readLine();
                }

                // Request payload
                StringBuilder builder = new StringBuilder();
                line = reader.readLine();
                while (!line.trim().startsWith("----")) {
                    builder.append(line).append("\n");
                    line = reader.readLine();
                    line = doPropertyReplacement(line);
                }
                rval.setRequestPayload(builder.toString());
            }

            // Response
            // Expected Status Code
            line = reader.readLine();
            rval.setExpectedStatusCode(new Integer(line.trim()));

            // Expected Response Headers
            line = reader.readLine();
            while (line != null && line.trim().length() > 0) {
                int idx = line.indexOf(':');
                String headerName = line.substring(0, idx).trim();
                String headerValue = line.substring(idx + 1).trim();
                rval.getExpectedResponseHeaders().put(headerName, headerValue);
                line = reader.readLine();
            }

            // Expected Response Payload
            if (line != null) {
                StringBuilder builder = new StringBuilder();
                line = reader.readLine();
                while (line != null && !line.trim().startsWith("----")) {
                    builder.append(line).append("\n");
                    line = reader.readLine();
                }
                rval.setExpectedResponsePayload(builder.toString());
            }
        } catch (Throwable t) {
            throw new IOException("Error while parsing Rest Test", t);
        }

        return rval;
    }

    /**
     * @return the base context of the DT API
     */
    protected String getBaseApiContext() {
        return System.getProperty("apiman.junit.gateway-context", "/");
    }

    /**
     * Assert that the response matched the expected.
     * @param restTest
     * @param response
     */
    public static void assertResponse(RestTest restTest, Response response) {
        int actualStatusCode = response.code();
        try {
            Assert.assertEquals("Unexpected REST response status code.  Status message: "
                            + response.message(), restTest.getExpectedStatusCode(),
                    actualStatusCode);
        } catch (Error e) {
            if (actualStatusCode >= 400) {
                InputStream content = null;
                try {
                    String payload = response.body().string();
                    System.out.println("------ START ERROR PAYLOAD ------");
                    if (payload.startsWith("{")) {
                        payload = payload.replace("\\r\\n", "\r\n").replace("\\t", "\t");
                    }
                    System.out.println(payload);
                    System.out.println("------ END   ERROR PAYLOAD ------");
                } catch (Exception e1) {
                } finally {
                    IOUtils.closeQuietly(content);
                }
            }
            throw e;
        }
        for (Map.Entry<String, String> entry : restTest.getExpectedResponseHeaders().entrySet()) {
            String expectedHeaderName = entry.getKey();
            if (expectedHeaderName.startsWith("X-RestTest-"))
                continue;
            String expectedHeaderValue = entry.getValue();
            String header = response.header(expectedHeaderName);

            Assert.assertNotNull("Expected header to exist but was not found: " + expectedHeaderName, header);
            Assert.assertEquals(expectedHeaderValue, header);
        }
        String ctValue = response.header("Content-Type");
        if (ctValue == null) {
            assertNoPayload(restTest, response);
        } else {
            if (ctValue.startsWith("application/json")) {
                assertJsonPayload(restTest, response);
            } else if (ctValue.startsWith("text/plain") || ctValue.startsWith("text/html")) {
                assertTextPayload(restTest, response);
            } else if (ctValue.startsWith("application/xml") || ctValue.startsWith("application/wsdl+xml")) {
                assertXmlPayload(restTest, response);
            } else {
                Assert.fail("Unsupported response payload type: " + ctValue);
            }
        }
    }

    /**
     * Asserts that the response has no payload and that we are not expecting one.
     * @param restTest
     * @param response
     */
    private static void assertNoPayload(RestTest restTest, Response response) {
        String expectedPayload = restTest.getExpectedResponsePayload();
        if (expectedPayload != null && expectedPayload.trim().length() > 0) {
            Assert.fail("Expected a payload but didn't get one.");
        }
    }

    /**
     * Assume the payload is JSON and do some assertions based on the configuration
     * in the REST Test.
     * @param restTest
     * @param response
     */
    protected static void assertJsonPayload(RestTest restTest, Response response) {
        InputStream inputStream = null;
        try {
            inputStream = response.body().byteStream();
            ObjectMapper jacksonParser = new ObjectMapper();
            JsonNode actualJson = jacksonParser.readTree(inputStream);
            bindVariables(actualJson, restTest);
            String expectedPayload = GatewayTestSupport.doPropertyReplacement(restTest.getExpectedResponsePayload());
            Assert.assertNotNull("REST Test missing expected JSON payload.", expectedPayload);
            JsonNode expectedJson = jacksonParser.readTree(expectedPayload);
            try {
                JsonCompare jsonCompare = new JsonCompare();
                jsonCompare.setArrayOrdering(JsonArrayOrderingType
                        .fromString(restTest.getExpectedResponseHeaders().get("X-RestTest-ArrayOrdering")));
                jsonCompare.setIgnoreCase("true"
                        .equals(restTest.getExpectedResponseHeaders().get("X-RestTest-Assert-IgnoreCase")));
                jsonCompare.setCompareNumericIds("true"
                        .equals(restTest.getExpectedResponseHeaders().get("X-RestTest-Assert-NumericIds")));
                jsonCompare.setMissingField(JsonMissingFieldType.fromString(
                        restTest.getExpectedResponseHeaders().get("X-RestTest-Assert-MissingField")));
                jsonCompare.assertJson(expectedJson, actualJson);
            } catch (Error e) {
                System.out.println("--- START FAILED JSON PAYLOAD ---");
                System.out.println(actualJson.toString());
                System.out.println("--- END FAILED JSON PAYLOAD ---");
                throw e;
            }
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * The payload is expected to be XML.  Parse it and then use XmlUnit to compare
     * the payload with the expected payload (obviously also XML).
     * @param restTest
     * @param response
     */
    protected static void assertXmlPayload(RestTest restTest, Response response) {
        InputStream inputStream = null;
        try {
            inputStream = response.body().byteStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            String xmlPayload = writer.toString();
            String expectedPayload = GatewayTestSupport.doPropertyReplacement(restTest.getExpectedResponsePayload());
            Assert.assertNotNull("REST Test missing expected XML payload.", expectedPayload);
            try {
                XMLUnit.setIgnoreComments(true);
                XMLUnit.setIgnoreAttributeOrder(true);
                XMLUnit.setIgnoreWhitespace(true);
                XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
                XMLUnit.setCompareUnmatched(false);
                Diff diff = new Diff(expectedPayload, xmlPayload);
                // A custom element qualifier allows us to customize how the diff engine
                // compares the XML nodes.  In this case, we're specially handling any
                // elements named "entry" so that we can compare the standard XML format
                // of the Echo API we use for most of our tests.  The format of an
                // entry looks like:
                //    <entry>
                //      <key>Name</key>
                //      <value>Value</value>
                //    </entry>
                diff.overrideElementQualifier(new ElementNameQualifier() {
                    @Override
                    public boolean qualifyForComparison(Element control, Element test) {
                        if (control == null || test == null) {
                            return super.qualifyForComparison(control, test);
                        }
                        if (control.getNodeName().equals("entry") && test.getNodeName().equals("entry")) {
                            String controlKeyName = control.getElementsByTagName("key").item(0).getTextContent();
                            String testKeyName = test.getElementsByTagName("key").item(0).getTextContent();
                            return controlKeyName.equals(testKeyName);
                        }
                        return super.qualifyForComparison(control, test);
                    }
                });
                diff.overrideDifferenceListener(new DifferenceListener() {
                    @Override
                    public void skippedComparison(Node control, Node test) {
                    }
                    @Override
                    public int differenceFound(Difference difference) {
                        String value = difference.getControlNodeDetail().getValue();
                        String tvalue = null;
                        if (difference.getControlNodeDetail().getNode() != null) {
                            tvalue = difference.getControlNodeDetail().getNode().getTextContent();
                        }
                        if ("*".equals(value) || "*".equals(tvalue)) {
                            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                        } else {
                            return RETURN_ACCEPT_DIFFERENCE;
                        }
                    }
                });
                XMLAssert.assertXMLEqual(null, diff, true);
            } catch (Error e) {
                System.out.println("--- START FAILED XML PAYLOAD ---");
                System.out.println(xmlPayload);
                System.out.println("--- END FAILED XML PAYLOAD ---");
                throw e;
            }
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Assume the payload is Text and do some assertions based on the configuration
     * in the REST Test.
     * @param restTest
     * @param response
     */
    private static void assertTextPayload(RestTest restTest, Response response) {
        InputStream inputStream = null;
        try {
            inputStream = response.body().byteStream();
            List<String> lines = IOUtils.readLines(inputStream);
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line).append("\n");
            }

            String actual = builder.toString();
            String expected = restTest.getExpectedResponsePayload();
            if (expected != null) {
                Assert.assertEquals("Response payload (text/plain) mismatch.", expected, actual);
            }
        } catch (Exception e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Binds any variables found in the response JSON to system properties
     * so they can be used in later rest tests.
     * @param actualJson
     * @param restTest
     */
    protected static void bindVariables(JsonNode actualJson, RestTest restTest) {
        for (String headerName : restTest.getExpectedResponseHeaders().keySet()) {
            if (headerName.startsWith("X-RestTest-BindTo-")) {
                String bindExpression = restTest.getExpectedResponseHeaders().get(headerName);
                String bindVarName = headerName.substring("X-RestTest-BindTo-".length());
                String bindValue = evaluate(bindExpression, actualJson);
                log("-- Binding value in response --");
                log("\tExpression: " + bindExpression);
                log("\t    To Var: " + bindVarName);
                log("\t New Value: " + bindValue);
                if (bindValue == null) {
                    System.clearProperty(bindVarName);
                } else {
                    System.setProperty(bindVarName, bindValue);
                }
            }
        }
    }

    /**
     * Evaluates the given expression against the given JSON object.
     *
     * @param bindExpression
     * @param json
     */
    protected static String evaluate(String bindExpression, final JsonNode json) {
        PropertyHandlerFactory.registerPropertyHandler(ObjectNode.class, new PropertyHandler() {
            @Override
            public Object setProperty(String name, Object contextObj, VariableResolverFactory variableFactory,
                    Object value) {
                throw new RuntimeException("Not supported!");
            }

            @Override
            public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory) {
                ObjectNode node = (ObjectNode) contextObj;
                TestVariableResolver resolver = new TestVariableResolver(node, name);
                return resolver.getValue();
            }
        });
        return String.valueOf(MVEL.eval(bindExpression, new TestVariableResolverFactory(json)));
    }

    /**
     * Sets the system property indicated by the specified key unless already defined
     *
     * @param key  the name of the system property.
     * @param value the value of the system property.
     * @return the new value of the system property,
     */
    public static String setProperty(String key, String value) {
        System.setProperty(key, System.getProperty(key, value));
        return System.getProperty(key);
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
    protected void runAndValidate(String expectedResponse, String uri, String payload, String httpMethod, Map<String, String> requestHeaders, String username, String password)
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
        Assert.assertEquals(expectedResponse,response.body().string());
    }

    /**
     * Logs a message.
     *
     * @param message the message
     * @param params  the params
     */
    public static void log(String message, Object... params) {
        String outmsg = MessageFormat.format(message, params);
        logger.info("    >> " + outmsg);
    }

    /**
     * Logs a message.
     *
     * @param message
     */
    protected static void logPlain(String message) {
        logger.info("    >> " + message);
    }


}
