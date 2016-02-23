package org.biddingengine.core;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;

/**
 * Item processor thread to scan the Item Map 
 * An iteration is performed over the Map at 
 * scheduled interval (default 5 seconds) and 
 * flags all the items as inactive for which 
 * the bidding interval is expired. While flagging
 * and object as inactive transaction is performed
 * wherein the bidder id from first (winner) bid is
 * given the item.
 *  
 * @author piyush
 *
 */
public class ItemsProcessor implements Runnable {

	ConcurrentHashMap<String, Item> itemMap;
	
	public ItemsProcessor(ConcurrentHashMap<String, Item> itemMap){
		this.itemMap = itemMap;
	}
	
	@Override
	public void run() {
		Set<Entry<String,Item>> keys = itemMap.entrySet();
		Iterator<Entry<String,Item>> iter = keys.iterator();
		while(iter.hasNext()){
			Item item = iter.next().getValue();
			if(System.currentTimeMillis() >= (item.getCreationTime() + Configuration.BID_EXPIRY_INTERVAL)){
				synchronized (item) {
					System.out.println("Item with ID: " +item.getSellerUID() + " expired.");
					item.setActive(false);
					if(!item.getBidList().isEmpty()){
						Bid winnerBid = item.getBidList().first();
						item.setBuyerID(winnerBid.getBidderUID());
						item.setSoldPrice(winnerBid.getBidPrice());
					}
				}
			}
		}
	}
}
