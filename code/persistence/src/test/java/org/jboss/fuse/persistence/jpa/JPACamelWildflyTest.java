package org.jboss.fuse.persistence.jpa;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jpa.JpaComponent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.fuse.persistence.jpa.model.Account;
import org.jboss.fuse.persistence.jpa.routing.DirectToJPABuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extension.camel.CamelAware;
import org.wildfly.extension.camel.CamelContextRegistry;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

@RunWith(Arquillian.class)
@CamelAware
public class JPACamelWildflyTest {

    @ArquillianResource
    CamelContextRegistry contextRegistry;

    @PersistenceContext
    EntityManager em;

    @Resource(mappedName = "java:jboss/UserTransaction")
    private UserTransaction utx;

    @Deployment
    public static JavaArchive deployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "camel-jpa-test.jar");
        archive.addClass(Account.class);
        archive.addPackage(DirectToJPABuilder.class.getPackage());
        archive.addAsManifestResource("org/jboss/fuse/persistence/jpa/persistence-jpa.xml", "persistence.xml");
        archive.addAsManifestResource("org/jboss/fuse/persistence/jpa/jbossas-ds.xml");
        // Turn our project into CDI
        archive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return archive;
    }

    @Test
    public void testJpaCamelRoute() throws Exception {

        CamelContext camelctx = contextRegistry.getCamelContext("route-cdi-context");
        Assert.assertNotNull("Expected Route CDI context to not be null", camelctx);

        // Persist a new account entity
        Account account = new Account(1, 500);
        camelctx.createProducerTemplate().sendBody("direct:start", account);

        Account result = em.getReference(Account.class, 1);

        Assert.assertEquals(account, result);
    }

}

