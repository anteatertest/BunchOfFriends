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
import java.sql.*;
import java.util.*;
import javax.ejb.*;

/**
 * A Data Access Object for creating, querying and retrieving posts / messages
 * created by users.
 */
@Stateless
public class PostDao extends GenericIdDao<Post> {

    /**
     * The meta-data defining the underlying SQL table and getters/setters for
     * properties of Post records.
     * <p>
     * create table bof_post(
     *   id integer primary key not null generated always as identity,
     *   creator integer foreign key references bof_user(id),
     *   message varchar(8196),
     *   creationDate timestamp,
     *   likes integer
     * );
     */
    private final Column[] columns = new Column[] {
        new ForeignKeyColumn<Post,User>("creator", User.class) {
            @Override
            public User getFK(Post record) {
                return record.getCreator();
            }
            @Override
            public void setFK(Post record, User value) {
                record.setCreator(value);
            }
        },
        new Column<Post,String>("message", String.class) {
            @Override
            public String get(Post record) {
                return record.getMessage();
            }
            @Override
            public void set(Post record, String value) {
                record.setMessage(value);
            }
        },
        new Column<Post,Timestamp>("creationDate", Timestamp.class) {
            @Override
            public Timestamp get(Post record) {
                return record.getDate();
            }
            @Override
            public void set(Post record, Timestamp value) {
                record.setDate(value);
            }
        },
        new Column<Post,Integer>("likes", Integer.class) {
            @Override
            public Integer get(Post record) {
                return record.getLikes();
            }
            @Override
            public void set(Post record, Integer value) {
                record.setLikes(value);
            }
        }
    };
    
    public PostDao() {
        super(Post.class);
    }
    
    @Override
    public String getTable() {
        return "bof_post";
    }

    @Override
    public Column[] getColumns() {
        return columns;
    }
    
    /**
     * Retrieve a list of all Posts by the supplied user and his/her friends.
     * @param me the to query related posts
     * @return all posts that the supplied user should see, sorted by date descending
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<Post> findFriendMessages(User me) throws DataSourceException {
        return findAll("creator in (select friendTo from bof_friend where friendFrom = " + me.getId() + " union all select " + me.getId() + " from bof_dual) order by creationDate desc");
    }

    /**
     * Retrieve a list of all Posts by the supplied user only.
     * @param me the to query related posts
     * @return all posts by the supplied, sorted by date descending
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<Post> findUserMessages(User me) throws DataSourceException {
        return findAll("creator = " + me.getId() + " order by creationDate desc");
    }

    /**
     * Increment the number of posts by one.
     * @param postId the unique identifier of the post whose like-count should be incremented
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void like(int postId) throws DataSourceException {
        // Relying on container managed transactions and also assuming that the
        // database isolation level is "serializable"
        
        // Without isolation a like could be forgotten due to a race condition
        // If the database only creates a read lock here, then it is possible to
        // end up with deadlock
        Post post = find(postId, true); // get a write like
        post.setLikes(post.getLikes() + 1);
        update(post);
    }
    
}
