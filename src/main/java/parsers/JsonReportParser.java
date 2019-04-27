package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import components.ALMTestCaseCreator;
import components.ALMTestDesignStepCreator;
import components.ALMTestInstanceCreator;
import components.ALMTestPlanFolderCreator;
import components.ALMTestRunCreator;
import exceptions.ConnectorCustomException;
import helpers.ALMCommonHelper;
import helpers.SystemHelper;
import initializer.InitializationHandler;

@SuppressWarnings({ "unused", "unchecked" })
public class JsonReportParser extends InitializationHandler {

	private static String scenarioName;
	private static String serenityTestStatus;
	private static String driver;
	private static String serenityTestCaseId;
	private static String serenityRunName;
	private static String serenityExecutionDate;
	private static String serenityTestInstanceID;
	private static String serenityTestRunId;
	private static String updateStatusOfTestInstance;
	private static String serenityConfigId;
	private static String serenityExeDuration;
	private static String featureName;
	private static String narrative;
	private static String newlyCreatedtestPlanFolderId;
	private static String newlyCreatedTestCaseId;

	public static void serenityUpdateTestReportInALMWhileManualTestCasesArePresentWithID() throws Exception {

		List<File> files;

		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "json" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		} else {
			File temp_files = new File(test_report_location);
			files = Arrays.asList(temp_files);

		}

