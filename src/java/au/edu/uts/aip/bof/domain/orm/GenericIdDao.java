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

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.inject.*;
import javax.sql.*;

/**
 * A generic Data Access Object (DAO). It performs CRUD operations on an 
 * underlying record/row type. This is similar to the EntityManager in JPA.
 * JPA is not being used because this application is intentionally insecure.
 * <p>
 * Use this abstract class by creating a subclass. Implement the getTable() 
 * method (which should return the name of the underlying SQL table) and the 
 * getColumns() method (which should return an array of Column objects that
 * provide meta-data about the underlying table).
 * <p>
 * This class is intentionally insecure. Do not use in production code.
 * @param <T> the type of records that the DAO stores/retrieves
 */
public abstract class GenericIdDao<T extends Id> implements Serializable {
    
    private static Logger logger = Logger.getLogger("au.edu.uts.aip.bof.domain.orm");
    
    @Inject
    private DataSource dataSource;
    
    private final Class<? extends T> rowType;
    
    /**
     * The name of the underlying SQL table.
     * @return the SQL table name
     */
    public abstract String getTable();
    
    /**
     * Meta-data about the underlying SQL table -- the underlying columns.
     * Note that an "id" column is always assumed: it should not be in the 
     * meta-data.
     * @return meta-data about the underlying SQL table
     */
    public abstract Column[] getColumns();
    
    /**
     * Constructor. Requires the class of the rows/records because Java Generics
     * are not necessarily available at runtime (type erasure).
     * @param rowType the Java type used to represent records of the table
     */
    public GenericIdDao(Class<? extends T> rowType) {
        this.rowType = rowType;
    }
    
