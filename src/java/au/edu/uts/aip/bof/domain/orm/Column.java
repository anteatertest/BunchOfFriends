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
 * Defines column metadata for an object relational mapping system.
 * A Column identifies an underlying SQL column and provides a mechanism to 
 * set the corresponding property of a Java object.
 * <p>
 * The "name" property of Column identifies the underlying SQL column.
 * The get/set methods are used to set a value to the corresponding Java object
 * of the record type.
 * <p>
 * JPA is not being used because this application is intentionally insecure.
 * <p>
 * Note that an "id" column is always assumed to be present so it does not need 
 * to be defined in the getColumns method of a DAO.
 * @param <T> the type of the underlying record that the column is associated with
 * @param <D> the type of the values that the column can hold
 */
public abstract class Column<T, D> {

    private final String name;
    private final Class<? extends D> type;
    
    public Column(String name, Class<? extends D> type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * The name of the column in the underlying SQL table.
     * @return the SQL column name
     */
    public String getName() {
        return name;
    }
    
    /**
     * The Java type of the data that the column or record property may store.
     * @return the type of the values that can be passed to set/get
     */
    public Class<? extends D> getType() {
        return type;
    }
    
    /**
     * Given an object record (i.e., a Java class), retrieve the value 
     * corresponding to this column.
     * The object relational mapping tool will get the value using this method
     * and then save it in the database column given by getName().
     * @param record the record to read from
     * @return the value corresponding to the column
     */
    public abstract D get(T record);
    
    /**
     * Given an object record (i.e., a Java class), sets the value 
     * corresponding to this column.
     * The object relational mapping tool will retrieve the value of the column
     * given by getName() and then set it to the supplied record using this 
     * method.
     * @param record the record to update
     * @param value the value to save in the "column" of the record
     */
    public abstract void set(T record, D value);
    
}
