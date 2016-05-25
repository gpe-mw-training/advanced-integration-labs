package org.jboss.fuse.persistence;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jboss.fuse.persistence.model.SendEmail;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public abstract class AbstractJpaTest extends CamelTestSupport {

    protected ApplicationContext applicationContext;
    protected TransactionTemplate transactionTemplate;
    protected EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        EntityManagerFactory entityManagerFactory = applicationContext
                .getBean("entityManagerFactory", EntityManagerFactory.class);
        transactionTemplate = applicationContext.getBean("transactionTemplate", TransactionTemplate.class);
        entityManager = entityManagerFactory.createEntityManager();
        cleanupRepository();
    }

    @After public void tearDown() throws Exception {
        super.tearDown();
        if (entityManager != null) {
            entityManager.close();
        }
    }

    @Override protected CamelContext createCamelContext() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext(routeXml());
        return SpringCamelContext.springCamelContext(applicationContext);
    }

    protected void cleanupRepository() {
        transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus arg0) {
                entityManager.joinTransaction();
                List<?> list = entityManager.createQuery(selectAllString()).getResultList();
                for (Object item : list) {
                    entityManager.remove(item);
                }
                entityManager.flush();
                return Boolean.TRUE;
            }
        });
    }

/*    protected void assertEntityInDB(int size) throws Exception {
        List<?> list = entityManager.createQuery(selectAllString()).getResultList();
        assertEquals(size, list.size());

        assertIsInstanceOf(SendEmail.class, list.get(0));
    }*/

    protected abstract String routeXml();

    protected abstract String selectAllString();
}

