package org.biddingengine.exceptions;

@SuppressWarnings("serial")
public class BidInvalidException extends Exception{
	
	private final float bidVal;
	
	public BidInvalidException(float value) {
		bidVal = value;
	}
	@Override
	public String getMessage() {
		return ("Bid value " +bidVal+ " invalid. Cannot be lower than the start price.");
	}
}