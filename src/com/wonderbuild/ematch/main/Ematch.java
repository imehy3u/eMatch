package com.wonderbuild.ematch.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.wonderbuild.ematch.functions.InitProcess;
import com.wonderbuild.ematch.functions.ProcessFunctions;
import com.wonderbuild.ematch.functions.Validation;

public class Ematch {
  public static void main(String[] args) {

	  Date date1 = null ;
	  Date date2 = new Date();
	  try {
		date1 = new SimpleDateFormat("ddMMyyyy").parse(new String("31122018"));
	  } catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
	  }

	  try {
		  FileReader consoleHeader = new FileReader(InitProcess.absolutePath+"//splash.txt");
		  @SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(consoleHeader);
		  String line = "";
		  while((line= br.readLine())!= null)
			  System.out.println(line);
		  
	  } catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  if(date2.before(date1))
	  {
		  do
		  {
			System.out.println("Menu");
			System.out.println("1. Start");
			System.out.println("2. Exit");
			System.out.println("Please select an option:");  
		  }while(userInput());	  
		 	 	  
		  ProcessFunctions.closeConnect();
	  }
	  else
	  {
		  System.out.println("Invalid License");
	  }
	  System.out.println("End Time:"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
	  System.exit(0);
  }
  
  public static void startProcess()
  {
	  ProcessFunctions processFunction = new ProcessFunctions();
	  Validation validate = new Validation();
	  File f = new File(InitProcess.controlPath);
	  File[] matchingFiles = f.listFiles(new FilenameFilter() {
		   public boolean accept(File dir, String name) {
		       return name.toLowerCase().endsWith("ctl");
		    }
	  });
	  
	  if (matchingFiles.length >0)
	  {
		  for(int i=0;i<matchingFiles.length;i++)
		  {
			  if(validate.validateControl(new File(InitProcess.controlPath+"\\"+matchingFiles[i].getName())))
			  {
				  String fileName = matchingFiles[i].getName();
				  System.out.println("Start Processing:"+fileName);
				  String sourceDelimiter = ProcessFunctions.getControlFileKey(fileName, "sourcefiledelimiter");
				  String targetDelimiter = ProcessFunctions.getControlFileKey(fileName, "targetfiledelimiter");
				  boolean printType = processFunction.getControlFileColInfo(fileName, "", "");
				  processFunction.readCSV(fileName,InitProcess.sourcePath,sourceDelimiter);
				  processFunction.readCSV(fileName,InitProcess.targetPath,targetDelimiter);
				  System.out.println( "target:" + InitProcess.targetData.size());
				  try {
					InitProcess.setResult(fileName);
					if(!printType)
						InitProcess.printResult(2,fileName);
					else
						InitProcess.printResult(1,fileName);
				  } catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("IO Exception:"+e.toString());
				  }
				  System.out.println("Process "+matchingFiles[i].getName()+" Done");
			  }
			  
			  InitProcess.clearMap();
		  }                                                                                                                                                      
		  
		  System.out.println("Completed.");
	  }
	  else
	  {
		  System.out.println("No Control File in Control folder.");
	  }
  }
  
  public static boolean userInput()
  {
	@SuppressWarnings("resource")
	Scanner sc = new Scanner(System.in);
	String i = sc.nextLine();
	if (i.compareToIgnoreCase(new String("1"))==0)
	{
	  startProcess();
	  return true;
	}
	else if (i.compareToIgnoreCase(new String("2"))==0)
	{
	  return false;
	}
 	else
 	{
	  System.out.print("Please enter the correct options.");
	  return true;
 	}  
  }
}
