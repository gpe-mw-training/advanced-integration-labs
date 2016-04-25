package org.jboss.fuse.security.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface Echo {
    @WebMethod
    String echo(@WebParam(name = "text")
            String text);
}