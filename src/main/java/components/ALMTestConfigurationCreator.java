package components;

import org.pmw.tinylog.Logger;

import helpers.ALMCommonHelper;
import schemas.wrapper.TestConfig;

public class ALMTestConfigurationCreator {
	

	
	public static String createTestConfiguration(String parentTestId, String configName, String owner,String description)
			throws Exception {

		String responseString = "";

		String response_from_alm = ALMCommonHelper.isResouceAlreadyAvailable(
				"test-configs/?query={parent-id[=\"" + parentTestId + "\"];name[=\"" + configName + "\"]}");
		if (response_from_alm.equalsIgnoreCase("ERROR_OCCURED")) {

			responseString = "ERROR_OCCURED";
		} else if (response_from_alm.equalsIgnoreCase("No_ID_FOUND")) {

			String xmlContent = TestConfig.prepareXml(parentTestId, configName, owner,description);
			String config_id = ALMCommonHelper.pushRequestMessageToALM(xmlContent, "test-configs");
			Logger.info("new test config id is:" + config_id);
			responseString = config_id;
		} else {
			responseString = response_from_alm;
		}

		return responseString;

	}
	
	

}
