package org.biddingengine.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;
import org.biddingengine.exceptions.BidExpiredException;
import org.biddingengine.exceptions.BidInvalidException;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;

/**
 * Service to perform Bidding over a given item.
 * Placing of the bid for an item is handled by a seperate thread from the thread pool
 * @author piyush
 *
 */
public class BidService {

	private final ConcurrentHashMap<String, Item> itemMap;
	private ExecutorService executorService;
	private UserService userService;
	private final AtomicLong totalBidCount;
	
	public BidService(ConcurrentHashMap<String, Item> itemMap, UserService userService){
		this.itemMap = itemMap;
		this.userService = userService;
		totalBidCount = new AtomicLong();
		executorService= Executors.newFixedThreadPool(Configuration.BID_THREAD_POOL_SIZE);
	}
	
	/**
	 * 
	 * @param itemID Item for which the bid is to be placed
	 * @param bidderUID UserId of the bidder which placed the bid
	 * @param bidValue Value of the bid
	 * @return Bid object representing the newly placed bid
	 * @throws BidInvalidException
	 * @throws UserNotFoundException
	 * @throws ItemNotFoundException
	 * @throws BidExpiredException
	 */
	public Bid registerBid(String itemID, String bidderUID, float bidValue) throws BidInvalidException, 
																				   UserNotFoundException, 
																				   ItemNotFoundException,
																				   BidExpiredException {
		Item item = itemMap.get(itemID);
	
		if(item != null){
			totalBidCount.getAndIncrement();
			if(bidValue <= item.getStartPrice()){
				throw new BidInvalidException(bidValue);
			}

			if(item.isActive()){
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
				throw new BidExpiredException();
			}
		}
		else{
			throw new ItemNotFoundException();
		}
	}

	public boolean unregisterBid(String itemID, String bidID) {
		return false;
	}

	/**
	 * 
	 * @param itemID Item ID for which the top bid listing is to be acquired
	 * @param listSize
	 * @return
	 * @throws ItemNotFoundException
	 */
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
	
	public long getTotalBidCount() {
		return totalBidCount.get();
	}
}
