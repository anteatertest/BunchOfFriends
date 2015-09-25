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
 * This interface is used to denote an entity with a unique primary key.
 * The interface is analogous to using JPA's @Entity annotation and @Id on an 
 * integer field/property. JPA is not being used because this application is 
 * intentionally insecure.
 * <p>
 * If getId() returns zero, this is taken to mean that the record has not yet
 * been persisted.
 */
public interface Id {
    
    /**
     * Get the unique identifier for this record. Used as a primary key
     * in the underlying database. The default value, zero, should be used
     * for records that have not yet been persisted.
     * @return the unique identifier
     */
    public int getId();
    
    /**
     * Set the unique identifier for this record.
     * @param id the unique identifier
     */
    public void setId(int id);
    
}
