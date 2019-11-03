package com.wonderbuild.ematch.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Validation {
	
	public boolean fileExist(String fileName, String fileType)
	{
		if(fileType.compareToIgnoreCase("SRC")==0)
		{
			if(new File(InitProcess.sourcePath+"\\"+fileName).isFile())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (fileType.compareToIgnoreCase("TGT")==0)
		{
			if(new File(InitProcess.targetPath+"\\"+fileName).isFile())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	
	public boolean validateControl(File f)
	{
		String missingTag = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
	        doc.getDocumentElement().normalize();
	        if(doc.getElementsByTagName("sourcekey").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <sourcekey> tag;";
	        else if(doc.getElementsByTagName("sourcekey").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <sourcekey> tag;";
	        if(doc.getElementsByTagName("targetkey").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <targetkey> tag;";
	        else if(doc.getElementsByTagName("targetkey").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <targetkey> tag;";
	        if(doc.getElementsByTagName("sourcefile").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <sourcefile> tag;";
	        else if(doc.getElementsByTagName("sourcefile").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <sourcefile> tag;";
	        if(doc.getElementsByTagName("targetfile").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <targetfile> tag;";
	        else if(doc.getElementsByTagName("targetfile").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <targetfile> tag;";
	        if(doc.getElementsByTagName("sourcefiledelimiter").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <sourcefiledelimiter> tag;";
	        else if(doc.getElementsByTagName("sourcefiledelimiter").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <sourcefiledelimiter> tag;";
	        if(doc.getElementsByTagName("targetfiledelimiter").getLength()==0)
	        	missingTag = missingTag+ " Missing mandatory <targetfiledelimiter> tag;";
	        else if(doc.getElementsByTagName("targetfiledelimiter").item(0).getTextContent().length()<=0)
	        	missingTag = missingTag+ " No Data in <targetfiledelimiter> tag;";
	        NodeList colList = doc.getElementsByTagName("columnitem");
	        //System.out.println(doc.getElementsByTagName("columnitem").getLength());
	        if(doc.getElementsByTagName("columnitem").getLength()>0)
	        {
		        for(int i=0;i<colList.getLength();i++)
		        {
		        	Node attr = colList.item(i);
		        	if (attr.getNodeType() == Node.ELEMENT_NODE) {
	                 
		    			Element eElement = (Element) attr;
		    			if(eElement.getElementsByTagName("sourcecolumn").getLength()==0)
		    				missingTag = missingTag+ " Missing mandatory <sourcecolumn> tag;";
		    			else
		    				if(eElement.getElementsByTagName("sourcecolumn").item(0).getTextContent().length()<1)
		    					missingTag = missingTag+ " No Data <sourcecolumn> tag;";
		    			if(eElement.getElementsByTagName("sourcepattern").getLength()==0)
		    				missingTag = missingTag+ " Missing mandatory <sourcepattern> tag;";
		    			/*else
		    				if(eElement.getElementsByTagName("sourcepattern").item(0).getTextContent().length()<1)
		    					missingTag = missingTag+ " No Data <sourcepattern> tag;";*/
		    			if(eElement.getElementsByTagName("targetcolumn").getLength()==0)
		    				missingTag = missingTag+ " Missing mandatory <targetcolumn> tag;";
		    			else
		    				if(eElement.getElementsByTagName("targetcolumn").item(0).getTextContent().length()<1)
		    					missingTag = missingTag+ " No Data <targetcolumn> tag;";
		    			if(eElement.getElementsByTagName("targetpattern").getLength()==0)
		    				missingTag = missingTag+ " Missing mandatory <targetpattern> tag;";
		    			/*else
		    				if(eElement.getElementsByTagName("targetpattern").item(0).getTextContent().length()<1)
		    					missingTag = missingTag+ " No Data <targetpattern> tag;";*/
	 
	                    //innerList.add(eElement.getElementsByTagName("sourcepattern").item(0).getTextContent());
	                    //innerList.add(eElement.getElementsByTagName("targetcolumn").item(0).getTextContent());
	                    //innerList.add(eElement.getElementsByTagName("targetpattern").item(0).getTextContent());
	
		    		}
		        }
	        }
	        
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(missingTag.length() >0)
		{
			System.out.println(missingTag);
			return false;
		}
        
		return true;
	}
	
	public static boolean CheckParentesis(String str,String key)
	{
		String temp = "";
		List<String> tempList = new ArrayList<String>();
		int listseq = 0;
	    if (str.isEmpty())
	        return true;

	    Stack<Character> stack = new Stack<Character>();
	    for (int i = 0; i < str.length(); i++)
	    {
	        char current = str.charAt(i);
	       
	        if (current == '{' || current == '(' || current == '[')
	        {
	            stack.push(current);
	            if(tempList.isEmpty())
	 	        	tempList.add(listseq, current+"");
	 	        else
	 	        	tempList.set(listseq,tempList.get(listseq)+current);
	            listseq++;
	            
	        }
	        else if (current == '}' || current == ')' || current == ']')
	        {
	            if (stack.isEmpty())
	                return false;

	            char last = stack.peek();
	            if (current == '}' && last == '{' || current == ')' && last == '(' || current == ']' && last == '[')
	            {  
	            	stack.pop();
	            	if(tempList.isEmpty())
		 	        	tempList.add(listseq, current+"");
		 	        else
		 	        {
		 	        	temp = tempList.get(listseq-1);
		 	        	tempList.set(listseq-1,temp+current);
		 	        }
		            listseq--;
	                
	            }
	            else 
	                return false;
	        }
	        else
	        {
	        	if(tempList.isEmpty())
	 	        	tempList.add(listseq, current+"");
	 	        else
	 	        {
	 	        	if(tempList.size()<listseq+1)
	 	        		tempList.add(listseq,current+"");
	 	        	else	
	 	        	{
	 	        		temp = tempList.get(listseq);
	 	        		tempList.set(listseq,temp+current);
	 	        	}
	 	        }
	        }
	        
	        temp = "";

	    }
	    for(int i=0;i<tempList.size();i++)
	    {
	    	System.out.println("Seq:"+i+" Command:"+tempList.get(i));
	    }
	    InitProcess.setPatternList(key, tempList);
	    return stack.isEmpty();
	}
	
}
