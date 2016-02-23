package org.biddingengine.core;

import java.util.concurrent.ConcurrentHashMap;

import org.biddingengine.datamodel.User;

/**
 * Service to perform all the user related operations 
 * i.e. addition and removal of users i.e. bidders and sellers
 * @author piyush
 *
 */
public class UserService {

	private ConcurrentHashMap<String, User> userMap;
	
	public UserService(ConcurrentHashMap<String, User> userMap){
		this.userMap = userMap;
	}
	
	/**
	 * 
	 * @param userID userID of the user
	 * @param userName username of the user
	 * @return userID
	 */
	public String registerUser(String userID, String userName) {
		
		if(!userMap.containsKey(userID)){
			User user = new User(userID, userName);
			userMap.put(userID, user);
		}
		return userID;
	}

	/**
	 * 
	 * @param userID userId of the user to be removed
	 * @return true if user exists and false if it doesn't
	 */
	public boolean unregisterUser(String userID) {
		if(userMap.containsKey(userID)){
			userMap.remove(userID);
			return true;
		}
		return false;			
	}
	
	/**
	 * 
	 * @param userID userId of the user to be checked if registered
	 * @return true if registered and false if it doesn't
	 */
	public boolean isRegistered(String userID){
		if(!userMap.containsKey(userID))
			return false;
		else
			return true;
	}
	
	/**
	 * 
	 * @param userID userId of the user for which the User object is to be fetched
	 * @return User object
	 */
	public User getUser(String userID) {
		
		if(userMap.containsKey(userID)){
			return userMap.get(userID);
		}
		else
			return null;
	}
}
