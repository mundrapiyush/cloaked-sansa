package org.biddingengine.datamodel;

public class User {

	private final String userID;
	private String name;
	
	public User(String userID, String name){
		this.userID = userID;
		this.name = name;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object o){
		if(o != null && o instanceof User){
			return userID.equals(o);
		}
		else
			return false;
	}
	
	public int hashCode(){
		return userID.hashCode();
	}
}
