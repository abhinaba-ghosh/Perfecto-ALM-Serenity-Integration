package com.db.ALMMethodsImplementers;

import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import com.db.ALMInfrastructure.AlmConnector;
import com.db.ALMInfrastructure.Constants;
import com.db.ALMInfrastructure.Response;
import com.db.ALMInfrastructure.RestConnector;

public class ALMTestInstanceCreator {
	
	
	public static String createTestInstance(String testsetId, String testId, String testConfigId, String tester,String executionStatus)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMXMethodHelper.isResouceAlreadyAvailable(
				"test-instances?query={cycle-id[=\""+testsetId+"\"];test-id[=\""+testId+"\"];test-config-id[=\""+testConfigId+"\"]} ");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = createTestInstanceXml(testsetId, testId, testConfigId, tester,executionStatus);
			String tesiInstanceid = ALMXMethodHelper.pushRequestMessageToALM(xmlContent, "test-instances");
			Logger.info("newly created test-instance id:" + tesiInstanceid);
			responseString = tesiInstanceid;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}
	
	
	public static String updateTestInstanceStatus(String queryParam, String status) throws Exception {

		String newlyCretedResourceId = "NO_ID_GENERATED";

		AlmConnector alm = new AlmConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(queryParam);
		//System.out.println("testconfig url:" + testconfigurl);

		String xmlContent = prepareStatusUpdateXml(status);
		Logger.info("request  xml: " + xmlContent);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpPut(testconfigurl, xmlContent.getBytes(), requestHeaders);

		
		//System.out.println("postedEntityResponse.getStatusCode():" + postedEntityResponse.getStatusCode());

		if ((postedEntityResponse.getStatusCode() == 200) || (postedEntityResponse.getStatusCode() == 201)) {

			newlyCretedResourceId = ALMXMethodHelper.getNodeValueFromXmlString(postedEntityResponse.toString(), "id");
			//System.out.println("Newly created resource id is:" + newlyCretedResourceId);

		} else {
			Logger.error("error response: " + postedEntityResponse.getFailure());
			Logger.error("error response full stack: " + postedEntityResponse.toString());

		}

		alm.logout();
		alm = null;

		return newlyCretedResourceId;
	}
	
	private static String prepareStatusUpdateXml(String status) {
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"    <Entity Type=\"test-instance\">\r\n" + 
				"        <Fields>\r\n" + 
				"			<Field Name=\"status\">\r\n" + 
				"                <Value>"+status+"</Value>\r\n" + 
				"            </Field>        </Fields>\r\n" + 
				"        \r\n" + 
				"    </Entity>";
		
		return xml;
	}


	public static String  createTestInstanceXml(String testsetId, String testId,String testConfigId,String tester, String executionStatus) {
		
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"    <Entity Type=\"test-instance\">\r\n" + 
				"        <Fields>\r\n" + 
				"            <Field Name=\"cycle-id\">\r\n" + 
				"                <Value>"+testsetId+"</Value>\r\n" + 
				"            </Field>\r\n" + 
				"            <Field Name=\"actual-tester\">\r\n" + 
				"                <Value>"+tester+"</Value>\r\n" + 
				"            </Field>\r\n" + 
				"            <Field Name=\"test-id\">\r\n" + 
				"                <Value>"+testId+"</Value>\r\n" + 
				"            </Field>\r\n" +
				"            <Field Name=\"test-config-id\">\r\n" + 
				"                <Value>"+testConfigId+"</Value>\r\n" + 
				"            </Field>"+
				"            <Field Name=\"subtype-id\">\r\n" + 
				"                <Value>hp.qc.test-instance.MANUAL</Value>\r\n" + 
				"            </Field>\r\n" + 
				"            <Field Name=\"owner\">\r\n" + 
				"                <Value>"+tester+"</Value>\r\n" + 
				"            </Field>\r\n" + 
				"			<Field Name=\"status\">\r\n" + 
						"                <Value>"+executionStatus+"</Value>\r\n" + 
						"            </Field>"+
				"        </Fields>\r\n" + 
				"        <RelatedEntities/>\r\n" + 
				"    </Entity>";
		
		return xml;
	}
	
	
	

}
