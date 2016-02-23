package org.biddingengine.core;

import java.util.concurrent.ConcurrentHashMap;

import org.biddingengine.datamodel.Item;
import org.biddingengine.datamodel.ServiceType;
import org.biddingengine.datamodel.User;

/**
 * Singleton registry in which all the services are
 * placed. 
 * BidService - to perform all the bid for items
 * ItemService - to perform adding and removal of items
 * Userservice - to perform adding and removal of users (bidders/sellers)
 * @author piyush
 *
 */
public class ServiceRegistry {
	private static ConcurrentHashMap<ServiceType, Object> registry = new ConcurrentHashMap<>();
	
	public ServiceRegistry() {
		
		ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();
		ConcurrentHashMap<String, Item> itemMap = new ConcurrentHashMap<>();
		
		UserService userService = new UserService(userMap);
		ItemService itemService = new ItemService(itemMap, userService);
		BidService bidService = new BidService(itemMap,  userService);
				
		registry.put(ServiceType.USER_SERVICE, userService);
		registry.put(ServiceType.ITEM_SERVICE, itemService);
		registry.put(ServiceType.BID_SERVICE, bidService);
	}
	
	public static Object getService(ServiceType type){
		return registry.get(type);
	}	
}
