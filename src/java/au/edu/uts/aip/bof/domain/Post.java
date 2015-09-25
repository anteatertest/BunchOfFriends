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
import java.sql.*;

/**
 * A Post is a message posted to the social network by a user.
 * This class is an "entity" in the object relational mapping system.
 * <p>
 * Each post has a like count. Unlike full-featured social networks, the like
 * count does not remember who liked the message or any other details. It is 
 * implemented as a simple integer counter.
 */
public class Post implements Id, Serializable {
    
    private int id;
    private User creator;
    private String message;
    private Timestamp date;
    private int likes;
    
    public Post() {
        // do nothing -- default constructor
    }

    public Post(User creator, String message, Timestamp date, int likes) {
        this.creator = creator;
        this.message = message;
        this.date = date;
        this.likes = likes;
    }

    /**
     * The id property uniquely identifies a record (i.e., a single post).
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
     * The author who created the post
     * @return a user who created the post
     */
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * The body of the post / message.
     * Maximum length is 8196 characters (configured in database).
     * @return the textual content of the message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The date/time that the post was created
     * @return the creation timestamp
     */
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * A simple integer count of the number of times that the post has been
     * "liked" by other users.
     * @return a count of the number of likes
     */
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    
}
