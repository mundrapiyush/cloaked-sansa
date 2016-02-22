package org.biddingengine.exceptions;

@SuppressWarnings("serial")
public class BidExpiredException extends Exception{
	
	@Override
	public String getMessage() {
		return ("Bid Expired");
	}
}
