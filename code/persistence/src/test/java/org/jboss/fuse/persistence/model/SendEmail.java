package org.jboss.fuse.persistence.model;

import org.apache.camel.component.jpa.PreConsumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a task which is added to the database, then removed from the database when it is consumed
 *
 * @version 
 */
@Entity
public class SendEmail {
    private static final Logger LOG = LoggerFactory.getLogger(SendEmail.class);
    private Long id;
    private String address;

    public SendEmail() {
    }

    public SendEmail(String address) {
        setAddress(address);
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @PreConsumed
    public void doBefore() {
        LOG.info("Invoked the pre consumed method with address {}", getAddress());
        if ("dummy".equals(getAddress())) {
            setAddress("dummy@somewhere.org");
        }
    }

    @Override
    public String toString() {
        // OpenJPA warns about fields being accessed directly in methods if NOT using the corresponding getters.
        return "SendEmail[id: " + getId() + ", address: " + getAddress() + "]";
    }

}
