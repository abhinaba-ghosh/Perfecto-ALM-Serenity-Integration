package components;

import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import infrastructure.ALMConnector;
import infrastructure.Constants;
import infrastructure.Response;
import infrastructure.RestConnector;
import schemas.wrapper.TestInstanceWrapper;

public class ALMTestInstanceCreator {

	public static String createTestInstance(String testsetId, String testCaseId, String tester, String executionStatus)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMCommonHelper.isResouceAlreadyAvailable("test-instances?query={cycle-id[=\""
				+ testsetId + "\"];test-id[=\"" + testCaseId + "\"] ");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = TestInstanceWrapper.prepareXml(testsetId, testCaseId, tester, executionStatus);
			String tesiInstanceid = ALMCommonHelper.pushRequestMessageToALM(xmlContent, "test-instances");
			Logger.info("newly created test-instance id:" + tesiInstanceid);
			responseString = tesiInstanceid;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}

	public static String updateTestInstanceStatus(String queryParam, String status,String execDate) throws Exception {

		String newlyCretedResourceId = "NO_ID_GENERATED";

		ALMConnector alm = new ALMConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(queryParam);

		String xmlContent = TestInstanceWrapper.prepareStatusXml(status,execDate);
		Logger.info("request  xml: " + xmlContent);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpPut(testconfigurl, xmlContent.getBytes(), requestHeaders);

		if ((postedEntityResponse.getStatusCode() == 200) || (postedEntityResponse.getStatusCode() == 201)) {

			newlyCretedResourceId = ALMCommonHelper.getNodeValueFromXmlString(postedEntityResponse.toString(), "id");

		} else {
			Logger.error("error response: " + postedEntityResponse.getFailure());
			Logger.error("error response full stack: " + postedEntityResponse.toString());

		}

		alm.logout();
		alm = null;

		return newlyCretedResourceId;
	}

}
