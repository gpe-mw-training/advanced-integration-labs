package org.jboss.fuse.security;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RemoveMustUnderstandHeader extends AbstractPhaseInterceptor<Message> {

    public RemoveMustUnderstandHeader() {
        super(Phase.RECEIVE);
        addBefore(LoggingInInterceptor.class.getName());
    }

    public void handleMessage(Message message) throws Fault {
        String stringToSearch = "soap:mustUnderstand=\"1\"";
        String stringToReplace = "soap:mustUnderstand=\"0\"";
        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            CachedOutputStream bos = new CachedOutputStream();
            try {
                IOUtils.copy(is, bos);

                bos.flush();
                is.close();
                message.setContent(InputStream.class, bos.getInputStream());
                bos.close();
                String soapMessage = new String(bos.getBytes());
                if (soapMessage.indexOf(stringToSearch) >= 0) {
                    //add code here to change soapMessage to soap:mustUnderstand="0"
                    soapMessage = soapMessage.replace(stringToSearch,stringToReplace);
                    ByteArrayInputStream bais = new ByteArrayInputStream(soapMessage.getBytes());
                    if (bais != null) {
                        message.setContent(InputStream.class, bais);
                    }
                }
            } catch (IOException e) {
                throw new Fault(e);
            }
        }

    }
}
