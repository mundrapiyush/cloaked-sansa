package org.biddingengine.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.biddingengine.core.ServiceRegistry;
import org.biddingengine.core.UserService;
import org.biddingengine.datamodel.ServiceType;
import org.biddingengine.datamodel.User;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/users")
public class UserServiceEndPoint {

	private UserService userService;
	
	public UserServiceEndPoint() {
		userService = (UserService)ServiceRegistry.getService(ServiceType.USER_SERVICE);
	}
	
	@GET
	@Path("/{userID}")
	@Produces("application/json")
	public Response getUser(@PathParam("userID") String userID) throws JSONException{
		
		ResponseBuilder builder = new ResponseBuilderImpl();
		
		User user = userService.getUser(userID);
		if(user != null){
			JSONObject object = new JSONObject();
			object.put("userID", user.getUserID());
			object.put("userName", user.getName());
			builder.entity(object);
			builder.status(Response.Status.OK);
		}
		else
			builder.status(Response.Status.NOT_FOUND);		
		return builder.build();
	}
	
	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response addUser(JSONObject request,
							@Context UriInfo urlInfo) throws JSONException{
		ResponseBuilder builder = new ResponseBuilderImpl();
		
		String userID = request.getString("userID");
		String userName = request.getString("userName");
		
		userService.registerUser(userID, userName);
		
		JSONObject object = new JSONObject();
		object.put("userURL", urlInfo.getRequestUri() + "/" + userID);
		
		builder.entity(object);
		builder.status(Response.Status.OK);
		
		return builder.build();
	}
}
