package com.wonderbuild.ematch.intial;

import java.io.File;

import com.wonderbuild.ematch.functions.InitProcess;
import com.wonderbuild.license.CheckLicense;

public class Prep {

	public static void checkPath()
	{
		if(!new File(InitProcess.controlPath).exists())
		{
			new File(InitProcess.controlPath).mkdirs();
		}
		if(!new File(InitProcess.sourcePath).exists())
		{
			new File(InitProcess.sourcePath).mkdirs();
		}
		if(!new File(InitProcess.targetPath).exists())
		{
			new File(InitProcess.targetPath).mkdirs();
		}
		if(!new File(InitProcess.resultPath).exists())
		{
			new File(InitProcess.resultPath).mkdirs();
		}
	}
	
	public static boolean checkLicense()
	{
		CheckLicense chk = new CheckLicense();
		return chk.process(new File("").getAbsolutePath()+"\\roots");
	}
}
