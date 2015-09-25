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
import au.edu.uts.aip.bof.domain.FriendDao;
import au.edu.uts.aip.bof.domain.User;
import au.edu.uts.aip.bof.domain.UserDao;
import au.edu.uts.aip.bof.domain.Friend;
import java.util.*;
import javax.ejb.*;
import javax.enterprise.context.*;
import javax.inject.*;

/**
 * JavaServer Faces controller for querying and connecting with friends.
 */
@Named
@RequestScoped
public class FriendsController {
    
    @EJB
    private UserDao userDao;
    
    @EJB
    private FriendDao friendDao;
    
    @Inject
    private UserController userController;
    
    //--------------------------------------------------------------------------
    // Properties used by Facelets views
    //--------------------------------------------------------------------------
    
    /**
     * Get a list of friends of the current user.
     * @return friends of the current user
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<User> getMyFriends() throws DataSourceException {
        return userDao.getFriends(userController.getUser());
    }
    
    /**
     * Get a list of users who are not friends of the current user.
     * This is a list of people who the current user could potentially "friend".
     * @return users who are not friends of the current user
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public List<User> getNotFriends() throws DataSourceException {
        return userDao.getNotFriends(userController.getUser());
    }
    
    //--------------------------------------------------------------------------
    // Actions used by JavaServer Faces
    //--------------------------------------------------------------------------
    
    /**
     * Establish a friend relationship.
     * The friend relationship is directional - adding another user as a friend
     * does not automatically make the reverse relationship true (i.e., they 
     * need to also friend you).
     * @param id the other user that this user is friending with
     * @return the next view to use
     * @throws DataSourceException if there was an error in the underlying data source
     */
    public String friend(int id) throws DataSourceException {
        User friend = userDao.find(id);
        friendDao.create(new Friend(userController.getUser(), friend));
        return "friend_list?faces-redirect=true";
    }
    
}
