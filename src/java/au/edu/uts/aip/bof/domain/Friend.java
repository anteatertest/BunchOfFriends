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

import au.edu.uts.aip.bof.domain.orm.Id;
import java.io.*;

/**
 * Friend defines a friendship relationship (i.e., a connection) between two 
 * users.
 * This class is an "entity" in the object relational mapping system.
 * The friend is directed - A can friend B even if B is not a friend of A.
 * The friend relationship requires no confirmation.
 * <p>
 * Perhaps in a future version, this would be extended to allow the typical
 * friend request and confirmation process used in most social networks.
 */
public class Friend implements Id, Serializable {
    
    private int id;
    private User from;
    private User to;

    public Friend() {
        // do nothing -- default constructor
    }
    
    public Friend(User from, User to) {
        this.from = from;
        this.to = to;
    }
    
    /**
     * The id property uniquely identifies a record (i.e., an instance of the
     * friend relationship -- a pairing of two users).
     * @return a unique record identifier
     */
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The from property is the user who is making the friendship.
     * @return the user who "owns" the friendship relationship
     */
    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * The to property is the user who is the friend of the "from" user.
     * @return the user who this relationship is connected to
     */
    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }
    
}
