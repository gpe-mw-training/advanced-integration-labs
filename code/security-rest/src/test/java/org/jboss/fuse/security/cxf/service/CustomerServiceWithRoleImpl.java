package org.jboss.fuse.security.cxf.service;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class CustomerServiceWithRoleImpl implements CustomerServiceWithRole {

    long currentId = 123;
    Map<Long, Customer> customers = new HashMap<Long, Customer>();

    public CustomerServiceWithRoleImpl() {
        init();
    }

    public Customer getCustomer(String id) {
        System.out.println("----invoking getCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);
        return c;
    }

    public Response updateCustomer(Customer customer) {
        System.out.println("----invoking updateCustomer, Customer name is: " + customer.getName());
        Customer c = customers.get(customer.getId());
        Response r;
        if (c != null) {
            customers.put(customer.getId(), customer);
            r = Response.ok().build();
        } else {
            r = Response.notModified().build();
        }

        return r;
    }

    public Response addCustomer(Customer customer) {
        System.out.println("----invoking addCustomer, Customer name is: " + customer.getName());
        customer.setId(++currentId);

        customers.put(customer.getId(), customer);

        return Response.ok(customer).build();
    }

    public Response deleteCustomer(String id) {
        System.out.println("----invoking deleteCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);

        Response r;
        if (c != null) {
            r = Response.ok().build();
            customers.remove(idNumber);
        } else {
            r = Response.notModified().build();
        }

        return r;
    }

    final void init() {
        Customer c = new Customer();
        c.setName("John");
        c.setId(123);
        customers.put(c.getId(), c);
    }

}
