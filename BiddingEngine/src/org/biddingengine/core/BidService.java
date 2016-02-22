package org.biddingengine.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Item;
import org.biddingengine.exceptions.BidInvalidException;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;

public class BidService {

	private final ConcurrentHashMap<String, Item> itemMap;
	private ExecutorService executorService;
	private UserService userService;
	
	public BidService(ConcurrentHashMap<String, Item> itemMap, UserService userService){
		this.itemMap = itemMap;
		this.userService = userService;
		executorService= Executors.newCachedThreadPool();
	}
	
	public Bid registerBid(String itemID, String bidderUID, float bidValue) throws BidInvalidException, 
																				   UserNotFoundException, 
																				   ItemNotFoundException {

		Item item = itemMap.get(itemID);
	
		if(item != null){
			if(bidValue <= item.getStartPrice()){
				throw new BidInvalidException(bidValue);
			}
			
			if(userService.isRegistered(bidderUID)){
				Bid newBid = new Bid(bidderUID, bidValue, true);
				BidsProcessor executor = new BidsProcessor(item, newBid);
				executorService.execute(executor);
				return newBid;
			}
			else{
				throw new UserNotFoundException();
			}
		}
		else{
			throw new ItemNotFoundException();
		}
	}

	public boolean unregisterBid(String itemID, String bidID) {
		return false;
	}

	public List<Bid> listBids(String itemID, int listSize) throws ItemNotFoundException {
		Item item = itemMap.get(itemID);
		List<Bid> retList = new ArrayList<>();
		int count = 0;
		if(item != null){
			TreeSet<Bid> bidList = item.getBidList();
			Iterator<Bid> bidListIter = bidList.iterator();
			while(bidListIter.hasNext() && count < listSize){
				retList.add(bidListIter.next());
			}
		}
		else{
			throw new ItemNotFoundException();
		}			
		return retList;
	}

	public Bid getBid(String itemID, String bidID) throws ItemNotFoundException{

		Item item = itemMap.get(itemID);
		Bid bid = null;
		if(item != null){
			Iterator<Bid> iter = item.getBidList().iterator();
			while(iter.hasNext()){
				Bid b = iter.next();
				if(b.getBidderUID().contentEquals(bidID)){
					bid = b;
					break;
				}
			}
		}
		else{
			throw new ItemNotFoundException();
		}
		return bid;
	}
}
