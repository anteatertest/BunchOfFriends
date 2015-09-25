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
 * User is a user of the social network.
 * This class is an "entity" in the object relational mapping system.
 */
public class User implements Id, Serializable {

    private int id;
    private String username;
    private String password;
    private String fullName;
    
    public User() {
        // do nothing -- default constructor
    }
    
    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    /**
     * The id property uniquely identifies a record (i.e., a user).
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
     * The username property is used during login but not otherwise shown.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The password property is used during login
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The fullName property is the name shown within the application and user
     * interface (i.e., your name, your friends name, and so on).
     * @return the full name of the user
     */
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", fullName=" + fullName + '}';
    }
    
}
