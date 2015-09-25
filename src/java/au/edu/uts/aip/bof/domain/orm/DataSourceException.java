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
package au.edu.uts.aip.bof.domain.orm;

/**
 * A general purpose exception class to encapsulate errors caused in the 
 * underlying persistence mechanism. Typically this will be a simple wrapper
 * around an SQLException.
 */
public class DataSourceException extends Exception {
    
    public DataSourceException(String message) {
        super(message);
    }
    
    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DataSourceException(Throwable cause) {
        super(cause);
    }
    
}
