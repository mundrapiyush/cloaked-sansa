package org.biddingengine.test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.biddingengine.core.BidService;
import org.biddingengine.core.ItemService;
import org.biddingengine.core.ServiceRegistry;
import org.biddingengine.core.UserService;
import org.biddingengine.datamodel.Item;
import org.biddingengine.datamodel.ServiceType;
import org.biddingengine.exceptions.BidExpiredException;
import org.biddingengine.exceptions.BidInvalidException;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class BiddingEngineTest {

	ServiceRegistry serviceRegistry;
	
	UserService userService;
	ItemService itemService;
	BidService bidService;
	
	int basePrice = 10;
	
	public static final int TEST_DURATION = 60;
	public static final int TEST_ITEM_COUNT = 100;
	public static final int TEST_BIDDER_COUNT = 100;
	
	ArrayList<String> itemIDArray = new ArrayList<>();
	ArrayList<String> bidderIDArray = new ArrayList<>();
	
	public static void main(String [] args){
		BiddingEngineTest tester = new BiddingEngineTest();
		tester.initTest();
		tester.testFixedNumberBidders();
	}
	
	@Before
	public void initTest(){
		
		// Initializing System
		serviceRegistry = new ServiceRegistry();
		
		userService = (UserService)ServiceRegistry.getService(ServiceType.USER_SERVICE);
		itemService = (ItemService)ServiceRegistry.getService(ServiceType.ITEM_SERVICE);
		bidService = (BidService)ServiceRegistry.getService(ServiceType.BID_SERVICE);
		
		// Creating seller
		String sellerID = userService.registerUser("sampleSeller", "Sample Seller");
		
		// Advertising items (no of items is governed by TEST_ITEM_COUNT variable
		// The itemIDs generated are then maintained in an ArrayList
		long currTime = System.currentTimeMillis();
		String itemID = null;
		
		for(int itemCount = 0; itemCount < TEST_ITEM_COUNT; itemCount++){
			String itemName = "Item" + itemCount;
			try {
				itemID = itemService.advertizeItem(itemName, itemName, basePrice, currTime,  sellerID, true);
				itemIDArray.add(itemID);
			} catch (UserNotFoundException e1) {
				System.out.println(e1.getMessage());
			}
		}
		
		// Creating Bidders
		// The biddeIDs generated are then maintained in an ArrayList 
		for(int bidderCount = 0; bidderCount < TEST_BIDDER_COUNT; bidderCount++){
			String bidderUID = "sampleBidder" + bidderCount;
			String bidderName = "Sample Bidder " + bidderCount;
			String bidderID = userService.registerUser(bidderUID, bidderName);
			bidderIDArray.add(bidderID);
		}
	}
	
	@Test
	public void testFixedNumberBidders() {
		
		// Starting workers that simulate bidders
		ExecutorService executor = Executors.newFixedThreadPool(100);
		for(int bidderIdx = 0; bidderIdx < TEST_BIDDER_COUNT; bidderIdx++){
			Bidder bidder = new Bidder(bidderIDArray.get(bidderIdx));
			executor.execute(bidder);
		}

		// Let them fight for TEST_DURATION
		try {
			executor.awaitTermination(TEST_DURATION, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Finished Test. Summary:");
		System.out.println("-----------------------");
		System.out.println("Processed Bids: " +bidService.getTotalBidCount());
		System.out.println("Time Executed (seconds): " + TEST_DURATION);
		System.out.println("-----------------------");
		for(Item item : itemService.getItems()){
			System.out.println(" Item Name: " + item.getName()+
							   " StartPrice: " + item.getStartPrice()+
							   " Buyer Name: " + item.getBuyerID()+
							   " SoldPrice: " + item.getSoldPrice());
		}
		System.exit(0);
	}
	
	public class Bidder implements Runnable{
		
		String bidderID;
		long startTime;
		
		public Bidder(String bidderID){
			this.bidderID = bidderID;
			startTime = System.currentTimeMillis();
		}
		
		@Override
		public void run() {
			while(System.currentTimeMillis() < (startTime + (TEST_DURATION * 1000))){
				Random random = new Random();
				
				// Lets see which item we want to bid next
				// Generate a new index for the itemIDArray
				int itemIdx = random.nextInt(TEST_ITEM_COUNT);
				String itemID = itemIDArray.get(itemIdx);
				
				// Generate a new bid with value taking ceiling of 100
				int newPrice = basePrice + 1 + random.nextInt(100);
				
				// Register our bid and take a nap of 1 micro second
				try {
					bidService.registerBid(itemID, bidderID, newPrice);
					Thread.sleep(1);
				} catch (InterruptedException | BidInvalidException | UserNotFoundException | ItemNotFoundException | BidExpiredException e) {
					
				}
			}
		}		
	}
}
