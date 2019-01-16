package com.db.PerfectoReportAnalyser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.pmw.tinylog.Logger;

import com.db.ALMMethodsImplementers.ALMTestCaseCreator;
import com.db.ALMMethodsImplementers.ALMTestConfigurationCreator;
import com.db.ALMMethodsImplementers.ALMTestInstanceCreator;
import com.db.ALMMethodsImplementers.ALMTestRunCreator;
import com.db.ALMMethodsImplementers.ALMTestSetCreator;
import com.db.ALMMethodsImplementers.ALMXMethodHelper;

public class PerfectoReportParser {

	@SuppressWarnings("unused")
	public static void perfectoUpdateTestReportInALM(String perfectoJsonResponse, String parentFolderId,
			String applicationName, String testPriority, String TestPhase, String TestType, String TestStatus,
			String Classification, String owner, String Description, String TestSetParentFolderId) throws Exception {

		List<String> testcaseNameList = new ArrayList<>(new LinkedHashSet<>());

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = mapper.getJsonFactory();
		JsonParser jp = factory.createJsonParser(perfectoJsonResponse);

		JsonToken current;

		current = jp.nextToken();
		if (current != JsonToken.START_OBJECT) {
			Logger.error("Error: root should be object: quiting.");
			return;
		}

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldName = jp.getCurrentName();
			current = jp.nextToken();
			if (fieldName.equals("resources")) {
				if (current == JsonToken.START_ARRAY) {

					while (jp.nextToken() != JsonToken.END_ARRAY) {
						JsonNode root = jp.readValueAsTree();

						String TC_Name = "";
						String modelName = "";
						String osName = "";
						String deviceId = "";
						String deviceType = "";
						String manufacturerName = "";
						String osVersionName = "";
						String browserType = "";
						String browserVersion = "";
						String bowserInformation = "";
						String PerfectoAutomationOwner = "";
						String perfectoTestStatus = "";
						long executionStartTime = 0;
						long executionEndTime = 0;
						String perfectoRunName = "";
						String PerefctoExeDuration = "";
						String PerfectoRunName = "";
						String PerfectoRunHostName = "";
						String PerfectConfigName = "";
						String PerfectoExecutionDate = "";
						String perfectoReportUrl = "";

						String PerfectoTestCaseId = "";
						String PerfectoConfigId = "";
						String PerfectoTestSetId = "";
						String PerfectoTestInstanceID = "";
						String PerfectoTestRunId = "";
						String updateStatusOfTestInstance = "";

						TC_Name = root.get("name").getTextValue().replaceAll("[/:\"?'<>|*%&]", "");
						TC_Name = TC_Name.replaceAll(" ", "_");
						manufacturerName = root.findValue("manufacturer").asText();
						modelName = root.findValue("model").asText();
						osName = root.findValue("os").asText();
						osVersionName = root.findValue("osVersion").asText();
						deviceId = root.findValue("deviceId").asText();
						
						try {
							browserType = root.findValue("browserType").asText();
						} catch (Exception e) {
							browserType = "";
							Logger.warn("Browser type not defined");
						}

						try {
							browserVersion = root.findValue("browserVersion").asText();
						} catch (Exception e) {
							browserVersion = "";
							Logger.warn("Browser version not defined");
						}

						deviceType = root.findValue("deviceType").asText();
						PerfectoAutomationOwner = owner;
						perfectoTestStatus = root.findValue("status").asText();
						executionStartTime = Long.parseLong(root.findValue("startTime").asText());
						executionEndTime = Long.parseLong(root.findValue("endTime").asText());
						perfectoReportUrl = root.findValue("reportURL").asText();

						PerefctoExeDuration = Long
								.toString(TimeUnit.MILLISECONDS.toSeconds((executionEndTime - executionStartTime)));
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
						LocalDateTime now = LocalDateTime.now();
						PerfectoRunName = "Run_" + dtf.format(now).toString();

						DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						LocalDateTime nowExe = LocalDateTime.now();
						PerfectoExecutionDate = exeDate.format(nowExe).toString();

						if (browserVersion.isEmpty() && browserVersion.isEmpty()) {
							bowserInformation = "-";
						} else {
							bowserInformation = browserType + "_" + browserVersion;
						}

						PerfectoRunHostName = deviceId;
						PerfectConfigName = manufacturerName + "_" + modelName + " | " + bowserInformation + " | "
								+ osName + "_" + osVersionName;
						PerfectConfigName = PerfectConfigName.toUpperCase().replaceAll("\\+", "_PLUS");

						/* create Perfecto Test Case */
						PerfectoTestCaseId = ALMTestCaseCreator.createTestCaseInTestPlan(parentFolderId, TC_Name,
								applicationName, testPriority, TestPhase, TestType, TestStatus, Classification,
								PerfectoAutomationOwner, Description);

						/* create Perfecto Test Configuratons */
						if (!PerfectoTestCaseId.equalsIgnoreCase("ERROR_OCCURED")
								&& !PerfectoTestCaseId.equalsIgnoreCase("")) {
							Logger.info("Test case id created/found:" + PerfectoTestCaseId);

							PerfectoConfigId = ALMTestConfigurationCreator.createTestConfiguration(PerfectoTestCaseId,
									PerfectConfigName, PerfectoAutomationOwner,Description);
							testcaseNameList.add(TC_Name);
							Logger.info("test config id creted/found:" + PerfectoConfigId);

						} else {
							Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test case name ->>" + TC_Name
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Connect to abhinaba.ghosh@db.com or jamal.tikniouine@db.com for the quick review.\n"
									+ "Otherwise, your alm will be messed up.");
							break;
						}

						/* create Perfecto Test Sets */
						if (!PerfectoConfigId.equalsIgnoreCase("ERROR_OCCURED")
								&& !PerfectoConfigId.equalsIgnoreCase("NO_ID_GENERATED")
								&& !PerfectoConfigId.equalsIgnoreCase("")) {

							PerfectoTestSetId = ALMTestSetCreator.createTestSet(TestSetParentFolderId, TC_Name,
									applicationName, "SIT", "Closed", Description);
							Logger.info("test set id creted/found:" + PerfectoTestSetId);

						} else {
							Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test configuration ->>"
									+ PerfectConfigName + "<< TC NAME ->>" + TC_Name
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Connect to abhinaba.ghosh@db.com or jamal.tikniouine@db.com for the quick review.\n"
									+ "Otherwise, your alm will be messed up.");
							break;

						}

						/* create Perfecto Test Instances */
						if (!PerfectoTestSetId.equalsIgnoreCase("ERROR_OCCURED")
								&& !PerfectoTestSetId.equalsIgnoreCase("NO_ID_GENERATED")
								&& !PerfectoTestSetId.equalsIgnoreCase("")) {

							if (perfectoTestStatus.equalsIgnoreCase("PASSED")) {
								perfectoTestStatus = "Passed";
							}
							if (perfectoTestStatus.equalsIgnoreCase("FAILED")) {
								perfectoTestStatus = "Failed";
							}
							if (perfectoTestStatus.equalsIgnoreCase("USERABORTED")) {
								perfectoTestStatus = "Blocked";
							}
							if (perfectoTestStatus.equalsIgnoreCase("UNKNOWN")) {
								perfectoTestStatus = "N/A";
							}

							PerfectoTestInstanceID = ALMTestInstanceCreator.createTestInstance(PerfectoTestSetId,
									PerfectoTestCaseId, PerfectoConfigId, PerfectoAutomationOwner, perfectoTestStatus);
							Logger.info("test instance id creted/found:" + PerfectoTestInstanceID);

						} else {
							Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test set->>" + TC_Name
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Connect to abhinaba.ghosh@db.com or jamal.tikniouine@db.com for the quick review.\n"
									+ "Otherwise, your alm will be messed up.");
							break;
						}

						/* create Perfecto Test Runs and attach url to the RunID */
						if (!PerfectoTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !PerfectoTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !PerfectoTestInstanceID.equalsIgnoreCase("")) {

							if (perfectoTestStatus.equalsIgnoreCase("PASSED")) {
								perfectoTestStatus = "Passed";
							}
							if (perfectoTestStatus.equalsIgnoreCase("FAILED")) {
								perfectoTestStatus = "Failed";
							}
							if (perfectoTestStatus.equalsIgnoreCase("USERABORTED")) {
								perfectoTestStatus = "Blocked";
							}
							if (perfectoTestStatus.equalsIgnoreCase("UNKNOWN")) {
								perfectoTestStatus = "N/A";
							}

							PerfectoTestRunId = ALMTestRunCreator.createTestRun(PerfectoTestSetId, PerfectoTestCaseId,
									PerfectoConfigId, PerfectoTestInstanceID, deviceId, PerfectoExecutionDate,
									perfectoTestStatus, PerfectoAutomationOwner, PerfectoRunName, osName,
									"hp.qc.run.MANUAL", PerefctoExeDuration);
							Logger.info("test run id creted/found:" + PerfectoTestRunId);

							ALMXMethodHelper.attachURLtoALMRunID(PerfectoTestRunId, perfectoReportUrl, PerfectoRunName);

							String queryParam = "test-instances/" + PerfectoTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									perfectoTestStatus);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + PerfectoConfigId
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Connect to abhinaba.ghosh@db.com or jamal.tikniouine@db.com for the quick review.\n"
									+ "Otherwise, your alm will be messed up.");
							break;
						}

					}
					String delete_query = "";
					Set<String> testcaseNameList_tempSet = new HashSet<>();
					testcaseNameList_tempSet.addAll(testcaseNameList);
					testcaseNameList.clear();
					testcaseNameList.addAll(testcaseNameList_tempSet);
					for (String testcaseElement : testcaseNameList) {
						delete_query = "test-configs?query={name[=\"" + testcaseElement + "\"]}";
						String response = ALMXMethodHelper.deleteResource(delete_query);
						Logger.info("Delete test config response:"+response);
					}

				} else {
					Logger.error("Error: records should be an array: skipping.");
					jp.skipChildren();
				}
			} else {
				Logger.warn("Unprocessed property: " + fieldName);
				Logger.info("Perfecto Result Update Completed");
				jp.skipChildren();
			}
		}

	}

}
