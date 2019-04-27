package components;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import schemas.wrapper.TestRun;

public class ALMTestRunCreator {
	
	
	
	public static String createTestRun(String cycleID,String testID,String testCycleId,String hostName,String executionDate,
			String status,String owner, String runName, String OSName, String subTypeID, String runDuration) throws Exception {
		
		String xmlContent= TestRun.prepareXml(cycleID,testID,testCycleId,hostName,executionDate,
				status,owner,runName,OSName,subTypeID,runDuration);
		String test_run_id=ALMCommonHelper.pushRequestMessageToALM(xmlContent, "runs");
		Logger.info("newly created test run id is:"+test_run_id);
		return test_run_id;
		
	}

}






