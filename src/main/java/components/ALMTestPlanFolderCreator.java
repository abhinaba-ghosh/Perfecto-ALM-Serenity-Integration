package components;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import schemas.wrapper.TestPlanFolder;

public class ALMTestPlanFolderCreator {

	public static String createTestFolderInTestPlan(String parnetFolderId, String TestFolderName, String description)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMCommonHelper.isResouceAlreadyAvailable(
				"test-folders?query={parent-id[=\"" + parnetFolderId + "\"];name=[=\"" + TestFolderName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {
			Logger.warn("Error response from alm: " + response_from_alm);
			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {
			String xmlContent = TestPlanFolder.prepareXml(parnetFolderId, TestFolderName, description);
			String test_folder_id = ALMCommonHelper.pushRequestMessageToALM(xmlContent, "test-folders");
			Logger.info("newly created test design step id " + test_folder_id);
			responseString = test_folder_id;
		} else {
			responseString = response_from_alm;
		}

		return responseString;
	}

}
