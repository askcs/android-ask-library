package com.askcs.android.sense;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class SenseJSONTask<T extends Object> extends SenseTask<T> {

	public SenseJSONTask( SenseApplication app, String sensorName,
			String displayName, String description ) {
		super( app, sensorName, displayName, description );
	}

	public String getType() {
		return "json";
	}

	public String getValue( Map<String, ? super T> map ) {
		JSONObject message = new JSONObject();
		try {
			for ( Map.Entry<String, ? super T> entry : map.entrySet() ) {
				message.put( entry.getKey(), entry.getValue() );
			}
		} catch ( JSONException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message.toString();
	}

}
