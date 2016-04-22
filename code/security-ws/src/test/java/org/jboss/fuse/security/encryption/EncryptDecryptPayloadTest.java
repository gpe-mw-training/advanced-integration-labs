package org.jboss.fuse.security.encryption;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.*;

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
import java.util.HashMap;

public class EncryptDecryptPayloadTest extends CamelSpringTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptDecryptPayloadTest.class);

    Helper helper = new Helper();

    protected AbstractApplicationContext createApplicationContext() {
    }

    @Test
    public void testXMLPayloadEncryption() throws Exception {
    }

    @Test
    public void testXMLPayloadDecryption() throws Exception {
    }

    @Test
    public void testEncryptedSOAPBody() {
    }

}
