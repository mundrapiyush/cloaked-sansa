package org.biddingengine.core;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;

public class BidsProcessor implements Runnable {

	private final Item item;
	private final Bid bid;
	
	public BidsProcessor(Item item, Bid bid) {
		this.item = item;
		this.bid = bid;
	}
	
	public Item getItem() {
		return item;
	}
	
	public Bid getBid(){
		return bid;
	}

	@Override
	public void run() {
		synchronized (item) {
			if(item.isActive()){
				System.out.println("Bid Received from: " +bid.getBidderUID()+ " with value: " +bid.getBidPrice());
				item.getBidList().add(bid);
				if(System.currentTimeMillis() >= item.getCreationTime() + (Configuration.BID_EXPIRY_INTERVAL * 1000))
					item.setActive(false);
			}
			else{
				System.out.println("Bid Received for an expired item. ");
			}				
		}
	}
}
