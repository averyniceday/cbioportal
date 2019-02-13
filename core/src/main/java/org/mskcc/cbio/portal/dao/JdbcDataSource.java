package org.mskcc.cbio.portal.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mskcc.cbio.portal.util.DatabaseProperties;
import org.apache.commons.lang.StringUtils;

/**
 * Data source that self-initializes based on cBioPortal configuration.
 */
public class JdbcDataSource extends BasicDataSource {
    public JdbcDataSource () {
        DatabaseProperties dbProperties = DatabaseProperties.getInstance();
        String host = dbProperties.getDbHost();
        String userName = dbProperties.getDbUser();
        String password = dbProperties.getDbPassword();
        String database = dbProperties.getDbName();
        String useSSL = (!StringUtils.isBlank(dbProperties.getDbUseSSL())) ? dbProperties.getDbUseSSL() : "false";
        System.out.println("\n\n\n\n\n\n\n\n >>> HERE I AM"); 
        String url ="jdbc:mysql://" + host + "/" + database +
                        "?useSSL=true&user=" + userName + "&password=" + password +
                        "&zeroDateTimeBehavior=convertToNull;
        //  Set up poolable data source
        this.setConnectionProperties("useSSL=true;useSSL=blegh");
        this.setDriverClassName("com.mysql.jdbc.Driver");
        this.setUsername(userName);
        this.setPassword(password);
        this.setUrl(url);
        //  By pooling/reusing PreparedStatements, we get a major performance gain
        this.setPoolPreparedStatements(true);
        // these are the values cbioportal has been using in their production
        // context.xml files when using jndi
        this.setMaxTotal(500);
        this.setMaxIdle(30);
        this.setMaxWaitMillis(10000);
        this.setMinEvictableIdleTimeMillis(30000);
        this.setTestOnBorrow(true);
        this.setValidationQuery("SELECT 1");
    }
}
