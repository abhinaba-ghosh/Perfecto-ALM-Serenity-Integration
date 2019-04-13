package components;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import schemas.wrapper.TestSetWrapper;

public class ALMTestSetCreator {

	public static String createTestSet(String parentFolderId, String TestSetName, String applicationName,
			String TestPhase, String TestStatus, String description) throws Exception {

		String responseString = "";

		String response_from_alm = ALMCommonHelper.isResouceAlreadyAvailable(
				"test-sets?query={parent-id[=\"" + parentFolderId + "\"];name[=\"" + TestSetName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = TestSetWrapper.prepareXml(parentFolderId, TestSetName, applicationName, TestPhase,
					TestStatus, description);
			String testset_id = ALMCommonHelper.pushRequestMessageToALM(xmlContent, "test-sets");
			Logger.info("newly created test set id:" + testset_id);
			responseString = testset_id;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}

}