    /**
     * Generates a list of comma-separated column names: the column list
     * of a select statement (e.g., select a, b, c from d --> "a, b, c").
     * @return a list of column names suitable for use in an SQL statement
     */
    private String getColumnString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Column column : getColumns()) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(column.getName());
        }
        return sb.toString();
    }
    
    /**
     * Read a single row from a JDBC ResultSet.
     * The current cursor of the ResultSet is read into a new object representing
     * a row/record of the database.
     * @param rs the ResultSet to read from
     * @return a new instance of the underlying record/row type, configured with 
     *           the column values
     * @throws SQLException 
     */
    private T read(ResultSet rs) throws SQLException {
        try {
            T row = rowType.newInstance();
            row.setId(rs.getInt("id"));
            for (Column column : getColumns()) {
                column.set(row, rs.getObject(column.getName(), column.getType()));
            }
            return row;
        } catch (InstantiationException | IllegalAccessException e) {
            // This should never occur!
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create an SQL "insert into" statement.
     * @param record the record to save to the database
     * @return an SQL string to execute
     */
    private String createInsertStatement(T record) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getTable())
          .append("(").append(getColumnString()).append(") ")
          .append("values (");
        
        boolean first = true;
        for (Column c : getColumns()) {
            if (first)
                first = false;
            else
                sb.append(", ");
            if (!Number.class.isAssignableFrom(c.getType()))
                sb.append("'");
            sb.append(String.valueOf(c.get(record)));
            if (!Number.class.isAssignableFrom(c.getType()))
                sb.append("'");
        }
        
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * Create an SQL "update __ set __" statement to update the supplied 
     * record. The id is used to identify the database row to update and all
     * values of the record are updated even if they have not changed.
     * @param record the record to update
     * @return the SQL string to execute
     */
    private String createUpdateStatement(T record) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getTable())
          .append(" set ");
        
        boolean first = true;
        for (Column c : getColumns()) {
            if (first)
                first = false;
            else
                sb.append(", ");
            sb.append(c.getName()).append(" = ");
            if (!Number.class.isAssignableFrom(c.getType()))
                sb.append("'");
            sb.append(String.valueOf(c.get(record)));
            if (!Number.class.isAssignableFrom(c.getType()))
                sb.append("'");
        }
        
        sb.append(" where id = ").append(record.getId());
        return sb.toString();
    }
    
    /**
     * Find the record that matches the supplied unique identifier.
     * If no record is found, return null.
     * If the writeLock parameter is true, attempt to obtain a write lock on the
     * database row using a "select __ for update" query.
     * @param id the primary key of the underlying database table
     * @param writeLock true if the query should be executed "for update"
     * @return a record corresponding to the unique identifier, null if none is found
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public T find(int id, boolean writeLock) throws DataSourceException {
        String query = "select id, " + getColumnString() + " from " + getTable() + " where id = " + id;
        
        // Obtain a write lock -- this works under Derby and apparently also PostgreSQL
        if (writeLock)
            query += " for update";
        
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next())
                return read(rs);
            else
                return null;
            
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    /**
     * Find the record that matches the supplied unique identifier.
     * If no record is found, return null.
     * This method does not attempt to acquire a write lock -- thus it will 
     * likely only obtain a read lock (that is, assuming, the database is 
     * running with high transaction isolation).
     * @param id the primary key of the underlying database table
     * @return a record corresponding to the unique identifier, null if none is found
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public T find(int id) throws DataSourceException {
        return find(id, false);
    }
    
    /**
     * Retrieves all rows in the database table, converting them into records.
     * @return a list of records corresponding to the rows of the table
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<T> findAll() throws DataSourceException {
        String query = "select id, " + getColumnString() + " from " + getTable();
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ArrayList<T> result = new ArrayList<>();
            while (rs.next())
                result.add(read(rs));
            return result;
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    /**
     * Retrieves a record corresponding to the the first row in the table that 
     * matches the supplied SQL where clause. 
     * Returns null, if there is no such row.
     * @param constraint the condition (e.g., "a = 1") to use in an SQL where clause
     * @return the first row that matches the condition, null if there are none
     * @throws DataSourceException if there was an error in the underlying data source
     */
    protected T findFirst(String constraint) throws DataSourceException {
        String query = "select id, " + getColumnString() + " from " + getTable() + " where " + constraint;
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ArrayList<T> result = new ArrayList<>();
            if (rs.next())
                return read(rs);
            else
                return null;
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    /**
     * Retrieves all rows in the database table that match the supplied SQL
     * where clause, converting them into records.
     * @param constraint the condition (e.g., "a = 1") to use in an SQL where clause
     * @return a list of records corresponding to the matching rows of the table
     * @throws DataSourceException if there was an error in the underlying data source
     */
    protected List<T> findAll(String constraint) throws DataSourceException {
        String query = "select id, " + getColumnString() + " from " + getTable() + " where " + constraint;
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ArrayList<T> result = new ArrayList<>();
            while (rs.next())
                result.add(read(rs));
            return result;
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    /**
     * Persists a record to the database. This method will also update the
     * supplied record with the unique identifier generated during the creation.
     * @param record the record to persist
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void create(T record) throws DataSourceException {
        // Assuming that 
        String query = createInsertStatement(record);
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                
                // now make sure to set the id of the record
                if (rs.next())
                    record.setId(rs.getInt(1));
                
            }

        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
        
    }
    
    /**
     * Persists any changes made to the supplied record back into the database.
     * The id of the supplied record is used to identify the database row to 
     * update. All values of the record are updated even if they have not changed.
     * @param record the record to update in the database
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void update(T record) throws DataSourceException {
        String query = createUpdateStatement(record);
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
        
    }
    
    /**
     * Deletes the supplied record from the underlying database. The deletion
     * is performed only on the basis of the primary key (it will still delete
     * a row based on the unique identifier, even if other parts of the record 
     * have changed).
     * @param record the record to delete
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void delete(T record) throws DataSourceException {
        String query = "delete from " + getTable() + " where id = " + record.getId();
        logger.log(Level.INFO, query);
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }
        
    }
    
}
