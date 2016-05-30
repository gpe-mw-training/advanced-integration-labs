package org.jboss.fuse.persistence.jpa.routing;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer {
    @Produces
    @PersistenceContext(unitName = "account")
    private EntityManager em;
}
