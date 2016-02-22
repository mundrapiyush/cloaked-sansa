package org.biddingengine.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.biddingengine.core.ServiceRegistry;

public class BootStrapper extends Application{
	
	public BootStrapper() {
		ServiceRegistry registry = new ServiceRegistry();
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		// TODO Auto-generated method stub
		return super.getClasses();
	}
	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();
		singletons.add(new UserServiceEndPoint());
		singletons.add(new ItemServiceEndPoint());
		singletons.add(new JSONProvider());
		return singletons;
	}
}
