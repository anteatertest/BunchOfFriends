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
package au.edu.uts.aip.bof.domain;

import au.edu.uts.aip.bof.domain.orm.DataSourceException;
import au.edu.uts.aip.bof.domain.orm.Column;
import au.edu.uts.aip.bof.domain.orm.GenericIdDao;
import java.util.*;
import javax.ejb.*;

/**
 * A Data Access Object for creating, querying and retrieving users.
 */
@Stateless
public class UserDao extends GenericIdDao<User> {

    /**
     * The meta-data defining the underlying SQL table and getters/setters for
     * properties of User records.
     * <p>
     * create table bof_user(
     *   id integer primary key not null generated always as identity,
     *   username varchar(255),
     *   password varchar(255),
     *   fullName varchar(255)
     * );
     */
    private final Column[] columns = new Column[] {
        new Column<User,String>("username", String.class) {
            @Override
            public String get(User record) {
                return record.getUsername();
            }
            @Override
            public void set(User record, String value) {
                record.setUsername(value);
            }
        },
        new Column<User,String>("password", String.class) {
            @Override
            public String get(User record) {
                return record.getPassword();
            }
            @Override
            public void set(User record, String value) {
                record.setPassword(value);
            }
        },
        new Column<User,String>("fullName", String.class) {
            @Override
            public String get(User record) {
                return record.getFullName();
            }
            @Override
            public void set(User record, String value) {
                record.setFullName(value);
            }
        }
    };
    
    public UserDao() {
        super(User.class);
    }
    
    @Override
    public String getTable() {
        return "bof_user";
    }

    @Override
    public Column[] getColumns() {
        return columns;
    }
    
    /**
     * Returns the user corresponding to the supplied username and password.
     * If no matching user could be found, or if the password is incorrect, the
     * method returns null.
     * @param username the user-supplied username to check
     * @param password the user-supplied password to check
     * @return the logged in user, otherwise null
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public User getLogin(String username, String password) throws DataSourceException {
        return findFirst("username = '" + username + "' and password = '" + password + "'");
    }
    
    /**
     * Find all friends of the supplied user.
     * @param me the user to locate friends of
     * @return a list of Users who are the friends of the supplied user
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<User> getFriends(User me) throws DataSourceException {
        return findAll("id in (select friendTo from bof_friend where friendFrom = " + me.getId() + ")");
    }
    
    /**
     * Find all users who are NOT friends of the supplied user.
     * This method returns a list of users who a user could befriend.
     * @param me the user looking for potential new friends
     * @return a list of users who are not associated with the supplied user
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<User> getNotFriends(User me) throws DataSourceException {
        return findAll("id not in (select friendTo from bof_friend where friendFrom = " + me.getId() + " union all select " + me.getId() + " from bof_dual)");
    }
    
}
