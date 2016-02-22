package org.bkup;

import java.util.List;

import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Item;
import org.biddingengine.datamodel.User;

public interface IBiddingEngine {
	
	public static int EXPIRY_TIME = 60 * 60 * 1000;

	/*
	 * Post an item for Bid 
	 * Specify the starting bid price and a date when the bid will close
	 * */
	public String advertizeItem(String name, String description, double startPrice, long creationTime, String sellerUID, boolean isActive);
    
    /*
	 * Withdraw an item from Bidding 
	 * Notify all the registered Bidders
	 * */
    public boolean withdrawItem(String itemID, String sellerID);

    /*
     * User specifies a bid price 
     * System updates the maximum bid price
     * Informs registered bidders/users
     * */
    public String registerBid(String itemID, String bidderUID, float bidValue);

    /*
     * User specifies a an existing bid placed earlier
     * System updates the maximum bid price
     * Informs registered bidders/users
     * */
    public boolean unregisterBid(String itemID, String bidID);

    /*
     * List Top Bids for a given item
     * */
    public List<Bid> listBids(String itemID, int listSize); 
}
