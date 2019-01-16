package com.db.ALMMethodsImplementers;

import org.pmw.tinylog.Logger;

public class ALMTestSetCreator {
	
	
	
	public static String createTestSet(String parentFolderId, String TestSetName, String applicationName,
			String TestPhase, String TestStatus, String description) throws Exception {


		String responseString = "";

		String response_from_alm = ALMXMethodHelper.isResouceAlreadyAvailable(
				"test-sets?query={parent-id[=\"" + parentFolderId + "\"];name[=\"" + TestSetName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = prepareTestSetXml(parentFolderId, TestSetName, applicationName, TestPhase, TestStatus,
					description);
			String testset_id = ALMXMethodHelper.pushRequestMessageToALM(xmlContent, "test-sets");
			Logger.info("newly created test set id:" + testset_id);
			responseString = testset_id;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}
	
	public static String prepareTestSetXml(String parentFolderId, String TestSetName,String applicationName, String TestPhase, String TestStatus, String description) {
		
		String xmlContent="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<Entity Type=\"test-set\">\r\n" + 
				"    <Fields>\r\n" + 
				"        <Field Name=\"user-template-02\">\r\n" + 
				"            <Value>"+TestPhase+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"user-template-01\">\r\n" + 
				"            <Value>"+applicationName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"name\">\r\n" + 
				"            <Value>"+TestSetName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"status\">\r\n" + 
				"            <Value>"+TestStatus+"</Value>\r\n" + 
				"        </Field>  \r\n" + 
				"        <Field Name=\"subtype-id\">\r\n" + 
				"            <Value>hp.qc.test-set.default</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"parent-id\">\r\n" + 
				"            <Value>"+parentFolderId+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"description\">\r\n" + 
				"            <Value>"+description+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"    </Fields>\r\n" + 
				"    <RelatedEntities/>\r\n" + 
				"</Entity>";
		
		return xmlContent;
	}

}