		Iterator<File> fileIterator = files.iterator();
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			if (test_arg.contains(file.getName())) {
				fileIterator.remove();
			}

		}

		for (File file : files) {

			Logger.info("File Name: " + file.getName());
			InputStream is = new FileInputStream(file.getCanonicalPath());
			String jsonTxt = IOUtils.toString(is, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonTxt);

			scenarioName = jsonObj.getString("name");
			serenityTestStatus = jsonObj.getString("result");
			serenityExeDuration = jsonObj.getString("duration");
			driver = jsonObj.getString("driver");

			Logger.info("name:" + scenarioName);
			serenityTestCaseId = getFunctionId(scenarioName);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
			LocalDateTime now = LocalDateTime.now();
			serenityRunName = "Run_" + dtf.format(now).toString();

			DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime nowExe = LocalDateTime.now();
			serenityExecutionDate = exeDate.format(nowExe).toString();

			if (serenityTestCaseId.contains(",")) {

				JSONArray c = jsonObj.getJSONArray("testSteps");
				String[] testCaseIdArray = serenityTestCaseId.split(",");
				for (int i = 0; i < c.length(); i++) {
					JSONObject obj = c.getJSONObject(i);
					serenityTestStatus = obj.getString("result");

					if (serenityTestStatus.equalsIgnoreCase("SUCCESS")) {
						serenityTestStatus = "Passed";
					} else if (serenityTestStatus.equalsIgnoreCase("ERROR")
							|| serenityTestStatus.equalsIgnoreCase("FAILURE")) {
						serenityTestStatus = "Failed";
					} else {
						serenityTestStatus = "N/A";
					}

					serenityTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, testCaseIdArray[i],
							testOwnerMailId, serenityTestStatus);
					Logger.info("test instance id creted/found:" + serenityTestInstanceID);

					/* create serenity Test Runs and attach url to the RunID */
					if (!serenityTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
							&& !serenityTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
							&& !serenityTestInstanceID.equalsIgnoreCase("")) {

						serenityTestRunId = ALMTestRunCreator.createTestRun(testSetId, testCaseIdArray[i],
								serenityTestInstanceID, driver, serenityExecutionDate, serenityTestStatus,
								testOwnerMailId, serenityRunName, driver, "hp.qc.run.MANUAL", serenityExeDuration);
						Logger.info("test run id creted/found:" + serenityTestRunId);

						if (testRunAttachmentType.equalsIgnoreCase("url")) {
							ALMCommonHelper.attachURLtoALMRunID(serenityTestRunId, testAttachment, serenityRunName);
						} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
							ALMCommonHelper.uploadFileAttachmentToTestRun(serenityTestRunId, testAttachment);
						}

						String queryParam = "test-instances/" + serenityTestInstanceID;
						updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
								serenityTestStatus, serenityExecutionDate);

					} else {
						Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
								+ serenityConfigId + "\n<<- Plesase recheck your parameters.\n"
								+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
								+ "Otherwise, your alm will be messed up.");
						break;
					}

				}

			} else {

				if (serenityTestStatus.equalsIgnoreCase("SUCCESS")) {
					serenityTestStatus = "Passed";
				} else if (serenityTestStatus.equalsIgnoreCase("ERROR")
						|| serenityTestStatus.equalsIgnoreCase("FAILURE")) {
					serenityTestStatus = "Failed";
				} else {
					serenityTestStatus = "N/A";
				}

				serenityTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, serenityTestCaseId,
						testOwnerMailId, serenityTestStatus);
				Logger.info("test instance id creted/found:" + serenityTestInstanceID);

				/* create serenity Test Runs and attach url to the RunID */
				if (!serenityTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
						&& !serenityTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
						&& !serenityTestInstanceID.equalsIgnoreCase("")) {

					serenityTestRunId = ALMTestRunCreator.createTestRun(testSetId, serenityTestCaseId,
							serenityTestInstanceID, driver, serenityExecutionDate, serenityTestStatus, testOwnerMailId,
							serenityRunName, driver, "hp.qc.run.MANUAL", serenityExeDuration);
					Logger.info("test run id creted/found:" + serenityTestRunId);

					if (testRunAttachmentType.equalsIgnoreCase("url")) {
						ALMCommonHelper.attachURLtoALMRunID(serenityTestRunId, testAttachment, serenityRunName);
					} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
						ALMCommonHelper.uploadFileAttachmentToTestRun(serenityTestRunId, testAttachment);
					}

					String queryParam = "test-instances/" + serenityTestInstanceID;
					updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
							serenityTestStatus, serenityExecutionDate);

				} else {
					Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
							+ serenityConfigId + "\n<<- Plesase recheck your parameters.\n"
							+ "CPlease send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
							+ "Otherwise, your alm will be messed up.");
					break;
				}

			}
		}

	}

	public static void serenityUpdateTestReportInALMWhileManualTestCasesArePresentWithName() throws Exception {

		List<File> files;

		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "json" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		} else {
			File temp_files = new File(test_report_location);
			files = Arrays.asList(temp_files);

		}

		Iterator<File> fileIterator = files.iterator();
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			if (test_arg.contains(file.getName())) {
				fileIterator.remove();
			}

		}
		for (File file : files) {

			Logger.info("File Name: " + file.getName());
			InputStream is = new FileInputStream(file.getCanonicalPath());
			String jsonTxt = IOUtils.toString(is, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonTxt);

			scenarioName = jsonObj.getString("name");
			serenityTestStatus = jsonObj.getString("result");
			serenityExeDuration = jsonObj.getString("duration");
			driver = jsonObj.getString("driver");

			Logger.info("name:" + scenarioName);
			serenityTestCaseId = ALMCommonHelper.isResouceAlreadyAvailable(
					"tests?query={parent-id[=\"" + testPlanFolderId + "\"];name[=\"" + scenarioName + "\"]}");

			if (serenityTestCaseId.equalsIgnoreCase("ERROR_OCCURED")
					|| serenityTestCaseId.equalsIgnoreCase("No_ID_FOUND")) {
				throw new ConnectorCustomException("Test Case Id not found for Test case Name:" + scenarioName
						+ " in test plan folder id:" + testPlanFolderId);
			}

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
			LocalDateTime now = LocalDateTime.now();
			serenityRunName = "Run_" + dtf.format(now).toString();

			DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime nowExe = LocalDateTime.now();
			serenityExecutionDate = exeDate.format(nowExe).toString();

			if (serenityTestStatus.equalsIgnoreCase("SUCCESS")) {
				serenityTestStatus = "Passed";
			} else if (serenityTestStatus.equalsIgnoreCase("ERROR") || serenityTestStatus.equalsIgnoreCase("FAILURE")) {
				serenityTestStatus = "Failed";
			} else {
				serenityTestStatus = "N/A";
			}

			serenityTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, serenityTestCaseId,
					testOwnerMailId, serenityTestStatus);
			Logger.info("test instance id creted/found:" + serenityTestInstanceID);

			/* create serenity Test Runs and attach url to the RunID */
			if (!serenityTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
					&& !serenityTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
					&& !serenityTestInstanceID.equalsIgnoreCase("")) {

				serenityTestRunId = ALMTestRunCreator.createTestRun(testSetId, serenityTestCaseId,
						serenityTestInstanceID, driver, serenityExecutionDate, serenityTestStatus, testOwnerMailId,
						serenityRunName, driver, "hp.qc.run.MANUAL", serenityExeDuration);
				Logger.info("test run id creted/found:" + serenityTestRunId);

				if (testRunAttachmentType.equalsIgnoreCase("url")) {
					ALMCommonHelper.attachURLtoALMRunID(serenityTestRunId, testAttachment, serenityRunName);
				} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
					ALMCommonHelper.uploadFileAttachmentToTestRun(serenityTestRunId, testAttachment);
				}

				String queryParam = "test-instances/" + serenityTestInstanceID;
				updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
						serenityTestStatus, serenityExecutionDate);

			} else {
				Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
						+ serenityConfigId + "\n<<- Plesase recheck your parameters.\n"
						+ "CPlease send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
						+ "Otherwise, your alm will be messed up.");
				break;
			}

		}

	}

	public static void serenityUpdateTestReportInALMWhileManualTestCasesAreNotPresent() throws Exception {

		List<File> files;

		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "json" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		} else {
			File temp_files = new File(test_report_location);
			files = Arrays.asList(temp_files);

		}
		Iterator<File> fileIterator = files.iterator();
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			if (test_arg.contains(file.getName())) {
				fileIterator.remove();
			}

		}
		for (File file : files) {

			Logger.info("File Name: " + file.getName());
			InputStream is = new FileInputStream(file.getCanonicalPath());
			String jsonTxt = IOUtils.toString(is, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonTxt);

			scenarioName = jsonObj.getString("name").replaceAll("\"", "");
			serenityTestStatus = jsonObj.getString("result");
			serenityExeDuration = jsonObj.getString("duration");
			driver = jsonObj.getString("driver");

			Logger.info("name:" + scenarioName);
			featureName = jsonObj.getJSONObject("userStory").getString("storyName");

			try {
				narrative = jsonObj.getJSONObject("userStory").getString("narrative");
			} catch (Exception e) {
				narrative = "";
				Logger.warn("Feature missing narrative");
			}

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
			LocalDateTime now = LocalDateTime.now();
			serenityRunName = "Run_" + dtf.format(now).toString();

			DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime nowExe = LocalDateTime.now();
			serenityExecutionDate = exeDate.format(nowExe).toString();

			newlyCreatedtestPlanFolderId = ALMTestPlanFolderCreator.createTestFolderInTestPlan(testPlanFolderId,
					featureName, narrative);

			Logger.info("new test feature folder id created/found:" + newlyCreatedtestPlanFolderId);

			newlyCreatedTestCaseId = ALMTestCaseCreator.createTestCaseInTestPlan(newlyCreatedtestPlanFolderId,
					scenarioName);

			Logger.info("new test id created/found:" + newlyCreatedtestPlanFolderId);

			generateDesignSteps(jsonObj, newlyCreatedTestCaseId);

			if (alm_test_set_link_creation.equalsIgnoreCase("Y")) {

				if (serenityTestStatus.equalsIgnoreCase("SUCCESS")) {
					serenityTestStatus = "Passed";
				} else if (serenityTestStatus.equalsIgnoreCase("ERROR")
						|| serenityTestStatus.equalsIgnoreCase("FAILURE")) {
					serenityTestStatus = "Failed";
				} else {
					serenityTestStatus = "N/A";
				}

				serenityTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, newlyCreatedTestCaseId,
						testOwnerMailId, serenityTestStatus);
				Logger.info("test instance id creted/found:" + serenityTestInstanceID);

				/* create serenity Test Runs and attach url to the RunID */
				if (!serenityTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
						&& !serenityTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
						&& !serenityTestInstanceID.equalsIgnoreCase("")) {

					serenityTestRunId = ALMTestRunCreator.createTestRun(testSetId, newlyCreatedTestCaseId,
							serenityTestInstanceID, driver, serenityExecutionDate, serenityTestStatus, testOwnerMailId,
							serenityRunName, driver, "hp.qc.run.MANUAL", serenityExeDuration);
					Logger.info("test run id creted/found:" + serenityTestRunId);

					if (testRunAttachmentType.equalsIgnoreCase("url")) {
						ALMCommonHelper.attachURLtoALMRunID(serenityTestRunId, testAttachment, serenityRunName);
					} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
						ALMCommonHelper.uploadFileAttachmentToTestRun(serenityTestRunId, testAttachment);
					}

					String queryParam = "test-instances/" + serenityTestInstanceID;
					updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
							serenityTestStatus, serenityExecutionDate);

				} else {
					Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
							+ serenityConfigId + "\n<<- Plesase recheck your parameters.\n"
							+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
							+ "Otherwise, your alm will be messed up.");
					break;
				}

			}

		}

	}

	private static void generateDesignSteps(JSONObject jsonObj, String newlyCreatedTestCaseId) throws Exception {

		JSONArray c = jsonObj.getJSONArray("testSteps");
		ALMCommonHelper.deleteResource("design-steps?query={parent-id[" + newlyCreatedTestCaseId + "]}");
		for (int i = 0; i < c.length(); i++) {
			JSONObject obj = c.getJSONObject(i);
			String designDescription = obj.getString("description");
			Logger.info("test design description:" + designDescription);
			ALMTestDesignStepCreator.createTestDesignInTestPlan(newlyCreatedTestCaseId, "Step " + (i + 1),
					designDescription);
		}

	}

	private static String getFunctionId(String scenarioName) {
		String temp_func_id = "";

		Matcher m = Pattern.compile("\\(ALM_TCID-([^]]+)\\)").matcher(scenarioName);
		if (m.find()) {
			temp_func_id = m.group(1).replaceAll(" ", "");
			Logger.info("Serenity Report creation: Received ALM TC id - " + temp_func_id);

		}

		return temp_func_id;

	}
}