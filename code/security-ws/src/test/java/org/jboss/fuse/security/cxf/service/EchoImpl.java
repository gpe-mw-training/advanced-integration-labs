package org.jboss.fuse.security.cxf.service;

import javax.jws.WebService;

@WebService(endpointInterface = "org.jboss.fuse.security.cxf.service.Echo")
public class EchoImpl implements Echo {

    public String echo(String text) {
        return text;
    }

}
