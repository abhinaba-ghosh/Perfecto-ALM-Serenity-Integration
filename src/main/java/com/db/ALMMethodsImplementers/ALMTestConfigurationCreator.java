package com.db.ALMMethodsImplementers;

import org.pmw.tinylog.Logger;

public class ALMTestConfigurationCreator {
	

	
	public static String createTestConfiguration(String parentTestId, String configName, String owner,String description)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMXMethodHelper.isResouceAlreadyAvailable(
				"test-configs/?query={parent-id[=\"" + parentTestId + "\"];name[=\"" + configName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = prepareConfigurationXml(parentTestId, configName, owner,description);
			String config_id = ALMXMethodHelper.pushRequestMessageToALM(xmlContent, "test-configs");
			Logger.info("new test config id is:" + config_id);
			responseString = config_id;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}
	
	
	
	public static String  prepareConfigurationXml(String parentTestId, String configName, String owner,String description) {
		
		String XmlBody="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<Entity Type=\"test-config\">\r\n" + 
				"\r\n" + 
				"    <Fields>\r\n" + 
				"        <Field Name=\"parent-id\">\r\n" + 
				"            <Value>"+parentTestId+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"data-state\">\r\n" + 
				"            <Value>0</Value>\r\n" + 
				"        </Field>\r\n" + 
				"        <Field Name=\"name\">\r\n" + 
				"            <Value>"+configName+"</Value>\r\n" + 
				"        </Field>\r\n" + 
				"      	<Field Name=\"owner\">\r\n" + 
				"            <Value>"+owner+"</Value>\r\n" + 
				"        </Field>\r\n"+
				"		 <Field Name=\"description\">\r\n" + 
				"             <Value>"+description+"</Value>\r\n" + 
				"         </Field>\r\n" + 
				"    </Fields>\r\n" + 
				"    <RelatedEntities/>\r\n" + 
				"</Entity>";
		return XmlBody;
	}
	
	
	

}
