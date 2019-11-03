package com.wonderbuild.ematch.functions;

import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class InitProcess {
	
	public static HashMap<String,HashMap<String,String>> sourceData = new HashMap<String,HashMap<String,String>>();
	public static HashMap<String,HashMap<String,String>> targetData = new HashMap<String,HashMap<String,String>>();
	public static HashMap<String,HashMap<String,String>> resultData = new HashMap<String,HashMap<String,String>>();
	public static HashMap<String,HashMap<String,List<String>>> controlInfo = new HashMap<String,HashMap<String,List<String>>>();
	public static HashMap<String,List<String>> patternList = new HashMap<String,List<String>>();
	public static String header ;
	public static String absolutePath= new File("").getAbsolutePath();
	public static String controlPath = new File("").getAbsolutePath()+"\\Control";
	public static String sourcePath = new File("").getAbsolutePath()+"\\Source Data";
	public static String targetPath = new File("").getAbsolutePath()+"\\Target Data";
	public static String resultPath = new File("").getAbsolutePath()+"\\Result";
	
	public static void setHeader(String head) {
		header = head;
		//Need to exception handling when there is duplicate key
	}
	public static void setSourceData(String key,HashMap<String,String> inner) {
		sourceData.put(key, inner);
		//Need to exception handling when there is duplicate key
	}
	public static void setTargetData(String key,HashMap<String,String> inner) {
		targetData.put(key, inner);
		//Need to exception handling when there is duplicate key
	}
	public static void setResultData(String key,HashMap<String,String> inner) {
		resultData.put(key, inner);
		//Need to exception handling when there is duplicate key
	}
	public static void setControlInfo(String key,HashMap<String,List<String>> inner) {
		controlInfo.put(key, inner);
		//Need to exception handling when there is duplicate key
	}
	public static void setPatternList(String key,List<String> inner)
	{
		patternList.put(key, inner);
		//Need to exception handling when there is duplicate key
	}
	public static HashMap<String,String> getInnerData(String type,String key){
		switch (type.toLowerCase()){
			case "source" :
			  return sourceData.get(key);
			case "target" :
			  return targetData.get(key);
			case "result" :
			  return resultData.get(key);
			default :
			  return null;
		}
	}
	public static void setResult(String fileName) throws IOException
	{
		boolean useControlList = false;
		//boolean firstRow       = true;
		if(sourceData.isEmpty() || targetData.isEmpty())
		{
			System.out.println("Source Data and//or Target Data is not loaded.");
		}
		else
		{
			//File file = new File("C:\\Users\\iyeu.cheong\\accouting_workspace\\eMatch\\results.txt");
			
			
			HashMap<String, List<String>> controlInner = controlInfo.get(fileName);
			List<String> controlList = controlInner.get("0");
			
			if(!controlList.get(0).isEmpty())
				useControlList = true;
			
			if(!useControlList)
			{
				Iterator<Entry<String,HashMap<String,String>>> it = sourceData.entrySet().iterator();
				while(it.hasNext())
				{
					HashMap.Entry<String,HashMap<String,String>> pair = (HashMap.Entry<String,HashMap<String,String>>)it.next();
					HashMap<String,String> tempHash = targetData.get(pair.getKey());
					
					Iterator<Entry<String,String>> itInner = pair.getValue().entrySet().iterator();
					HashMap<String,String> innerResult = new HashMap<String,String>();
					while(itInner.hasNext())
					{
						HashMap.Entry<String,String> pairInner = (HashMap.Entry<String,String>)itInner.next();
						innerResult.put(pairInner.getKey()+"_SRC", pairInner.getValue());
						innerResult.put(pairInner.getKey()+"_TGT", tempHash.get(pairInner.getKey()));
						if (tempHash.get(pairInner.getKey()).compareTo(pairInner.getValue())==0)
						{
							//innerResult.put(pairInner.getKey()+"_SRC", pairInner.getValue());
							//innerResult.put(pairInner.getKey()+"_TGT", tempHash.get(pairInner.getKey()));
							innerResult.put(pairInner.getKey(),"MATCH");
						}
						else
						{
							//innerResult.put(pairInner.getKey()+"_SRC", pairInner.getValue());
							//innerResult.put(pairInner.getKey()+"_TGT", tempHash.get(pairInner.getKey()));
							innerResult.put(pairInner.getKey(),"MISMATCH");
						}
						
					}
					resultData.put(pair.getKey(), innerResult);
				}
			}
			else
			{
				Iterator<Entry<String,HashMap<String,String>>> it = sourceData.entrySet().iterator();
				int counter = 0;
				while(it.hasNext())
				{
					HashMap.Entry<String,HashMap<String,String>> pair = (HashMap.Entry<String,HashMap<String,String>>)it.next();
					HashMap<String,String> tempHash = targetData.get(pair.getKey());
					HashMap<String,String> tempHashSrc = sourceData.get(pair.getKey());
					HashMap<String,String> innerResult = new HashMap<String,String>();
					if(pair.getKey().contains("person"))
					System.out.println("Temp hash src :"+tempHashSrc);
					
					//System.out.println("Temp hash :"+tempHash+ " TEST:"+ pair.getKey().toString());
					//System.out.println("Temp hash Src:"+tempHashSrc+ " TEST:"+ pair.getKey().toString());

					for(int i=0;i<controlInfo.get(fileName).size();i++)
					{
						String processSource = "";
						String processTarget = "";
						List<String> cols = controlInfo.get(fileName).get(Integer.toString(i));
						
						//System.out.println("Cols:"+cols.get(1).toString()+ " A:" +cols.toString()+" size:"+cols.size());
						if (cols.get(1).toString().length()==0)
						{
							//System.out.println("0:"+cols.get(1)+" temphashSrc:"+tempHashSrc);
							innerResult.put(cols.get(0)+"_SRC", tempHashSrc.get(cols.get(0)));
						}
						else
						{
							processSource = ProcessFunctions.processPatternSQL(cols.get(1),tempHashSrc);
							innerResult.put(cols.get(0)+"_SRC", processSource);		
						}
						if (cols.get(3).toString().length()==0)
						{
							//System.out.println("2:"+cols.get(2)+" temphash:"+tempHash);
							try {
							innerResult.put(cols.get(2)+"_TGT", tempHash.get(cols.get(2)));
							}
							catch (Exception e) {
								// TODO Auto-generated catch block
								//System.out.println("Fail Rows:"+counter);
							}
						}
						else
						{
							processTarget = ProcessFunctions.processPatternSQL(cols.get(3),tempHash);
							innerResult.put(cols.get(2)+"_TGT", processTarget);		
						}
						try {
						if(processTarget.length()==0 && processSource.length()==0)
							if(tempHash.get(cols.get(2)).compareTo(tempHashSrc.get(cols.get(0)))==0) 
								innerResult.put(cols.get(0)+"_"+cols.get(2),"MATCH");
							else
								innerResult.put(cols.get(0)+"_"+cols.get(2),"MISMATCH");
						else if (processTarget.length()==0 && processSource.length()!=0)
							innerResult.put(cols.get(0)+"_"+cols.get(2),"MISMATCH");
						else if (processTarget.length()!=0 && processSource.length()==0)
							innerResult.put(cols.get(0)+"_"+cols.get(2),"MISMATCH");
						else 
							if(processSource.compareTo(processTarget)==0) 
								innerResult.put(cols.get(0)+"_"+cols.get(2),"MATCH");
							else
								innerResult.put(cols.get(0)+"_"+cols.get(2),"MISMATCH");
						}
						catch (Exception e )
						{
							//to do
						}
					}
					resultData.put(pair.getKey(), innerResult);
					counter++;
				}
					
			}
		}
	}
	public static void printResult(int type,String fileName) throws IOException
	{
		//File file = new File(resultPath+"\\"+fileName.replace(".ctl","")+"_result.txt");
		//FileOutputStream fos = new FileOutputStream(file);
		
		Writer bw = new PrintWriter(resultPath+"\\"+fileName.replace(".ctl","")+"_result.txt","UTF8");
		if(type ==1)
		{
			Iterator<Entry<String,HashMap<String,String>>> it2 = resultData.entrySet().iterator();
			boolean headRow = true;
			String header = "";
			while(it2.hasNext())
			{
				String printR ="";
				HashMap.Entry<String,HashMap<String,String>> pair = (HashMap.Entry<String,HashMap<String,String>>)it2.next();
				Map<String, String> treeMap = new TreeMap<String, String>(pair.getValue());
				for (Entry<String, String> entry : treeMap.entrySet()) {
		            if(headRow) 
					{
					    header = header + entry.getKey() + "\t";				
					}
					printR = printR+entry.getValue()+"\t";
		        }
				if(headRow)
				{
					bw.write("Key \t Data:\t"+ header);
					bw.write("\n");
					headRow = false;
				}
				bw.write(pair.getKey()+"\t Data:\t"+ printR);
				bw.write("\n");
				//System.out.println(pair.getKey()+" Data:"+ printR);
			}
		}
		else if(type ==2)
		{
			String header = "";
			List<String> seq = new ArrayList<String>();
			for(int i=0;i<controlInfo.get(fileName).size();i++)
			{
				List<String> cols = controlInfo.get(fileName).get(Integer.toString(i));
				seq.add(cols.get(0)+"_SRC");
				seq.add(cols.get(2) + "_TGT");
				seq.add(cols.get(0)+"_"+cols.get(2));
				header = header + cols.get(0) + "_SRC \t" + cols.get(2) + "_TGT \t" + cols.get(0)+"_"+cols.get(2) + "\t";
			}
			bw.write("Key \t Data:\t"+ header);
			bw.write("\n");
			Iterator<Entry<String,HashMap<String,String>>> it2 = resultData.entrySet().iterator();
			while(it2.hasNext())
			{
				String printR ="";
				HashMap.Entry<String,HashMap<String,String>> pair = (HashMap.Entry<String,HashMap<String,String>>)it2.next();
				Map<String, String> treeMap = new TreeMap<String, String>(pair.getValue());
				for(int i=0;i<seq.size();i++)
				{
					
						printR = printR+ treeMap.get(seq.get(i)) + "\t";
				}
				
				bw.write(pair.getKey()+"\t Data:\t"+ printR);
				bw.write("\n");
				//System.out.println(pair.getKey()+" Data:"+ printR);
			}
			
		}
		bw.close();
	}
	
	public static void clearMap()
	{
		sourceData.clear();
		targetData.clear();
		resultData.clear();
		controlInfo.clear();
		patternList.clear();
	}
}
