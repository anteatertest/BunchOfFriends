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
import au.edu.uts.aip.bof.domain.orm.ForeignKeyColumn;
import java.util.*;
import javax.ejb.*;

/**
 * A Data Access Object for creating, querying and retrieving friend 
 * relationships.
 */
@Stateless
public class FriendDao extends GenericIdDao<Friend> {

    /**
     * The meta-data defining the underlying SQL table and getters/setters for
     * properties of Friend records.
     * <p>
     * create table bof_friend (
     *   id integer primary key not null generated always as identity,
     *   friendFrom integer foreign key references bof_user(id),
     *   friendTo integer foreign key references bof_user(id)
     * );
     */
    private final Column[] columns = new Column[] {
        new ForeignKeyColumn<Friend,User>("friendFrom", User.class) {
            @Override
            public User getFK(Friend record) {
                return record.getFrom();
            }
            @Override
            public void setFK(Friend record, User value) {
                record.setFrom(value);
            }
        },
        new ForeignKeyColumn<Friend,User>("friendTo", User.class) {
            @Override
            public User getFK(Friend record) {
                return record.getTo();
            }
            @Override
            public void setFK(Friend record, User value) {
                record.setTo(value);
            }
        },
    };
    
    public FriendDao() {
        super(Friend.class);
    }
    
    @Override
    public String getTable() {
        return "bof_friend";
    }

    @Override
    public Column[] getColumns() {
        return columns;
    }
    
    /**
     * Find all friend relationships "owned" by the supplied user.
     * @param me the user to locate friends of
     * @return a list of friends
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<Friend> getFriends(User me) throws DataSourceException {
        return findAll("friendFrom = " + me.getId());
    }
    
}
