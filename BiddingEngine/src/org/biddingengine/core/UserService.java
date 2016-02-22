package org.biddingengine.core;

import java.util.concurrent.ConcurrentHashMap;

import org.biddingengine.datamodel.User;

public class UserService {

	private ConcurrentHashMap<String, User> userMap;
	
	public UserService(ConcurrentHashMap<String, User> userMap){
		this.userMap = userMap;
	}
	
	public String registerUser(String userID, String userName) {
		
		if(!userMap.containsKey(userID)){
			User user = new User(userID, userName);
			userMap.put(userID, user);
		}
		return userID;
	}

	public boolean unregisterUser(String userID) {
		if(userMap.containsKey(userID)){
			userMap.remove(userID);
			return true;
		}
		return false;			
	}
	
	public boolean isRegistered(String userID){
		if(!userMap.containsKey(userID))
			return false;
		else
			return true;
	}
	
	public User getUser(String userID) {
		
		if(userMap.containsKey(userID)){
			return userMap.get(userID);
		}
		else
			return null;
	}
}
