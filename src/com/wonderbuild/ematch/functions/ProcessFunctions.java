package com.wonderbuild.ematch.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcessFunctions {
	
	static Connection conn = null;
	
	public boolean readCSV(String fileName, String filePath, String delimiter)
	{
		String rawData = "";
		String[] headerRow = null;
		int keyCol = 0;
		Boolean header = true;
		String keyColName = getControlFileKey(fileName,"sourcekey");
		String dataFileName = getControlFileKey(fileName,"sourcefile");
		if(!filePath.contains("Source Data"))
		{
			keyColName = getControlFileKey(fileName,"targetkey");
			dataFileName = getControlFileKey(fileName,"targetfile");
		}
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(filePath+'\\'+dataFileName));
			int delimiterCnt = 0;
			int rowCnt = 1;
			while ((rawData = br.readLine()) != null)
			{
			  if(header)
			  {
				  header = false;
				  delimiterCnt = rawData.split(Pattern.quote(delimiter),-1).length;
				  headerRow = rawData.split(Pattern.quote(delimiter),-1);
				  if (filePath.contains("Source Data"))
				  {
					  InitProcess.setHeader(rawData);
				  }
				  for(int i =0 ;i<headerRow.length;i++)
				  {
					  if(headerRow[i].equalsIgnoreCase(keyColName))
					  {
						  keyCol = i;
						  break;
					  }
				  }
			  }
			  else
			  {
				  if(delimiterCnt !=rawData.split(Pattern.quote(delimiter),-1).length)
				  {
					  System.out.println("Mismatch is row delimiter from header: Row"+rowCnt+" From: "+filePath);
					  return false;
				  }
				  String[] csvData = rawData.split(Pattern.quote(delimiter),-1);
				  HashMap<String,String> inner = new HashMap<String,String>();
				  for(int i = 0;i<csvData.length;i++)
				  {
					  inner.put(headerRow[i], csvData[i]);
				  } 
				  if(filePath.contains("Source Data"))
					  InitProcess.setSourceData(csvData[keyCol],inner);
				  else
					  InitProcess.setTargetData(csvData[keyCol],inner);
			  }
			  rowCnt++;
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static String getControlFileKey(String fileName,String tagName)
	{ 
		String keyCol = "";
		File f = new File(InitProcess.absolutePath+"\\Control\\"+fileName);
		
		
	    
		//if (matchingFiles.length >0) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		        Document doc = dBuilder.parse(f);//matchingFiles[0]
		        doc.getDocumentElement().normalize();
		        keyCol = doc.getElementsByTagName(tagName).item(0).getTextContent();
		        if(tagName.compareToIgnoreCase("targetfiledelimiter")==0 ||tagName.compareToIgnoreCase("sourcefiledelimiter")==0)
		        {
		        	if(keyCol.compareToIgnoreCase("TAB")==0)
		        	{
		        		keyCol = "\t";
		        	}
		        	else if(keyCol.compareToIgnoreCase("SPACE")==0)
		        	{
		        		keyCol = " ";
		        	}
		        }
			} 
			catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e) {
				//e.printStackTrace();
				return null;
			}
		//}
		return keyCol;
	}
	
	public boolean getControlFileColInfo(String fileName, String fileType, String tagName)
	{
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(new File(InitProcess.controlPath+"\\"+fileName));
	        doc.getDocumentElement().normalize();
	        //keyCol = doc.getElementsByTagName(tagName).item(0).getTextContent();
	        NodeList colList = doc.getElementsByTagName("columnitem");
	        if(colList.getLength()>0)
	        {
	        	HashMap<String,List<String>> inner = new HashMap<String,List<String>>();
		        for(int i=0;i<colList.getLength();i++)
		        {
		        	Node attr = colList.item(i);
		        	if (attr.getNodeType() == Node.ELEMENT_NODE) 
		        	{
	                    if(!InitProcess.controlInfo.containsKey(fileName))
	                    {
	                    	InitProcess.controlInfo.get(fileName);
	                    }
		    			Element eElement = (Element) attr;
	                    
	                    List<String> innerList = new ArrayList<String>();
	                    innerList.add(eElement.getElementsByTagName("sourcecolumn").item(0).getTextContent());
	                    innerList.add(eElement.getElementsByTagName("sourcepattern").item(0).getTextContent());
	                    innerList.add(eElement.getElementsByTagName("targetcolumn").item(0).getTextContent());
	                    innerList.add(eElement.getElementsByTagName("targetpattern").item(0).getTextContent());
	                    inner.put(Integer.toString(i), innerList);
		    		}
		        }
		        InitProcess.setControlInfo(fileName, inner);
	        }
	        else
	        {
	        	return false;
	        }
		} 
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String processPatternRegex(String pattern,String data,String fileName,String colName)
	{
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(data);
		if (m.find())
		{
			return m.group(0);
		}
		else
			return "";
		
	}
	
	/*public String processPattern(String pattern,String data,String fileName,String colName)
	{
		if(Validation.CheckParentesis(pattern,fileName))
		{
			List<String> patterns = InitProcess.patternList.get(fileName);
			if(patterns.isEmpty())
			{
				return "";
			}
			else
			{
				String[] tempVar;
				int tempNum ;
				for(int i=patterns.size()-1;i>=0;i--)
				{
					if(patterns.get(i).contains("SUBSTRING"))
					{
						String [] temp = patterns.get(i).split(",");
						temp[0]
					}
					else if(patterns.get(i).contains("INSTR"))
					{
						if(pattern.contains(","))
						{
							//String[] temp = patterns.get(i).split(",");
							
							tempNum = data.indexOf(tempVar);
							tempVar.replaceAll("INSTR()", tempNum+"");
						}
					}
					else
					{
						tempVar = patterns.get(i).split(",");
						for(int j = 0;j<tempVar.length;j++)
						{
							tempVar[j].replaceAll(colName, data);
						}
					}
				}
			}
		}
		return "";
	}*/
	
	public static String processPatternSQL(String pattern,HashMap<String,String> data)
	{
		
		String Results = "";
		try{
			if (conn == null)
				connectSQL();
			String sql = "SELECT "+pattern + " AS RESULTS";
			Iterator<Entry<String,String>> it = data.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, String> pair = it.next();
				//System.out.println("pair.getKey():"+pair.getKey()+" pair.getValue().toString():"+pair.getValue().toString());
				String temp = ";"+pair.getKey();
				sql = sql.replaceAll(temp, "\'"+pair.getValue()+"\'");
			}
			//System.out.println("SQL:"+sql);
			//System.out.println("Connection to SQLite has been established.");
			Statement stmt  = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
				Results = Results +rs.getString("RESULTS");
			
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return Results;
	}
	
	public static void connectSQL()
	{
		String url = "jdbc:sqlite:memory";
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void closeConnect()
	{
		try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
	}

}
