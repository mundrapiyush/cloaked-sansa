package org.biddingengine.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.biddingengine.core.BidService;
import org.biddingengine.core.ItemService;
import org.biddingengine.core.ServiceRegistry;
import org.biddingengine.core.UserService;
import org.biddingengine.datamodel.ServiceType;
import org.biddingengine.exceptions.BidInvalidException;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class BiddingEngineTest {

	ServiceRegistry serviceRegistry = new ServiceRegistry();
	
	UserService userService;
	ItemService itemService;
	BidService bidService;
	
	String sellerID;
	String bidderID1;
	String bidderID2;
	String bidderID3;
	String bidderID4;	
	String bidderID5;
	
	String itemID;
	int basePrice = 10;
	public static final int TEST_DURATION = 60;

	
	public static void main(String [] args){
		BiddingEngineTest tester = new BiddingEngineTest();
		tester.initTest();
		tester.testSingleBidder();
	}
	
	@Before
	public void initTest(){
		
		serviceRegistry = new ServiceRegistry();
		
		userService = (UserService)serviceRegistry.getService(ServiceType.USER_SERVICE);
		itemService = (ItemService)serviceRegistry.getService(ServiceType.ITEM_SERVICE);
		bidService = (BidService)serviceRegistry.getService(ServiceType.BID_SERVICE);
		
		sellerID = userService.registerUser("sampleSeller", "Sample Seller");
		bidderID1 = userService.registerUser("sampleBidder1", "Sample Bidder 1");
		bidderID2 = userService.registerUser("sampleBidder2", "Sample Bidder 2");
		bidderID3 = userService.registerUser("sampleBidder3", "Sample Bidder 3");
		bidderID4 = userService.registerUser("sampleBidder4", "Sample Bidder 4");
		bidderID5 = userService.registerUser("sampleBidder5", "Sample Bidder 5");
	}
	
	@Test
	public void testSingleBidder() {
		
		long currTime = System.currentTimeMillis();
		String itemID = null;
		try {
			itemID = itemService.advertizeItem("Item1", "Item1", basePrice, currTime,  sellerID, true);
		} catch (UserNotFoundException e1) {
			System.out.println(e1.getMessage());
		}
		
		Bidder bidder1 = new Bidder(itemID, bidderID1);
		Bidder bidder2 = new Bidder(itemID, bidderID2);
		Bidder bidder3 = new Bidder(itemID, bidderID3);
		Bidder bidder4 = new Bidder(itemID, bidderID4);
		Bidder bidder5 = new Bidder(itemID, bidderID5);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.execute(bidder1);
		executor.execute(bidder2);
		executor.execute(bidder3);
		executor.execute(bidder4);
		executor.execute(bidder5);

		try {
			executor.awaitTermination(TEST_DURATION, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Finished Test.");
	}
	
	public class Bidder implements Runnable{
		
		String itemID;
		String bidderID;
		long startTime;
		
		public Bidder(String itemID, String bidderID){
			this.itemID = itemID; 
			this.bidderID = bidderID;
			startTime = System.currentTimeMillis();
		}
		
		@Override
		public void run() {
			while(System.currentTimeMillis() < (startTime + (TEST_DURATION * 1000))){
				Random random = new Random();
				int newPrice = basePrice + 1 + random.nextInt(100);
				
				try {
					bidService.registerBid(itemID, bidderID, newPrice);
					Thread.sleep(1);
				} catch (InterruptedException | BidInvalidException | UserNotFoundException | ItemNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
}
