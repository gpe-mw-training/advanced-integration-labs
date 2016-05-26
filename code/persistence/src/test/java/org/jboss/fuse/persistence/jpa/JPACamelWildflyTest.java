package org.jboss.fuse.persistence.jpa;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jpa.JpaComponent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.fuse.persistence.jpa.model.Account;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelContextRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@RunWith(Arquillian.class)
public class JPACamelWildflyTest {

    @ArquillianResource
    CamelContextRegistry contextRegistry;

    @Deployment
    public static JavaArchive deployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "camel-jpa-tests.jar");
        archive.addClass(Account.class);
        archive.addAsResource("org/jboss/fuse/persistence/jpa/persistence-local.xml", "META-INF/persistence.xml");
        archive.addAsResource("org/jboss/fuse/persistence/jpa/jpa-camel-context.xml", "META-INF/jboss-camel-context.xml");
        return archive;
    }

    @Test
    public void testJpaCamelRoute() throws Exception {

        CamelContext camelctx = contextRegistry.getCamelContext("jpa-context");
        Assert.assertNotNull("Expected jpa-context to not be null", camelctx);

        // Persist a new account entity
        Account account = new Account(1, 500);
        camelctx.createProducerTemplate().sendBody("direct:start", account);

        JpaComponent component = camelctx.getComponent("jpa", JpaComponent.class);
        EntityManagerFactory entityManagerFactory = component.getEntityManagerFactory();

        // Read the saved entity back from the database
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Account result = em.getReference(Account.class, 1);
        em.getTransaction().commit();

        Assert.assertEquals(account, result);
    }

}

