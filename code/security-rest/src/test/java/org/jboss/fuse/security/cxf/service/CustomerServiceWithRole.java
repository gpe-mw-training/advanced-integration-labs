package org.jboss.fuse.security.cxf.service;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * This interface describes a JAX-RS root resource. All the JAXRS annotations (except those overridden) will
 * be inherited by classes implementing it.
 */
@Path("/customerservice/")
public interface CustomerServiceWithRole {

    @GET
    @Path("/customers/{id}/")
    @RolesAllowed({"user"})
    Customer getCustomer(@PathParam("id") String id);
    
    @PUT
    @Path("/customers/")
    Response updateCustomer(Customer customer);
     
    @POST
    @Path("/customers/")
    Response addCustomer(Customer customer);

    @DELETE
    @Path("/customers/{id}/")
    Response deleteCustomer(@PathParam("id") String id);

}
