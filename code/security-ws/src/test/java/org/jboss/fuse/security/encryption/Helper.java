package org.jboss.fuse.security.encryption;

import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.cxf.helpers.IOUtils;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class Helper {

    protected static final String XML_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:enc=\"http://encryption.security.fuse.jboss.org\">\n"
            + "   <soapenv:Header/>\n" + "   <soapenv:Body>\n" + "      <enc:processCheese>\n"
            + "         <arg0>parmezan</arg0>\n" + "      </enc:processCheese>\n" + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    protected static final String XML_ITALY_CHEESE_RESPONSE = "<ns2:country id=\"Italy\" xmlns:ns2=\"http://encryption.security.fuse.jboss.org\"><source>cow</source><cheese>Parmezan</cheese><rating>9/10</rating></ns2:country>";

    static final boolean HAS_3DES;

    static {
        boolean ok = false;
        try {
            org.apache.xml.security.Init.init();
            XMLCipher.getInstance(XMLCipher.TRIPLEDES_KeyWrap);
            ok = true;
        } catch (XMLEncryptionException e) {
            e.printStackTrace();
        }
        HAS_3DES = ok;
    }

    static final boolean UNRESTRICTED_POLICIES_INSTALLED;

    static {
        boolean ok = false;
        try {
            byte[] data = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

            SecretKey key192 = new SecretKeySpec(
                    new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
                            0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 }, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key192);
            c.doFinal(data);
            ok = true;
        } catch (Exception e) {
            //
        }
        UNRESTRICTED_POLICIES_INSTALLED = ok;
    }

    private static Logger log = LoggerFactory.getLogger(Helper.class);

    protected void sendText(final String URI, final Object msg, CamelContext context) throws Exception {
        ProducerTemplate template = context.createProducerTemplate();
        template.start();
        template.send(URI, new Processor() {
            public void process(Exchange exchange) throws Exception {
                // Set the property of the charset encoding
                exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8");
                Message in = exchange.getIn();
                in.setBody(msg);
                if (log.isDebugEnabled()) {
                    log.info(">> Send Message to endpoint {} : {}", URI, msg);
                }
            }
        });
    }

    protected void encryptXMLPayload(String msg, CamelContext context) throws Exception {
        MockEndpoint resultEndpoint = context.getEndpoint("mock:encrypted", MockEndpoint.class);
        resultEndpoint.setExpectedMessageCount(1);
        context.start();
        sendText("direct:encrypt", msg, context);
        resultEndpoint.assertIsSatisfied(100);
        Exchange exchange = resultEndpoint.getExchanges().get(0);

        Document inDoc = getDocumentForInMessage(exchange);
        if (log.isDebugEnabled()) {
            logXMLMessage(">> MESSAGE ENCRYPTED", exchange, inDoc);
        }
        Assert.assertTrue("The XML message has no encrypted data.", hasEncryptedData(inDoc));
        context.stop();
        resultEndpoint.reset();
    }

    protected void encryptXMLPayloadAndDecrypt(String msg, CamelContext context) throws Exception {

        MockEndpoint encrypted = context.getEndpoint("mock:encrypted", MockEndpoint.class);

        MockEndpoint decrypted = context.getEndpoint("mock:decrypted", MockEndpoint.class);
        decrypted.setExpectedMessageCount(1);

        // Send clear message and encrypt it
        context.start();
        sendText("direct:encrypt", msg, context);
        Exchange exchange = encrypted.getExchanges().get(0);
        Document inDoc = getDocumentForInMessage(exchange);

        if (log.isDebugEnabled()) {
            logXMLMessage(">> MESSAGE ENCRYPTED", exchange, inDoc);
        }

        // Decrypt the encrypted message
        sendText("direct:decrypt", inDoc, context);

        decrypted.assertIsSatisfied(100);
        exchange = decrypted.getExchanges().get(0);
        inDoc = getDocumentForInMessage(exchange);
        if (log.isDebugEnabled()) {
            logXMLMessage(">> MESSAGE DECRYPTED", exchange, inDoc);
        }
        Assert.assertFalse("The XML message has encrypted data.", hasEncryptedData(inDoc));

        // Verify that the decrypted message matches what was sent
        Document fragmentDoc = createDocumentfromInputStream(new ByteArrayInputStream(msg.getBytes()),
                exchange);
        Diff xmlDiff = XMLUnit.compareXML(fragmentDoc, inDoc);

        Assert.assertTrue("The decrypted document does not match the control document.", xmlDiff.identical());

        encrypted.reset();
        decrypted.reset();
        context.stop();
    }

    protected void decryptXMLPayload(String msg, CamelContext context) throws Exception {

        MockEndpoint decrypted = context.getEndpoint("mock:decrypted", MockEndpoint.class);
        decrypted.setExpectedMessageCount(1);

        // Send encrypted message to decrypt it
        context.start();
        sendText("direct:decrypt", msg, context);

        // Check that we get from the mock endpoint an exchange
        decrypted.assertIsSatisfied(100);

        Exchange exchange = decrypted.getExchanges().get(0);
        byte[] body = exchange.getIn().getBody(byte[].class);

        if (log.isDebugEnabled()) {
            log.debug(">> MESSAGE DECRYPTED : " + new String(body));
        }

        // Create Document that we will next compare with Original Response Message
        Document responseDoc = exchange.getContext().getTypeConverter().convertTo(Document.class, body);

        // Verify that the decrypted message matches what was sent
        InputStream originalResponse = Helper.class
                .getResourceAsStream("/org/jboss/fuse/security/encryption/response_italy_cheese.xml");
        String response = IOUtils.toString(originalResponse, StandardCharsets.UTF_8.displayName());

        if (log.isDebugEnabled()) {
            log.debug(">> ORIGINAL MESSAGE" + response);
        }

        Document originalDoc = createDocumentfromInputStream(
                new ByteArrayInputStream(XML_ITALY_CHEESE_RESPONSE.getBytes()), exchange);
        Diff xmlDiff = XMLUnit.compareXML(originalDoc, responseDoc);

        Assert.assertTrue("The decrypted document does not match the control document.", xmlDiff.identical());
    }

    public boolean hasEncryptedData(Document doc) throws Exception {
        NodeList nodeList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
        return nodeList.getLength() > 0;
    }

    private void logMessage(String info, Exchange exchange, Document inDoc) throws Exception {
        XmlConverter converter = new XmlConverter();
        String xmlStr = converter.toString(inDoc, exchange);
        if (log.isDebugEnabled()) {
            log.debug(info + ": " + xmlStr);
        }
    }

    private void logXMLMessage(String info, Exchange exchange, Document inDoc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        transformer.transform(new DOMSource(inDoc), streamResult);

        if (log.isDebugEnabled()) {
            log.debug(info + ": " + stringWriter.toString());
        }
    }

    private Document getDocumentForInMessage(Exchange exchange) {
        byte[] body = exchange.getIn().getBody(byte[].class);
        Document d = createDocumentfromInputStream(new ByteArrayInputStream(body), exchange);
        return d;
    }

    public Document createDocumentfromInputStream(InputStream is, Exchange exchange) {
        return exchange.getContext().getTypeConverter().convertTo(Document.class, is);
    }
}
