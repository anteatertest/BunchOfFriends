/*
 * WARNING!
 *
 * This project is intentionally insecure.
 *
 * DO NOT use in production.
 *
 * It is designed for educational purposes - to teach common vulnerabilities in
 * web applications.
 */
package au.edu.uts.aip.bof.domain.config;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;
import javax.sql.*;

/**
 * A CDI producer to generate JDBC DataSources.
 * By default, uses the "jdbc/aip" JNDI resource as mapped in the sun-web.xml file.
 */
@ApplicationScoped
public class DataSourceProducer {

    @Produces
    @Resource(name = "jdbc/bof")
    private DataSource dataSource;
    
}
