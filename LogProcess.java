package com.simulation.logprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.DocFlavor.URL;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

public class LogProcess {	
	
	
	private Map<String,ArrayList<Integer>> HotleadPageIndexMap ;
	private Map<String,Integer> SimodHotleadMap ;
	private int defaultHotLeadPageId = 9999 ;
	private ArrayList<Integer> HotLeadCounts ;
	private ArrayList<Integer> PurchaseCounts ;
	private int maxPageIndex = 20 ;
	private int visitorCount = 0 ;
	private String tp ;
	private String mvelExpression ;
	private Set<String> inputVariables ;
	private Serializable compiledExpression ;
	private Map<String,String> varMap ;
	private double thresholdScore = 3.67637 ;
	private int ntargeted = 0;
	private ArrayList<Double> hotleadfrac ;
	
	
	public LogProcess(String TP)
	{
		HotleadPageIndexMap  = new HashMap<String,ArrayList<Integer>>() ;
		HotLeadCounts = new ArrayList<Integer>() ;
		hotleadfrac = new ArrayList<Double>() ;
		SimodHotleadMap = new HashMap<String,Integer>() ;
		PurchaseCounts = new ArrayList<Integer>() ;
		varMap = new HashMap<String, String>() ;
		tp = TP ;
		try
		{
		BufferedReader inputStream1 = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") +"mvelString"));		
		mvelExpression = inputStream1.readLine() ;
		ParserContext ctx = ParserContext.create();
		MVEL.analysisCompile(mvelExpression, ctx);
		compiledExpression = MVEL.compileExpression(mvelExpression);
		inputVariables = ctx.getInputs().keySet();
		
		varMap.put("referrer", "0");
		varMap.put("timeOnSite", "1");
		varMap.put("browserType", "2");
		varMap.put("currentPageUrl", "3");
		varMap.put("loggedIn", "4");
		varMap.put("dayOfWeek", "5");
		varMap.put("geoISP", "6");
		varMap.put("timeOnPage", "7");
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		

	}
	
	public void processEventJson(eventObject eobj)
	{
		String uniqueId = eobj.getDataPtId() ;
		String targetPop = eobj.getTP() ;
		if(uniqueId.matches("nullevent") || !targetPop.matches(tp))
		{
			return ;
		}
		int pageIndex = eobj.getPageIndex() ;
		boolean HotLeadFlag = eobj.getHotLeadFlag() ;
		boolean purcahseFlag = eobj.getPurchaseFlag() ;
		String tp = eobj.getTP() ;
		

		
		if(HotleadPageIndexMap.containsKey(uniqueId))
		{
			if(HotLeadFlag && pageIndex<(HotleadPageIndexMap.get(uniqueId)).get(0))
			{
				(HotleadPageIndexMap.get(uniqueId)).set(0, pageIndex) ;
			}
			if((HotleadPageIndexMap.get(uniqueId)).get(0)<defaultHotLeadPageId && purcahseFlag)
			{
				(HotleadPageIndexMap.get(uniqueId)).set(1, 1) ;
			}
		}
		else
		{
			HotleadPageIndexMap.put(uniqueId, new ArrayList<Integer>()) ;
			if(HotLeadFlag)
			{
				(HotleadPageIndexMap.get(uniqueId)).add(pageIndex) ;
			}
			else
			{
				(HotleadPageIndexMap.get(uniqueId)).add(defaultHotLeadPageId) ;
			}
			if(purcahseFlag && (HotleadPageIndexMap.get(uniqueId)).get(0)<defaultHotLeadPageId)
			{
				(HotleadPageIndexMap.get(uniqueId)).add(1) ;
			}
			else
			{
				(HotleadPageIndexMap.get(uniqueId)).add(0) ;
			}
			
  		}
				
	}
	
	public void generateStatistics()
	{
		for(int i=0 ; i<maxPageIndex ; i++)
		{
			HotLeadCounts.add(0);
			PurchaseCounts.add(0) ;
		}
		java.util.Iterator<Entry<String, ArrayList<Integer>>> it =  HotleadPageIndexMap.entrySet().iterator() ;
		while(it.hasNext())
		{
			Map.Entry<String, ArrayList<Integer>> tmp = it.next() ;
			String uid = tmp.getKey();
			ArrayList<Integer> val = tmp.getValue() ;
			visitorCount++ ;
			if(val.get(0) < maxPageIndex-1)
			{
				int t = HotLeadCounts.get(val.get(0)) ;
				HotLeadCounts.set(val.get(0), t+1) ;
				t = PurchaseCounts.get(val.get(0)) ;
				PurchaseCounts.set(val.get(0), t+val.get(1)) ;
			}
			else if(val.get(0) < defaultHotLeadPageId)
			{
				int t = HotLeadCounts.get(maxPageIndex - 1) ;
				HotLeadCounts.set(maxPageIndex - 1, t+1) ;
				t = PurchaseCounts.get(maxPageIndex - 1) ;
				PurchaseCounts.set(maxPageIndex - 1, t+val.get(1)) ;
			}
		}
		
		
		
		
	}
	
	
	public void generateSimodStatistics()
	{
		for(int i=0 ; i<maxPageIndex ; i++)
		{
			HotLeadCounts.add(0);
			//hotleadfrac.add(0.0) ;
			//PurchaseCounts.add(0) ;
		}
		java.util.Iterator<Entry<String, Integer>> it =  SimodHotleadMap.entrySet().iterator() ;
		while(it.hasNext())
		{
			Map.Entry<String, Integer> tmp = it.next() ;
			String uid = tmp.getKey();
			Integer val = tmp.getValue() ;
			visitorCount++ ;
			if(val < maxPageIndex-1)
			{
				int t = HotLeadCounts.get(val) ;
				HotLeadCounts.set(val, t+1) ;
				
			}
			else if(val < defaultHotLeadPageId)
			{
				int t = HotLeadCounts.get(maxPageIndex - 1) ;
				HotLeadCounts.set(maxPageIndex - 1, t+1) ;
				
			}
		}
		for(int k : HotLeadCounts)
		{
			ntargeted+=k;
			hotleadfrac.add(((100.0*k)/visitorCount)) ;
		}
		
	}
	
	public ArrayList<Integer> getHotleadCount()
	{
		return this.HotLeadCounts ;
	}
	
	public ArrayList<Integer> getPurchaseCounts()
	{
		return this.PurchaseCounts ;
	}
	
	public int getVisitorCount()
	{
		return this.visitorCount ;
	}
	
	public void processLogs(String eventJson)
	{
		eventObject eobj = new eventObject(eventJson) ;
		processEventJson(eobj) ;
	}
	
	public void readLogFile(String FilePath)
	{
		int nevents = 0;
		try
		{

		BufferedReader inputStream = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") +"tempSpiritFile_17.txt"));	

		while(true)
		{
			String ipLine = inputStream.readLine() ;
			if(ipLine != null)
			{
				String jsonString = ipLine.split("PAIR")[1];
				nevents++ ;
				processLogs(jsonString) ;
				
			}
			else
			{
				break ;
			}
		}
		System.out.println("no of events:") ;
		System.out.println(nevents) ;
		System.out.println("") ;
		generateStatistics() ;
		System.out.println("no of unique sessions:") ;
		System.out.println(visitorCount) ;
		System.out.println("") ;
		System.out.println("HotleadCounts across pages:") ;
		System.out.println(HotLeadCounts) ;
		System.out.println("") ;
		System.out.println("PurchaseCounts across pages:") ;
		System.out.println(PurchaseCounts) ;
		System.out.println("") ;
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}
	
	public void processSimodDataPt(String csvString)
	{
		String[] cols = csvString.split(",") ;
		if(cols.length != 12)
		{
			return ;
		}
		String uniqueSessionId = cols[0]+"+"+cols[1]+"+"+cols[2]+"+"+cols[3] ;
		int pageIndex = Integer.parseInt(cols[3]) ;
		Map<String,Object> variablesWithValues=new HashMap<String, Object>(); 
		for(String var : inputVariables)
		{
			
			variablesWithValues.put(var, cols[Integer.parseInt(varMap.get(var))+4]) ;
		}
		
		Object val = MVEL.executeExpression(compiledExpression, variablesWithValues);
		
		double expVal = Double.parseDouble(val.toString()) ;
		if(SimodHotleadMap.containsKey(uniqueSessionId))
		{
			if((expVal>=thresholdScore) && (pageIndex<SimodHotleadMap.get(uniqueSessionId)) )
			{
				SimodHotleadMap.put(uniqueSessionId, pageIndex) ;
			}
		}
		else
		{
			if(expVal>=thresholdScore)
			{
				SimodHotleadMap.put(uniqueSessionId, pageIndex) ;
			}
			else
			{
				SimodHotleadMap.put(uniqueSessionId, defaultHotLeadPageId) ;
			}
		}
	}
	
	public void readSimodFile()
	{
		int cnt = 0 ;
		try
		{

		BufferedReader inputStream = new BufferedReader(new FileReader(System.getProperty("user.dir") + System.getProperty("file.separator") +"optus_data"));	
		//System.out.println(inputVariables);
		while(true)
		{
			String ipLine = inputStream.readLine() ;
			if(ipLine != null)
			{
				cnt++;
				if(cnt == 1)
				{
					continue ;
				}
				processSimodDataPt(ipLine) ;
				//System.out.println(ipLine) ;
			}
			else
			{
				//System.out.println(cnt);
				break ;
				
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}
	
	public static void main(String[] args)
	{
		LogProcess lp = new LogProcess("1") ;
		lp.readLogFile("path") ;
		
		//lp.readLogFile("ok") ;
		//lp.readSimodFile();
		
		//lp.generateSimodStatistics() ;
		
		/*
		System.out.println("no of unique sessions:");
		System.out.println(lp.visitorCount);
		System.out.println("");
		
		
		System.out.println("no of sessions targeted:");
		System.out.println(lp.ntargeted);
		System.out.println("");
		
		System.out.println("fraction of visitors targeted:");
		System.out.println(((100.0*lp.ntargeted)/lp.visitorCount)+"%");
		System.out.println("");

		System.out.println("hotlead pagewise funnel:");
		System.out.println(lp.HotLeadCounts);
		System.out.println("");
		
		System.out.println("hotlead pagewise percantage funnel:");
		System.out.println(lp.hotleadfrac+"%");
		System.out.println("");*/
	}
	
}
