package org.biddingengine.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Provider
@Produces("application/json")
@Consumes("application/json")

public class JSONProvider implements MessageBodyReader<JSONObject>, MessageBodyWriter<JSONObject>{

	@Override
	public long getSize(JSONObject arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == JSONObject.class;
	}

	@Override
	public void writeTo(JSONObject response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> objectMap, OutputStream entityStream) throws IOException, WebApplicationException {
        BufferedOutputStream bos = new BufferedOutputStream(entityStream);
        bos.write(response.toString().getBytes());
        bos.flush();
        bos.close();
/*        DataOutputStream dos = new DataOutputStream(entityStream);
        dos.writeChars(response.toString());
        dos.
*/	}

	@Override
	public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public JSONObject readFrom(Class<JSONObject> request, Type type, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> objectMap, InputStream entityStream) throws IOException, WebApplicationException {
		
		BufferedReader breader = new BufferedReader(new InputStreamReader(entityStream));
		StringBuilder sBuilder = new StringBuilder();
		String line;
		JSONObject inputObject = null;
		
		while((line = breader.readLine()) != null){
			sBuilder.append(line);
		}
		
		try {
			inputObject = new JSONObject(sBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inputObject;
	}
}
