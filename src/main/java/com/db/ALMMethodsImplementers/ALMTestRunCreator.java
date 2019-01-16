package com.db.ALMMethodsImplementers;

import org.pmw.tinylog.Logger;

public class ALMTestRunCreator {
	
	
	
	public static String createTestRun(String cycleID,String testID,String testConfigId,String testCycleId,String hostName,String executionDate,
			String status,String owner, String runName, String OSName, String subTypeID, String runDuration) throws Exception {
		
		String xmlContent= createTestRunXml(cycleID,testID,testConfigId,testCycleId,hostName,executionDate,
				status,owner,runName,OSName,subTypeID,runDuration);
		String testset_id=ALMXMethodHelper.pushRequestMessageToALM(xmlContent, "runs");
		Logger.info("newly created test run id is:"+testset_id);
		return testset_id;
		
	}
	
	
	public static String createTestRunXml(String cycleID,String testID,String testConfigId,String testCycleId, String hostName,String executionDate,
			String status,String owner, String runName, String OSName, String subTypeID, String runDuration) {
		
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<Entity Type=\"run\">\r\n" + 
				"    <Fields>\r\n" + 
				"        <Field Name=\"execution-date\">\r\n" + 
				"            <Value>"+executionDate+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"user-template-02\">\r\n" + 
				"            <Value>Internal</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"cycle-id\">\r\n" + 
				"            <Value>"+cycleID+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"host\">\r\n" + 
				"            <Value>"+hostName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"status\">\r\n" + 
				"            <Value>"+status+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"test-id\">\r\n" + 
				"            <Value>"+testID+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"owner\">\r\n" + 
				"            <Value>"+owner+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"test-config-id\">\r\n" + 
				"            <Value>"+testConfigId+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"name\">\r\n" + 
				"            <Value>"+runName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"testcycl-id\">\r\n" + 
				"            <Value>"+testCycleId+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"os-name\">\r\n" + 
				"            <Value>"+OSName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"subtype-id\">\r\n" + 
				"            <Value>"+subTypeID+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"duration\">\r\n" + 
				"            <Value>"+runDuration+"</Value>\r\n" + 
				"        </Field>\r\n"+ 
				"    </Fields>\r\n" + 
				"    <RelatedEntities/>\r\n" + 
				"</Entity>";
		
		return xml;
	}
	

}






