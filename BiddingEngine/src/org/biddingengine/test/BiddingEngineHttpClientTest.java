package org.biddingengine.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class BiddingEngineHttpClientTest {

	ArrayList<String> bidderIDArray = new ArrayList<>();
	ArrayList<String> itemIDArray = new ArrayList<>();
	
	int basePrice = 10;
	
	public static final int TEST_BIDDER_COUNT = 100;
	public static final int TEST_ITEM_COUNT = 100;
	public static final int TEST_DURATION = 60;
	
	public static final String putUserUrl = "http://localhost:8080/BiddingEngine/engine/users";
	public static final String putItemUrl = "http://localhost:8080/BiddingEngine/engine/items";
	
	public static void main(String[] args) {
		BiddingEngineHttpClientTest testObject = new BiddingEngineHttpClientTest();
		
		System.out.println("Adding Seller");
		System.out.println("Status Code: " +testObject.addUser("sampleSeller", "Sample Seller"));
		
		System.out.println("Adding Bidders");
		for(int bidderCount = 0; bidderCount < TEST_BIDDER_COUNT; bidderCount++){
			String bidderUID = "sampleBidder" + bidderCount;	
			String bidderName = "Sample Bidder " + bidderCount;
			testObject.bidderIDArray.add(bidderUID);
			System.out.println("Adding Bidder: " + bidderUID + " Status Code: " +testObject.addUser(bidderUID, bidderName));
		}
		
		System.out.println("Adding Items");
		for(int itemCount = 0; itemCount < TEST_ITEM_COUNT; itemCount++){
			String itemName = "Item" + itemCount;
			String itemID = testObject.addItem(itemName);
			testObject.itemIDArray.add(itemID);
			System.out.println("Adding Item: " + itemName + " Item ID: " +itemID);
		}
		
		System.out.println("Start Bidding");
		testObject.testFixedNumberBidders();
	}
	
	public int addUser(String userID, String userName){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		
		JSONObject userJSON = new JSONObject();
		try {
			userJSON.put("userID", userID);
			userJSON.put("userName", userName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpPut putUser = new HttpPut(putUserUrl);
		putUser.addHeader("Content-Type", "application/json");
		putUser.addHeader("Accept", "application/json");
		StringEntity input;
		try {
			input = new StringEntity(userJSON.toString());
			putUser.setEntity(input);
			response = client.execute(putUser);
			return response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public String addItem(String itemName){
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;
		String itemID = "";
		JSONObject itemJSON = new JSONObject();
		try {
			itemJSON.put("name", itemName);
			itemJSON.put("description", "Description of " + itemName);
			itemJSON.put("startPrice", 10.0);
			itemJSON.put("sellerUID", "sampleSeller");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpPut putUser = new HttpPut(putItemUrl);
		putUser.addHeader("Content-Type", "application/json");
		putUser.addHeader("Accept", "application/json");
		StringEntity input;
		try {
			input = new StringEntity(itemJSON.toString());
			putUser.setEntity(input);
			response = client.execute(putUser);
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON = new JSONObject(responseBody);
			itemID = responseJSON.getString("itemID");
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemID;
	}

	public void addBid(String itemID, String bidderID, float newPrice){
	
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;
		JSONObject bidJSON = new JSONObject();
		try {
			bidJSON.put("bidderUID", bidderID);
			bidJSON.put("bidPrice", newPrice);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpPut putUser = new HttpPut(putItemUrl + "/" + itemID + "/bids");
		putUser.addHeader("Content-Type", "application/json");
		putUser.addHeader("Accept", "application/json");
		StringEntity input;
		try {
			input = new StringEntity(bidJSON.toString());
			putUser.setEntity(input);
			response = client.execute(putUser);
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON;
			try {
				responseJSON = new JSONObject(responseBody);
				itemID = responseJSON.getString("itemID");
			} catch (JSONException e) {
				System.out.println(responseBody);
			}			
		} catch (IOException e) {}
	}
	
	public void getResults(){

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response;
		String responseBody;
		
		HttpGet getItem = new HttpGet(putItemUrl);
		getItem.addHeader("Content-Type", "application/json");
		getItem.addHeader("Accept", "application/json");
		try {
			response = client.execute(getItem);
			responseBody = readFromStream(response.getEntity().getContent());
			JSONObject responseJSON = new JSONObject(responseBody);
			JSONArray responseArray = responseJSON.getJSONArray("items");
			for(int itemCount = 0; itemCount < responseArray.length(); itemCount++){
				JSONObject thisItem = responseArray.getJSONObject(itemCount);
				String itemName = thisItem.getString("name");
				String buyerID = thisItem.getString("buyerID");
				String soldPrice = thisItem.getString("soldPrice");
				System.out.println("Item :" +itemName+ " sold to: " +buyerID+ " at Price: " +soldPrice);
			}
			
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String readFromStream(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));;
		String str = null;
		StringBuilder sbuilder = new StringBuilder();
		while((str = br.readLine()) != null){
			sbuilder.append(str);
		}
		return sbuilder.toString();
	}
	
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
		getResults();
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
					addBid(itemID, bidderID, newPrice);
					System.out.println("Placing Bid for :" +itemID+ " from Bidder: " +bidderID+ " with Bid Value: " +newPrice);
					Thread.sleep(1);
				} catch (InterruptedException e) {
					
				}
			}
		}		
	}
}
