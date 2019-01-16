package com.db.ALMMethodsImplementers;

import org.testng.annotations.Test;

public class UnitTestActivity {
	
	ALMTestConfigurationCreator acc = new ALMTestConfigurationCreator();
	ALMTestCaseCreator atc=new ALMTestCaseCreator();
	ALMXMethodHelper almhlp=new ALMXMethodHelper();
	
	@Test
	public void loginTest() throws Exception{

		System.out.println("this step will enter the username and password");
		
		//acc.createTestConfiguration( "289708","jamal-ios2", "abhinaba.ghosh");
		//atc.createTestCaseInTestPlan("28532", "Test79", "-legacy--undefined-", "C-Medium", "CIT","MANUAL" , "Design", "Internal", "abhinaba.ghosh", "");
		//ALMTestSetCreator.createTestSet("6628", "TestSet-009", "-legacy--undefined-", "CIT", "Open", "");
		//String res=ALMXMethodHelper.getResponse("tests?query={parent-id[=\"39027\"]}");
		//ALMTestInstanceCreator.createTestInstance("16502", "289708","292637", "hp.qc.test-instance.MANUAL", "abhinaba.ghosh");
//		ALMXMethodHelper.uploadFileAttachmentToTestRun("151487", "C:\\Users\\ghosabhc\\Dev_Room\\README.md");
		//String a="galaxy9+&_mobile";
		//ALMXMethodHelper.attachURLtoALMRunID("151487", "https://www.db.com", "Newest URL");
		//System.out.println("url is:"+a.replaceAll("[\\+\\&]", "PLUS"));

	            
		
	
	}
	

}
