package org.biddingengine.exceptions;

@SuppressWarnings("serial")
public class ItemNotFoundException extends Exception {
	
	@Override
	public String getMessage() {
		return ("Item not found");
	}
}
