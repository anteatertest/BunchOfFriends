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
 * A specialization of Column to handle foreign key columns. When the 
 * object-relational system encounters a foreign key, it does not do eager 
 * fetching or lazy loading. Instead it just creates an empty "placeholder" 
 * entity to use as the foreign key (the primary of the placeholder is set, but
 * nothing else).
 * @param <T> the type of the underlying record that the column is associated with
 * @param <D> the type of the values that the column can hold
 */
public abstract class ForeignKeyColumn<T,D extends Id> extends Column<T,Integer> {

    private final Class<? extends D> type;
    
    public ForeignKeyColumn(String name, Class<? extends D> type) {
        super(name, Integer.class);
        this.type = type;
    }
    
    /**
     * Translates an underlying foreign key reference into a simple integer
     * unique identifier.
     * @param record the record to read from
     * @return the unique identifier of the foreign key
     */
    @Override
    public Integer get(T record) {
        return getFK(record).getId();
    }
    
    /**
     * Translates an integer unique identifier into a simple "placeholder"
     * record suitable for the underlying foreign key reference.
     * @param record the record
     * @param value the unique identifier of the foreign key
     */
    @Override
    public void set(T record, Integer value) {
        try {
            // Create the placeholder record to use as the foreign key
            D fk = type.newInstance();
            // Set the primary key of the placeholder
            fk.setId(value);
            // Now save it to the underlying record
            setFK(record, fk);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Getter for the foreign key values. This is invoked by the overridden get()
     * method on this class (ForeignKeyColumn).
     * @param record the record to read from
     * @return the object reference for the foreign key
     */
    public abstract D getFK(T record);
    
    /**
     * Setter for the foreign key values. This is invoked by the overridden set()
     * method on this class (ForeignKeyColumn).
     * @param record the record to write to
     * @param value the object reference for the foreign key
     */
    public abstract void setFK(T record, D value);
    
}
