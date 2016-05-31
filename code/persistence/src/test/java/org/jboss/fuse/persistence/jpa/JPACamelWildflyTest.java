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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
        // Location of the Datasource definition file
        archive.addAsManifestResource("org/jboss/fuse/persistence/jpa/jbossas-ds.xml","jbossas-ds.xml");
        archive.addAsManifestResource("org/jboss/fuse/persistence/jpa/persistence-jpa.xml", "persistence.xml");
        // Turn on our project into a CDI IoC one
        archive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return archive;
    }

    @Before
    public void setUp() throws Exception {
        // Insert some test data
        utx.begin();
        em.joinTransaction();
        em.persist(new Account(1, 750));
        em.persist(new Account(2, 300));
        utx.commit();
        em.clear();
    }

    @After
    public void tearDown() throws Exception {
        // Clean up test data
        utx.begin();
        em.joinTransaction();
        em.createQuery("delete from Account").executeUpdate();
        utx.commit();
    }

    @Test
    public void testJpaInsertCamelRoute() throws Exception {

        CamelContext camelctx = contextRegistry.getCamelContext("route-cdi-context");
        Assert.assertNotNull("Expected Route CDI context to not be null", camelctx);

        // Persist a new account entity
        Account account = new Account(3, 800);
        camelctx.createProducerTemplate().sendBody("direct:start", account);

        Account result = em.getReference(Account.class, 3);

        Assert.assertEquals(account, result);
    }

}

