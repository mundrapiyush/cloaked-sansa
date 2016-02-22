package org.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Item;
import org.biddingengine.datamodel.SimpleItemComparator;
import org.biddingengine.datamodel.User;

public class BiddingEngineImpl implements IBiddingEngine {

	private ConcurrentHashMap<Item, TreeSet<Bid>> bidMap;
	private ConcurrentHashMap<String, Item> itemMap;
	private ConcurrentHashMap<String, User> userMap;
	private ScheduledExecutorService executors;
	private SimpleItemComparator bidComparator;

	public BiddingEngineImpl(){
		bidMap = new ConcurrentHashMap<>();
		itemMap = new ConcurrentHashMap<>();
		userMap = new ConcurrentHashMap<>();
		executors= Executors.newScheduledThreadPool(10);
		bidComparator = new SimpleItemComparator();
	}
	
	@Override
	public String advertizeItem(String name, String description, 
								double startPrice, String sellerUID, boolean isActive) {

		Item newItem =  new Item(name, description, startPrice, sellerUID, isActive);
		if(userMap.containsKey(sellerUID)){
			TreeSet<Bid> bidList = new TreeSet<>(bidComparator);
			itemMap.put(newItem.getItemID().toString(), newItem);
			bidMap.put(newItem, bidList);
			ItemProcessor processor = new ItemProcessor(newItem, bidMap);
			executors.schedule(processor, 2, TimeUnit.MINUTES);
			return newItem.getItemID().toString();
		}
		else{
			System.out.println("Seller with " +sellerUID+ " not found. Register First !!");
			return null;
		}
	}

	@Override
	public boolean withdrawItem(String itemID, String sellerUID) {

		Item item = itemMap.get(itemID);
		if(item != null && item.getSellerUID().contentEquals(sellerUID)){
			TreeSet<Bid> bidList = bidMap.get(item);
			bidList.removeAll(bidList);
			bidMap.remove(bidList);
			return true;
		}
		return false;
	}

	@Override
	public String registerBid(String itemID, String bidderUID, float bidValue) {

		Item item = itemMap.get(itemID);
	
		if(item != null){
			if(bidValue <= item.getStartPrice()){
				System.out.println("Bid Value: " +bidValue+ 
								   " cannot be lower than Starting Price: " +item.getStartPrice());
				return "-1";
			}
			
			if(userMap.containsKey(bidderUID)){
				Bid newBid = new Bid(bidderUID, bidValue, true);
				bidMap.get(item).add(newBid);
				return newBid.getBidID().toString();
			}
			else{
				System.out.println("Bidder with " +bidderUID+ " not found. Register First !!");
				return null;
			}
		}
		else{
			System.out.println("Item with " +itemID+ " not found.");
			return null;
		}
	}

	@Override
	public boolean unregisterBid(String itemID, String bidID) {
		return false;
	}

	@Override
	public List<Bid> listBids(String itemID, int listSize) {
		Item item = itemMap.get(itemID);
		List<Bid> retList = new ArrayList<>();
		int count = 0;
		if(item != null){
			TreeSet<Bid> bidList = bidMap.get(item);
			Iterator<Bid> bidListIter = bidList.iterator();
			while(bidListIter.hasNext() && count < listSize){
				retList.add(bidListIter.next());
			}
		}
		return retList;
	}

	@Override
	public String registerUser(String userID, String userName) {
		
		if(!userMap.containsKey(userID)){
			User user = new User(userID, userName);
			userMap.put(userID, user);
		}
		return userID;
	}

	@Override
	public boolean unregisterUser(String userID) {
		if(userMap.containsKey(userID)){
			userMap.remove(userID);
			return true;
		}
		return false;			
	}
	
	public Map getBidMap(){
		return bidMap;
	}
}
