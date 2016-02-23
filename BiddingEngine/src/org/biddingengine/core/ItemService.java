package org.biddingengine.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Configuration;
import org.biddingengine.datamodel.Item;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;

/**
 * Service to perform addition and removal of items for bidding
 * During initialization the service starts a batch processor 
 * that runs at scheduled intervals, iterates over the item map
 * and makes the items inactive for bids incase current time 
 * exceeds the bid interval for that particular object. 
 * @author piyush
 *
 */
public class ItemService {

	private final ConcurrentHashMap<String, Item> itemMap;
	private final UserService userService;
	private ScheduledExecutorService itemBatchProcessor;
	
	public ItemService(ConcurrentHashMap<String, Item> itemMap, UserService userService){
		this.itemMap = itemMap;
		this.userService = userService;
		ItemsProcessor processor = new ItemsProcessor(itemMap);
		itemBatchProcessor = new ScheduledThreadPoolExecutor(1);
		itemBatchProcessor.scheduleAtFixedRate(processor, 0, Configuration.ITEM_MAP_SCAN_INTERVAL, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 * @param name Name of the item
	 * @param description Description of the item
	 * @param startPrice Start Price of the item. Price at which the bidding starts
	 * @param creationTime Time when the item is placed for bidding by bidders
	 * @param sellerUID UserId of the seller who placed the item for bidding
	 * @param isActive boolean flag to indicate if the item is open for bidding or not
	 * @return String UUID of the item
	 * @throws UserNotFoundException
	 */
	public String advertizeItem(String name, String description, 
								double startPrice, long creationTime,
								String sellerUID, boolean isActive) throws UserNotFoundException {

		Item newItem =  new Item(name, description, startPrice, creationTime, sellerUID, isActive);
		if(userService.isRegistered(sellerUID)){
			itemMap.put(newItem.getItemID().toString(), newItem);
			return newItem.getItemID().toString();
		}
		else{
			throw new UserNotFoundException();
		}
	}

	public boolean withdrawItem(String itemID, String sellerUID) throws UserNotFoundException, 
																		ItemNotFoundException {

		Item item = itemMap.get(itemID);
		if(item != null){
			if(item.getSellerUID().contentEquals(sellerUID)){
				TreeSet<Bid> bidList = item.getBidList();
				bidList.removeAll(bidList);
				return true;
			}
			else{
				throw new UserNotFoundException();
			}				
		}
		else{
			throw new ItemNotFoundException();
		}
	}
	
	/**
	 * 
	 * @return List of all the Item objects 
	 */
	public List<Item> getItems(){

		Set<Entry<String, Item>> entries = itemMap.entrySet();
		List<Item> retList = new ArrayList<>();
		if(entries != null){
			Iterator<Entry<String, Item>> iter = entries.iterator();
			while(iter.hasNext()){
				Entry<String,Item> entry = iter.next();
				Item item = entry.getValue();
				retList.add(item);
			}
		}
		return retList;
	}
	
	/**
	 * 
	 * @param itemID ID of the item to be searched 
	 * @return Item object representing the object
	 * @throws ItemNotFoundException
	 */
	public Item getItem(String itemID) throws ItemNotFoundException{

		Item item = itemMap.get(itemID);
		
		if(item == null)
			throw new ItemNotFoundException();
		
		return item;
	}
}
