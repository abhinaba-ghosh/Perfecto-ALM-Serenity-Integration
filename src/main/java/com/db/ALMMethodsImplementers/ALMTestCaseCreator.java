package com.db.ALMMethodsImplementers;

import org.pmw.tinylog.Logger;

public class ALMTestCaseCreator {

	public static String createTestCaseInTestPlan(String parentFolderId, String TestCaseName, String applicationName,
			String testPriority, String TestPhase, String TestType, String TestStatus, String Classification,
			String owner, String Description) throws Exception {

		String responseString="";
		
		String response_from_alm = ALMXMethodHelper.isResouceAlreadyAvailable(
				"tests?query={parent-id[=\"" + parentFolderId + "\"];name[=\"" + TestCaseName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString="ERROR_OCCURED";
		} else if(response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = prepareTestCaseXml(parentFolderId, TestCaseName, applicationName, testPriority,
					TestPhase, TestType, TestStatus, Classification, owner, Description);
			String test_id = ALMXMethodHelper.pushRequestMessageToALM(xmlContent, "tests");
			Logger.info("newly created test id:" + test_id);
			responseString=test_id;
		}else {
			responseString=response_from_alm;
		}
		
		return responseString;
	}

	/**
	 * Function Name: createTestCaseCml input params demo value: parentFolderId:
	 * 28532 (represents the folder:
	 * tds://pcc_ger.alm_cross_domain.alm12.intranet.db.com/qcbin/[AnyModule]?EntityType=ITestFolder&EntityID=28532
	 * )
	 */

	public static String prepareTestCaseXml(String parentFolderId, String TestCaseName, String applicationName,
			String testPriority, String TestPhase, String TestType, String TestStatus, String Classification,
			String owner, String Description) {

		String xmlbody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n"
				+ "<Entity Type=\"test\">\r\n" + "    <ChildrenCount>\r\n" + "        <Value>0</Value>\r\n"
				+ "    </ChildrenCount>\r\n" + "    <Fields>  \r\n" + "        <Field Name=\"user-template-02\">\r\n"
				+ "            <Value>" + TestPhase + "</Value>\r\n" + "        </Field>\r\n"
				+ "        <Field Name=\"user-template-01\">\r\n" + "            <Value>" + applicationName
				+ "</Value>\r\n" + "        </Field>\r\n" + "        <Field Name=\"user-template-06\">\r\n"
				+ "            <Value>" + testPriority + "</Value>\r\n" + "        </Field>\r\n"
				+ "        <Field Name=\"status\">\r\n" + "            <Value>" + TestStatus + "</Value>\r\n"
				+ "        </Field>\r\n" + "       <Field Name=\"description\">\r\n" + "            <Value>"
				+ Description + "</Value>\r\n" + "        </Field>\r\n" + "        <Field Name=\"parent-id\">\r\n"
				+ "            <Value>" + parentFolderId + "</Value>\r\n" + "        </Field>\r\n"
				+ "        <Field Name=\"owner\">\r\n" + "            <Value>" + owner + "</Value>\r\n"
				+ "        </Field>\r\n" + "        <Field Name=\"name\">\r\n" + "            <Value>" + TestCaseName
				+ "</Value>\r\n" + "        </Field>      \r\n" + "        <Field Name=\"user-template-24\">\r\n"
				+ "            <Value>" + Classification + "</Value>\r\n" + "        </Field>\r\n"
				+ "        <Field Name=\"subtype-id\">\r\n" + "            <Value>" + TestType + "</Value>\r\n"
				+ "        </Field>\r\n" + "    </Fields>\r\n" + "    <RelatedEntities/>\r\n" + "</Entity>";

		return xmlbody;
	}

}
