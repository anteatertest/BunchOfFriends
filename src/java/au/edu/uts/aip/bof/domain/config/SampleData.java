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

import au.edu.uts.aip.bof.domain.orm.DataSourceException;
import au.edu.uts.aip.bof.domain.Friend;
import au.edu.uts.aip.bof.domain.FriendDao;
import au.edu.uts.aip.bof.domain.Post;
import au.edu.uts.aip.bof.domain.PostDao;
import au.edu.uts.aip.bof.domain.User;
import au.edu.uts.aip.bof.domain.UserDao;
import java.sql.*;
import java.util.logging.*;
import javax.annotation.*;
import javax.ejb.*;
import javax.ejb.Singleton;
import javax.inject.*;
import javax.sql.*;

/**
 * Configure the database on deployment by: dropping any existing tables, 
 * creating new tables.
 * This Singleton bean is configured to run on start up.
 */
@Singleton
@Startup
public class SampleData {
    
    @Inject
    private DataSource ds;
    
    @EJB
    private UserDao userDao;
    
    @EJB
    private FriendDao friendDao;
    
    @EJB
    private PostDao postDao;
    
    /**
     * Drop and then populate the database with sample data.
     */
    @PostConstruct
    public void init()  {
        Logger logger = Logger.getLogger("au.edu.uts.aip.bof.domain.config");
        
        // DDL to drop all existing tables
        String drop = 
            "drop table bof_dual;" +
            "drop table bof_post;" +
            "drop table bof_friend;" +
            "drop table bof_user;";
        
        String schemaDual = 
            "create table bof_dual(\n" +
            "    x integer\n" +
            ");\n" +
            "\n" +
            "insert into bof_dual values (1);";
        
        String autoGenPlaceholder = "<AUTOGEN>";
        String derbyAutoGen = "generated always as identity";
        String mySqlAutoGen = "auto_increment";
        
        // DDL for complete database schema
        String schema = 
            "create table bof_user(\n" +
            "    id integer primary key not null <AUTOGEN>,\n" +
            "    username varchar(255),\n" +
            "    password varchar(255),\n" +
            "    fullName varchar(255)\n" +
            ");\n" +
            "\n" +
            "create table bof_friend(\n" +
            "    id integer primary key not null <AUTOGEN>,\n" +
            "    friendFrom integer,\n" +
            "    friendTo integer,\n" +
            "    constraint friendFrom_fk foreign key (friendFrom) references bof_user(id),\n" +
            "    constraint friendTo_fk foreign key (friendTo) references bof_user(id)\n" +
            ");\n" +
            "\n" +
            "create table bof_post(\n" +
            "    id integer primary key not null <AUTOGEN>,\n" +
            "    creator integer,\n" +
            "    message varchar(8196),\n" +
            "    creationDate timestamp,\n" +
            "    likes integer,\n" +
            "    constraint creator_fk foreign key (creator) references bof_user(id)\n" +
            ");";

        try (Connection conn = ds.getConnection()) {
            
            // First drop all tables
            try (Statement stmt = conn.createStatement()) {
                execute(stmt, drop);
            } catch (SQLException e) {
                // Ignore any errors during dropping as this is probably because
                // the tables don't exist yet and so can't be dropped.
                logger.log(Level.WARNING, "Tried to drop existing database tables but failed -- this is probably the first time you have run the application");
            }
            
            // Then create dual
            try (Statement stmt = conn.createStatement()) {
                execute(stmt, schemaDual);
            }
            
            // Then create all tables
            try (Statement stmt = conn.createStatement()) {
                execute(stmt, schema.replaceAll(autoGenPlaceholder, derbyAutoGen));
            } catch (SQLException e) {
                // The Derby database schema didn't seem to work
                // Let's try MySQL before giving up
                logger.log(Level.WARNING, "Failed to create database tables using Derby/JavaDB syntax, trying MySQL");
                try (Statement stmt = conn.createStatement()) {
                    execute(stmt, schema.replaceAll(autoGenPlaceholder, mySqlAutoGen));
                }
            }
            
        } catch (SQLException e) {
            // Uh oh! We couldn't initialize the database. There isn't really 
            // any way to recover from this
            // Dump to the default log!
            logger.log(Level.SEVERE, "Could not initialize database", e);
        }
        
        
        try {
            // Finally, use the object-relational system to populate the 
            // database with sample data
            User carol = new User("carol", "password", "Carol");
            User mike = new User("mike", "qwerty", "Mike");
            User alice = new User("alice", "123456", "Alice");
            User sam = new User("sam", "iloveyou", "Sam");
            
            User greg = new User("greg", "bravo", "Greg");
            User peter = new User("peter", "volcano", "Peter");
            User bobby = new User("bobby", "racecar", "Bobby");
            
            User marcia = new User("marcia", "davyjones", "Marcia");
            User jan = new User("jan", "glass", "Jan");
            User cindy = new User("cindy", "thindy", "Cindy");
            
            userDao.create(carol);
            userDao.create(mike);
            userDao.create(alice);
            userDao.create(sam);
            userDao.create(greg);
            userDao.create(peter);
            userDao.create(bobby);
            userDao.create(marcia);
            userDao.create(jan);
            userDao.create(cindy);
            
            friendDao.create(new Friend(marcia, carol));
            friendDao.create(new Friend(marcia, jan));
            friendDao.create(new Friend(jan, marcia));
            friendDao.create(new Friend(jan, alice));
            friendDao.create(new Friend(jan, cindy));
            friendDao.create(new Friend(cindy, jan));
            friendDao.create(new Friend(cindy, mike));
            
            friendDao.create(new Friend(carol, marcia));
            friendDao.create(new Friend(carol, greg));
            friendDao.create(new Friend(carol, alice));
            friendDao.create(new Friend(alice, carol));
            friendDao.create(new Friend(alice, jan));
            friendDao.create(new Friend(alice, peter));
            friendDao.create(new Friend(alice, mike));
            friendDao.create(new Friend(mike, alice));
            friendDao.create(new Friend(mike, cindy));
            friendDao.create(new Friend(mike, bobby));
            
            friendDao.create(new Friend(greg, carol));
            friendDao.create(new Friend(greg, peter));
            friendDao.create(new Friend(peter, greg));
            friendDao.create(new Friend(peter, alice));
            friendDao.create(new Friend(peter, bobby));
            friendDao.create(new Friend(bobby, peter));
            friendDao.create(new Friend(bobby, mike));
            
            friendDao.create(new Friend(alice, sam));
            friendDao.create(new Friend(sam, alice));

            postDao.create(new Post(mike, "It has been a busy week", Timestamp.valueOf("2014-10-2 09:00:00"), 5));
            postDao.create(new Post(mike, "Finished designs for new building.", Timestamp.valueOf("2014-10-4 16:15:00"), 2));
            postDao.create(new Post(mike, "Received Father of the Year award. Wow!", Timestamp.valueOf("2014-10-5 12:33:00"), 5));
            
            postDao.create(new Post(carol, "Enjoying a nightcap with hubby", Timestamp.valueOf("2014-10-5 18:42:00"), 5));
            postDao.create(new Post(carol, "Teaching the boys to dance", Timestamp.valueOf("2014-10-8 4:55:00"), 4));
            
            postDao.create(new Post(alice, "Cleaning, cleaning. Always cleaning.", Timestamp.valueOf("2014-10-1 13:21:00"), 3));
            postDao.create(new Post(alice, "Cooking up a storm in the kitchen", Timestamp.valueOf("2014-10-2 11:49:00"), 2));
            
            postDao.create(new Post(sam, "At the meat market", Timestamp.valueOf("2014-10-5 08:01:00"), 2));
            postDao.create(new Post(sam, "Thinking of buying a new refrigerator", Timestamp.valueOf("2014-10-6 12:05:00"), 1));
            
            postDao.create(new Post(greg, "Bobby is so immature", Timestamp.valueOf("2014-10-2 16:59:00"), 3));
            postDao.create(new Post(greg, "Another school day. :(", Timestamp.valueOf("2014-10-4 07:32:00"), 5));
            postDao.create(new Post(greg, "Listening to the new Jonny Bravo single", Timestamp.valueOf("2014-10-7 17:38:00"), 8));
            
            postDao.create(new Post(peter, "Saved a life today", Timestamp.valueOf("2014-10-3 18:05:00"), 1));
            postDao.create(new Post(peter, "Am I dull?", Timestamp.valueOf("2014-10-3 18:22:00"), 0));
            
            postDao.create(new Post(bobby, "Clairol #43. Ugh!", Timestamp.valueOf("2014-10-5 10:10:00"), 4));
            postDao.create(new Post(bobby, "Feeling afraid of heights", Timestamp.valueOf("2014-10-7 13:52:00"), 0));
            
            postDao.create(new Post(marcia, "I could listen to Davy Jones all night", Timestamp.valueOf("2014-10-7 20:19:00"), 0));
            
            postDao.create(new Post(jan, "Feeling low", Timestamp.valueOf("2014-10-8 09:15:00"), 0));
            
            postDao.create(new Post(cindy, "I have just heard an amazing secret", Timestamp.valueOf("2014-10-5 13:11:00"), 0));
            
        } catch (DataSourceException e) {
            logger.log(Level.SEVERE, "Could not populate database with sample data", e);
        }
    }
    
    /**
     * A helper to execute a multi-statement SQL expression.
     * Note that this is a very naive split. It just looks for semicolons.
     * It does not check whether the semicolon is escaped or quoted.
     * <p>
     * On some databases, it would be better to enable modes such as 
     * allowMultiQueries but it seems that Derby doesn't allow this.
     * @param stmt the JDBC Statement to use to execute the batch
     * @param query the queries, separated by a semicolon, to execute
     * @throws SQLException 
     */
    private void execute(Statement stmt, String query) throws SQLException {
        for (String command : query.split(";")) {
            stmt.addBatch(command);
        }
        stmt.executeBatch();
    }
    
}
