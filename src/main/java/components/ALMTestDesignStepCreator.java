package components;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import schemas.wrapper.TestDesignWrapper;

public class ALMTestDesignStepCreator {

	public static String createTestDesignInTestPlan(String parnetTestCaseId, String stepName, String description)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMCommonHelper
				.isResouceAlreadyAvailable("design-steps?query={parent-id[=\"" + parnetTestCaseId + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {
			Logger.warn("Error response from alm: " + response_from_alm);
			responseString = "ERROR_OCCURED";
		} else {
			String xmlContent = TestDesignWrapper.prepareXml(parnetTestCaseId, stepName, description);
			String test_design_id = ALMCommonHelper.pushRequestMessageToALM(xmlContent, "design-steps");
			Logger.info("newly created test design step id " + test_design_id);
			responseString = test_design_id;
		}

		return responseString;
	}

}
