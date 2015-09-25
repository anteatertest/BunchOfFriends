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
import au.edu.uts.aip.bof.domain.Post;
import au.edu.uts.aip.bof.domain.User;
import au.edu.uts.aip.bof.domain.UserDao;
import au.edu.uts.aip.bof.domain.PostDao;
import java.sql.*;
import java.util.*;
import javax.annotation.*;
import javax.ejb.*;
import javax.enterprise.context.*;
import javax.inject.*;

/**
 * JavaServer Faces controller for reading, creating and liking posts (messages).
 */
@Named
@RequestScoped
public class PostController {
    
    @EJB
    private PostDao postDao;
    
    @EJB
    private UserDao userDao;
    
    @Inject
    private UserController userController;
    
    private String message;
    
    /**
     * The currently chosen user.
     * This controller will retrieve the posts for this user.
     * By <i>default</i> this is the currently logged in user.
     * However, this can also be another chosen user (e.g., it will be changed
     * when viewing the posts of a different user).
     */
    private User user;
    
    private List<Post> posts;
    private List<Post> userPosts;
    
    //--------------------------------------------------------------------------
    // Initialization
    //--------------------------------------------------------------------------
    
    /**
     * By default, this controller will view the posts of the currently logged
     * in user.
     */
    @PostConstruct
    public void init() {
        user = userController.getUser();
    }
    
    /**
     * Choose to view the posts of another user (i.e., a friend).
     * @param id the user id of a friend
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void loadUser(int id) throws DataSourceException {
        user = userDao.find(id);
    }
    
    //--------------------------------------------------------------------------
    // Properties used by Facelets views
    //--------------------------------------------------------------------------
    
    /**
     * Get the currently chosen user (or the default, being the currently logged
     * in user).
     * @return the currently chosen user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * The read/write property "message" is used by the JavaServer Faces view 
     * for creating new posts.
     * @return the current value of the "message" property
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Retrieve a list of all posts by the currently chosen user (i.e., the
     * user's posting history).
     * @return posts by the currently chosen user
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<Post> getUserPosts() throws DataSourceException {
        // Values are cached
        if (userPosts == null) {
            userPosts = postDao.findUserMessages(user);
        }
        return userPosts;
    }
    
    /**
     * Retrieve a list of all posts by the currently chosen user and his/her
     * friends (i.e., the user's "wall").
     * @return posts that the currently chosen user should see
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<Post> getPosts() throws DataSourceException {
        // Values are cached
        if (posts == null) {
            posts =  postDao.findFriendMessages(user);
            for (Post post : posts) {
                post.setCreator(userDao.find(post.getCreator().getId()));
            }
        }
        return posts;
    }
    
    //--------------------------------------------------------------------------
    // Actions used by JavaServer Faces
    //--------------------------------------------------------------------------
    
    /**
     * Create a new post, using the content of the "message" property as the 
     * body of the post.
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public void post() throws DataSourceException {
        Post post = new Post(user, message, new Timestamp(System.currentTimeMillis()), 0);
        postDao.create(post);
        
        // If the post list has been cached, update the caches
        if (posts != null)
            posts.add(0, post);
        if (userPosts != null)
            userPosts.add(0, post);
    }
    
    /**
     * Record a "like" of a particular post.
     * @param postId the post to like
     * @param back the view to show after completing the post action
     * @param friend the optional friend parameter to pass to the next view
     * @return the JavaServer Faces view to show (back)
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public String like(int postId, String back, String friend) throws DataSourceException {
        postDao.like(postId);
        posts = null;
        userPosts = null;
        String outcome = back + "?faces-redirect=true";
        if (friend != null && friend.length() > 0)
            outcome = outcome + "&friend=" + friend;
        return outcome;
    }
    
}
