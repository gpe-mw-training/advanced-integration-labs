package org.jboss.fuse.persistence.jpa.routing;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.sql.DataSource;

public class DatasourceProducer {

    @Resource(name="java:jboss/datasources/AccountDS")
    DataSource dataSource;

    @Produces
    @Named("accountDS")
    public DataSource getDataSource() {
        return dataSource;
    }
}
