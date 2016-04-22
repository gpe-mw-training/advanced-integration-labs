package org.jboss.fuse.security.encryption;

import org.apache.cxf.helpers.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SOAPXPathTest {

    private static final Logger LOG = LoggerFactory.getLogger(SOAPXPathTest.class);

    Helper helper = new Helper();

    @Test
    public void testSOAPXpath() throws Exception {

        String soapBodyFile = "soap_body_encrypted_italy_cheese.xml";
        String responseFile = "response_italy_cheese.xml";
        String expression = "/soap:Envelope/soap:Body/xenc:EncryptedData";

        InputStream soapBodyEncryptedStream = SOAPXPathTest.class.getResourceAsStream("/org/jboss/fuse/security/encryption/" + soapBodyFile);
        InputStream responseStream = SOAPXPathTest.class.getResourceAsStream("/org/jboss/fuse/security/encryption/" + responseFile);

        // Encrypt original message in order to compare it
        // String response = IOUtils.toString(responseStream, StandardCharsets.UTF_8.displayName());

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder =  builderFactory.newDocumentBuilder();

        // Parse SOAP Body Encrypted Stream to generate a DOM
        Document xmlSOAPBodyDocument = builder.parse(soapBodyEncryptedStream);

        // Extract SOAP Body content from SOAP Body Encrypted Message using XPath
        XPath xPath = XPathFactory.newInstance().newXPath();

        HashMap<String, String> prefMap = new HashMap<String, String>() {{
            put("soap","http://schemas.xmlsoap.org/soap/envelope/");
            put("xenc", "http://www.w3.org/2001/04/xmlenc#");
        }};
        SimpleNameSpaceContext namespaces = new SimpleNameSpaceContext(prefMap);
        xPath.setNamespaceContext(namespaces);

        Node n = (Node)xPath.compile(expression).evaluate(xmlSOAPBodyDocument, XPathConstants.NODE);

        // Convert the content to a String & omit the XML declaration
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(n), new StreamResult(writer));
        String xmlBodyEncrypted = writer.toString();
        LOG.info("XML Body encrypted : " + xmlBodyEncrypted);

    }
}
