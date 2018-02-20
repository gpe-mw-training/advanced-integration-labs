package org.fuse.usecase;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.cxf.message.MessageContentsList;
import org.globex.Account;
import org.globex.CorporateAccount;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.SequenceInputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {

    public void readFile(Exchange exchange) throws IOException {
	int c;
	Object idObj = exchange.getIn().getHeader("id");
	String sSuffix = idObj.toString();
	int suffix = Integer.valueOf(sSuffix);
	Object fileObj = exchange.getIn().getHeader("fileDir");
	String fileDir = fileObj.toString();
	//	String fileName = fileDir + "account-" + suffix + ".json";
	String fileName = "/Users/nandanjoshi/partnertraining/fuse_integration_advanced/code/homework/routing/" + fileDir + "account-" + suffix + ".json";
	System.out.println("fileName = " + fileName + "\n");
        FileInputStream fis = new FileInputStream(fileName);
        StringBuffer buf = new StringBuffer();
        while ((c = fis.read()) != -1) {
            buf.append((char) c);
        }
        fis.close();
	System.out.println("Contents of the file are " +buf.toString() + "\n");
        exchange.getIn().setBody(buf.toString());
    }
}
