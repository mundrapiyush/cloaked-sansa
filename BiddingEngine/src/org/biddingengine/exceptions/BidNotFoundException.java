package org.biddingengine.exceptions;

@SuppressWarnings("serial")
public class BidNotFoundException extends Exception {
	
	@Override
	public String getMessage() {
		return ("Item not found");
	}
}
