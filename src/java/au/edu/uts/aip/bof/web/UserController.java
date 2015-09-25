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
package au.edu.uts.aip.bof.web;

import au.edu.uts.aip.bof.domain.orm.DataSourceException;
import au.edu.uts.aip.bof.domain.User;
import au.edu.uts.aip.bof.domain.UserDao;
import java.io.*;
import javax.ejb.*;
import javax.enterprise.context.*;
import javax.faces.application.*;
import javax.faces.context.*;
import javax.inject.*;

/**
 * JavaServer Faces controller for creating users, logging in and logging out.
 * This SessionScoped bean is also used to find the currently logged in user.
 */
@Named
@SessionScoped
public class UserController implements Serializable {
    
    @EJB
    private UserDao userDao;
    
    private User user;
    private String username;
    private String password;
    private String fullName;

    //--------------------------------------------------------------------------
    // Properties used by Facelets views
    //--------------------------------------------------------------------------
    
    /**
     * The read-only property "user" is the currently logged in user.
     * If there is no logged in user, then this method returns null.
     * @return the currently logged-in user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * The read/write property "username" is used by the JavaServer Faces view 
     * for logging in (and also creating new accounts).
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The read/write property "password" is used by the JavaServer Faces view 
     * for logging in (and also creating new accounts).
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This read/write property "full name" is used by the JavaServer Faces 
     * view when creating new accounts (the fullName property of the User class
     * is used to store the name shown throughout the user interface).
     * @return the username
     */
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    //--------------------------------------------------------------------------
    // Actions used by JavaServer Faces
    //--------------------------------------------------------------------------

    /**
     * Attempts to log in with the current username and password.
     * @return the "home" view if login succeeds, otherwise adds an error message
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public String login() throws DataSourceException {
        user = userDao.getLogin(username, password);
        if (user != null) {
            username = null;
            password = null;
            return "home?faces-redirect=true";
        } else {
            password = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid username or password"));
            return null;
        }
    }
    
    /**
     * Causes the current user (if any) to be logged out.
     */
    public void logout() {
        user = null;
    }
    
    /**
     * Create a new account using the supplied username, password and full name.
     * @return the view to report a successful signup.
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public String signup() throws DataSourceException {
        User newUser = new User(username, password, fullName);
        userDao.create(newUser);
        
        // keep username but clear the rest
        password = null;
        fullName = null;
        
        return "signup_success";
    }
    
}
