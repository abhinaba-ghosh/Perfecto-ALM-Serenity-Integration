package com.db.PerfectoReportAnalyser;

import java.io.File;
import java.net.URLDecoder;
import java.security.CodeSource;

import org.pmw.tinylog.Logger;
import org.testng.annotations.Test;

import com.sun.tools.javac.main.Main;



public class UnitTestPerfectoAnalyser {
	
	@Test
	public void loginTest() throws Exception{

		System.out.println("this step will unit test the perfecto package");
		
	//String per_res=PerfectoAPIHandler
		//.getPerectoReportByTagName("https://db-pwcc.reporting.perfectomobile.com/export/api/v1/test-executions","Nightly,Deutsche Bank Mobile");
//		//PerfectoReportParser.perfectoCreateTestCaseInTestPlan("", "39027", "-legacy--undefined-", "C-Medium", 
//				//"SIT", "MANUAL", "Design", "Internal", "abhinaba.ghosh", "Mobile Test Case","10465");
		

		File currentJavaJarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());   
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
		String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
		String decodedPath = URLDecoder.decode(currentRootDirectoryPath, "UTF-8");
		System.out.println("the jar location is:"+decodedPath);
	}
	
	
}
