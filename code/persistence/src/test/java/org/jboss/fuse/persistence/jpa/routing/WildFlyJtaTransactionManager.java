package org.jboss.fuse.persistence.jpa.routing;

import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Named("transactionManager")
@SuppressWarnings("serial")
public class WildFlyJtaTransactionManager extends JtaTransactionManager {

    @Resource(mappedName = "java:/TransactionManager")
    private TransactionManager transactionManager;

    @Resource(mappedName = "java:jboss/UserTransaction")
    private UserTransaction userTransaction;

    @PostConstruct
    public void initTransactionManager() {
        setTransactionManager(transactionManager);
        setUserTransaction(userTransaction);
    }
}
