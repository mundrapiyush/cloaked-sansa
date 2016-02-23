package org.biddingengine.core;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;

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
			Item item = itemMap.get(iter.next());
			if(System.currentTimeMillis() > item.getCreationTime() + (Configuration.BID_EXPIRY_INTERVAL * 1000)){
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
