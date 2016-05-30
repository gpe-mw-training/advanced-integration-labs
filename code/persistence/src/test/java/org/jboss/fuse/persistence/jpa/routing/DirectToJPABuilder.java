package org.jboss.fuse.persistence.jpa.routing;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jpa.JpaComponent;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Startup
@ContextName(value = "route-cdi-context")
public class DirectToJPABuilder extends RouteBuilder {

    @PersistenceContext(unitName = "account")
    EntityManager em;

    @Inject
    JtaTransactionManager transactionManager;

    @Override
    public void configure() throws Exception {

        // Configure JPA component
        JpaComponent jpaComponent = new JpaComponent();
        jpaComponent.setEntityManagerFactory(em.getEntityManagerFactory());
        jpaComponent.setTransactionManager(transactionManager);
        getContext().addComponent("jpa", jpaComponent);

        from("direct:start")
            .to("jpa:org.jboss.fuse.persistence.jpa.model.Account");


    }
}
