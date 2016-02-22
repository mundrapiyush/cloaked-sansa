package org.biddingengine.datamodel;

import java.util.UUID;

public class Bid {
	
    private final UUID bidID;
    private final String bidderUID;    
    private float bidPrice;
    
    public Bid(String bidderUID, float bidPrice, boolean isActive) {
    	this.bidderUID = bidderUID;
    	this.setBidPrice(bidPrice);
    	this.bidID = UUID.randomUUID();
    }

	public UUID getBidID() {
		return bidID;
	}

	public String getBidderUID() {
		return bidderUID;
	}

	public float getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(float bidPrice) {
		this.bidPrice = bidPrice;
	}
}
