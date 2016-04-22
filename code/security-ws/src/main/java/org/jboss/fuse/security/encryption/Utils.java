package org.jboss.fuse.security.encryption;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Utils {

    public Document convertToDom(Exchange exchange) {
        ByteArrayInputStream body = (ByteArrayInputStream) exchange.getIn().getBody();
        Document d = createDocumentfromInputStream(body, exchange.getContext());
        return d;
    }

    public Document createDocumentfromInputStream(InputStream is, CamelContext context) {
        return context.getTypeConverter().convertTo(Document.class, is);
    }

}
