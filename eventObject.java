package com.simulation.logprocessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class eventObject {
	
	private String eventJson ; 
	
	
	private JsonParser jp = new JsonParser();
	
	private int HOTLEADCODE = 110000 ;
	private int PURCHASECODE = 11122 ;
	public eventObject(String eventJson)
	{	
		this.eventJson = eventJson ;
		
	}
	
	

	public String getDataPtId()
	
	{
		String retstr = "nullevent" ;
		try
		{
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();

		JsonElement event1 = jsonObject1.get("bsid");

		if(event1 != null)
		{
			retstr = event1.getAsString() ;
		}

		}
		catch(Exception e)
		{
		}
		return retstr ;
	}
	
	
	public String getTP()
	{
		String retstr = "nullevent" ;
		try
		{
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();

		JsonElement event1 = jsonObject1.get("tp");

		if(event1 != null)
		{
			retstr = event1.getAsString() ;
		}

		}
		catch(Exception e)
		{
		}
		return retstr ;
	}
	public int getPageIndex()
	{
		int pageindex = 99999999;
		try
		{
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();
		
		
		JsonElement event1 = jsonObject1.get("r");
		if(event1 != null)
		{
		JsonObject jsonObject2 = event1.getAsJsonObject();

		JsonElement event2 = jsonObject2.get("totalVisitCount");
		if(event2 != null)
		{
			pageindex = event2.getAsInt() ;
		}

		}

		}
		catch(Exception e)
		{
			
		}
		return  pageindex;
	}
	
	public ArrayList<String> getFiredRules()
	{
		 JsonArray rulesFired = new JsonArray() ;
		 ArrayList<String> rf =  new ArrayList<String>() ;
		try
		{
		
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();
		
		JsonElement event1 = jsonObject1.get("ed");
		
		if((event1 != null))
		{
			JsonObject jsonObject2 = event1.getAsJsonObject();
			JsonElement event2 = jsonObject2.get("rcs");
			if(event2 != null)
			{
				rulesFired  = event2.getAsJsonArray() ;
			}
		}
		
		}
		catch(Exception e)
		{
			
		}
		Iterator<JsonElement> it = rulesFired.iterator() ;
		while(it.hasNext())
		{
			 rf.add(it.next().getAsString()) ;
		}
		return rf ;
		
	}
	
	public boolean getHotLeadFlag()
	{
		boolean isHotlead = false ;
		try
		{
		
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();
		
		JsonElement event1 = jsonObject1.get("ec");
		
		if((event1 != null) && (event1.getAsInt() == HOTLEADCODE))
		{
			isHotlead = true ;
		}
		
		}
		catch(Exception e)
		{
			
		}
		return isHotlead ;
	}
	
	public int getEventCode()
	{
		int eventcode = 0 ;
		try
		{
		
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();
		
		JsonElement event1 = jsonObject1.get("ec");
		
		if((event1 != null))
		{
			eventcode = event1.getAsInt() ;
		}
		
		}
		catch(Exception e)
		{
			
		}
		return eventcode ;
		
	}
	
	public boolean getPurchaseFlag()
	{
		boolean ispurchase = false ;
		try
		{
		
		JsonElement parsed = jp.parse(eventJson);
		JsonObject jsonObject = parsed.getAsJsonObject();
		JsonElement event = jsonObject.get("e");
		JsonObject jsonObject1 = event.getAsJsonObject();
		
		JsonElement event1 = jsonObject1.get("ec");
		
		if((event1 != null) && (event1.getAsInt() == PURCHASECODE))
		{
			ispurchase = true ;
		}
		
		}
		catch(Exception e)
		{
			
		}
		return ispurchase ;
	}
	
	
}
