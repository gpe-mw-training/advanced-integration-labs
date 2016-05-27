package org.jboss.fuse.transaction.route;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SqlUtil {

    private static final Logger log = LoggerFactory.getLogger(SqlUtil.class);

    private Connection conn;
    private Statement stmt;

    public void init() {
        conn = null;
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection("jdbc:derby:memory:reportdb");
            stmt = conn.createStatement();
        } catch (Exception e) {
            log.error("Something happened");
        }
    }

    public void close() throws SQLException {
        conn.close();
    }

    public String checkRecord(@Header("query") String query) throws SQLException {
        ResultSet rs = stmt.executeQuery(query);
        StringBuffer result = new StringBuffer();
        while(rs.next()) {
            result.append(rs.getString("INCIDENT_ID") + ", ");
            result.append(rs.getString("GIVEN_NAME") + ", ");
            result.append(rs.getString("FAMILY_NAME") + ", ");
            result.append(rs.getString("EMAIL") + ", ");
            result.append(rs.getString("PHONE") + ", ");
            result.append(rs.getString("INCIDENT_REF") + ", ");
            result.append(rs.getString("DETAILS") + ", ");
            result.append(rs.getString("SUMMARY"));
        }
        return result.toString();
    }
}
