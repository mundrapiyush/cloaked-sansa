package org.biddingengine.core;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;


/**
 * Bid Processor thread to perform bidding for a given item 
 * If the item is in active state ( non expired ) the bid is added
 * to the list of bid for the item. The bid list is a sorted 
 * set of all the bids placed for the item. First element of the
 * set is the potential winner bid. 
 * 
 * If current time exceeds the bid expiry interval then perform 
 * the transaction and assign the item to the winner bidder and 
 * make the item inactive for all future bids.
 * @author piyush
 *
 */
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
				if(System.currentTimeMillis() >= (item.getCreationTime() + Configuration.BID_EXPIRY_INTERVAL)){
					System.out.println("Item with ID: " +item.getSellerUID() + " expired.");
					item.setActive(false);
					if(!item.getBidList().isEmpty()){
						Bid winnerBid = item.getBidList().first();
						item.setBuyerID(winnerBid.getBidderUID());
						item.setSoldPrice(winnerBid.getBidPrice());
					}				
				}
			}
			else{
				System.out.println("Bid Received for an expired item. ");
			}				
		}
	}
}
