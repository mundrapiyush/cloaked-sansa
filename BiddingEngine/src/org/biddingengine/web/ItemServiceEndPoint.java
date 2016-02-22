package org.biddingengine.web;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.biddingengine.core.BidService;
import org.biddingengine.core.ItemService;
import org.biddingengine.core.ServiceRegistry;
import org.biddingengine.datamodel.Bid;
import org.biddingengine.datamodel.Item;
import org.biddingengine.datamodel.ServiceType;
import org.biddingengine.exceptions.BidInvalidException;
import org.biddingengine.exceptions.ItemNotFoundException;
import org.biddingengine.exceptions.UserNotFoundException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/items")
public class ItemServiceEndPoint {

	private ItemService itemService;
	private BidService bidService;
	
	public ItemServiceEndPoint() {
		itemService = (ItemService)ServiceRegistry.getService(ServiceType.ITEM_SERVICE);
		bidService = (BidService)ServiceRegistry.getService(ServiceType.BID_SERVICE);
	}
	
	@GET
	@Produces("application/json")
	public Response getItems() {
		ResponseBuilder response = new ResponseBuilderImpl();
		List<Item> itemList = itemService.getItems();
		ObjectMapper mapper = new ObjectMapper();
		JSONArray itemJSONArray = new JSONArray();
		try {
			for(Item item : itemList){
				String jsonString = mapper.writeValueAsString(item);
				itemJSONArray.put(new JSONObject(jsonString));
			}
			JSONObject responseObject = new JSONObject();
			responseObject.put("items", itemJSONArray);
			response.entity(responseObject);
			response.status(Response.Status.OK);

		} catch (IOException | JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
	
	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response putItemForBid(JSONObject item, @Context UriInfo uriInfo){
		
		ResponseBuilder response = new ResponseBuilderImpl();
		String name = null;
		String desc = null;
		float strtPc = -1f;
		String suid = null;
		long crTme = -1;
		
		try{
			name = item.getString("name");
			desc = item.getString("description");
			strtPc = new Float(item.getString("startPrice")).floatValue();
			suid = item.getString("sellerUID");
			crTme = System.currentTimeMillis();
		} catch(JSONException e){
			response.entity(e.getMessage());
			response.status(Response.Status.BAD_REQUEST);
			return response.build();
		}
		
		try {
			String itemID = itemService.advertizeItem(name, desc, strtPc, crTme, suid, true);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("itemID", itemID);
			jsonObject.put("itemURL", uriInfo.getRequestUri() + "/" + itemID);
			response.entity(jsonObject);
			response.status(Response.Status.OK);
			
		} catch (JSONException | UserNotFoundException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
			return response.build();
		}
		return response.build();
	}
	
	@GET
	@Path("/{itemID}")
	@Produces("application/json")
	public Response getItem(@PathParam("itemID") String itemID){

		ResponseBuilder response = new ResponseBuilderImpl();

		try{
			Item item = itemService.getItem(itemID);
			ObjectMapper mapper = new ObjectMapper();
			String itemAsJSON = mapper.writeValueAsString(item);
			JSONObject jsonObject = new JSONObject(itemAsJSON);
			response.entity(jsonObject);
			response.status(Response.Status.OK);
		
		} catch (IOException | JSONException | ItemNotFoundException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}

	@GET
	@Path("/{itemID}/bids")
	@Produces("application/json")
	public Response getTopBids(@PathParam("itemID") String itemID){
		
		ResponseBuilder response = new ResponseBuilderImpl();
		try {
			List<Bid> bidList = bidService.listBids(itemID, 5);
			ObjectMapper mapper = new ObjectMapper();
			JSONArray bidJSONArray = new JSONArray();
			for(Bid bid : bidList){
				String jsonString = mapper.writeValueAsString(bid);
				bidJSONArray.put(new JSONObject(jsonString));
			}
			response.entity(new JSONObject().put("bids", bidJSONArray));
			response.status(Response.Status.OK);

		} catch (IOException | JSONException | ItemNotFoundException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
	
	@PUT
	@Path("/{itemID}/bids")
	@Produces("application/json")
	@Consumes("application/json")
	public Response putBidForItem(@PathParam("itemID") String itemID, JSONObject bid){
		
		ResponseBuilder response = new ResponseBuilderImpl();
		String bidderUID;
		float bidValue;
		
		try {
			bidderUID = bid.getString("BidderUID");
			bidValue = new Float(bid.getString("BidPrice")).floatValue();
		} catch (JSONException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.BAD_REQUEST);
			return response.build();
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			Bid newBid = bidService.registerBid(itemID, bidderUID, bidValue);
			String jsonObject = mapper.writeValueAsString(newBid);
			response.entity(jsonObject);
			response.status(Response.Status.OK);
		} catch (IOException | BidInvalidException | UserNotFoundException | ItemNotFoundException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();	
	}
	
	@GET
	@Path("/{itemID}/bids/{bidID}")
	@Produces("application/json")
	public Response getBid(@PathParam("itemID") String itemID,
						   @PathParam("bidID") String bidID	){

		ResponseBuilder response = new ResponseBuilderImpl();
		try{
			Bid bid = bidService.getBid(itemID, bidID);
			ObjectMapper mapper = new ObjectMapper();
			JSONObject jsonObject = null;
			String itemAsJSON = mapper.writeValueAsString(bid);
			jsonObject = new JSONObject(itemAsJSON);
			response.entity(jsonObject);
			response.status(Response.Status.OK);
		
		} catch (IOException | JSONException | ItemNotFoundException e) {
			response.entity(e.getMessage());
			response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return response.build();
	}
}
