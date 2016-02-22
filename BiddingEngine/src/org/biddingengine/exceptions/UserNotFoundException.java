package org.biddingengine.exceptions;

@SuppressWarnings("serial")
public class UserNotFoundException extends Exception {
	
	@Override
	public String getMessage() {
		return ("Item not found");
	}
}
