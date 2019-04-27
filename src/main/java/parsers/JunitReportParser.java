package parsers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.pmw.tinylog.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import components.ALMTestCaseCreator;
import components.ALMTestInstanceCreator;
import components.ALMTestPlanFolderCreator;
import components.ALMTestRunCreator;
import exceptions.ConnectorCustomException;
import helpers.ALMCommonHelper;
import helpers.SystemHelper;
import initializer.InitializationHandler;

@SuppressWarnings({ "unused", "unlikely-arg-type","unchecked" })
public class JunitReportParser extends InitializationHandler {

	private static String scenarioName;
	private static String junitTestStatus;
	private static String junitRunName;
	private static String junitExeDuration;
	private static String junitExecutionDate = "23";
	private static String driver = "chrome";

	private static String junitTestCaseId;
	private static String junitConfigId;
	private static String junitTestInstanceID;
	private static String junitTestRunId;
	private static String updateStatusOfTestInstance;
	private static String featureName;
	private static String newlyCreatedtestPlanFolderId;
	private static String newlyCreatedTestCaseId;

	public static void junitUpdateTestReportInALMWhileManualTestCasesArePresentWithID() throws Exception {

		List<File> files;
		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "xml" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			files.remove(test_arg);
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

			File inputFile = new File(file.getCanonicalPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("testcase");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");

					Logger.info("name:" + scenarioName);
					junitTestCaseId = getFunctionId(scenarioName);

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					junitRunName = "Run_" + dtf.format(now).toString();

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					junitExecutionDate = exeDate.format(nowExe).toString();

					if (eElement.getElementsByTagName("error").getLength() == 0) {
						junitTestStatus = "Passed";
					} else {
						junitTestStatus = "Failed";
					}

					if (junitTestCaseId.contains(",")) {

						String[] testCaseIdArray = junitTestCaseId.split(",");
						for (int i = 0; i < testCaseIdArray.length; i++) {

							junitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId,
									testCaseIdArray[i], testOwnerMailId, junitTestStatus);
							Logger.info("test instance id creted/found:" + junitTestInstanceID);

							/* create serenity Test Runs and attach url to the RunID */
							if (!junitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
									&& !junitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
									&& !junitTestInstanceID.equalsIgnoreCase("")) {

								junitTestRunId = ALMTestRunCreator.createTestRun(testSetId, testCaseIdArray[i],
										junitTestInstanceID, driver, junitExecutionDate, junitTestStatus,
										testOwnerMailId, junitRunName, driver, "hp.qc.run.MANUAL", junitExeDuration);
								Logger.info("test run id creted/found:" + junitTestRunId);

								ALMCommonHelper.attachURLtoALMRunID(junitTestRunId, testAttachment, junitRunName);

								String queryParam = "test-instances/" + junitTestInstanceID;
								updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
										junitTestStatus, junitExecutionDate);

							} else {
								Logger.error("BOOOOOM !!!!\n"
										+ "Error occured while creating test instance with congig id->>" + junitConfigId
										+ "\n<<- Plesase recheck your parameters.\n"
										+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
										+ "Otherwise, your alm will be messed up.");
								break;
							}

						}

					} else {

						junitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, junitTestCaseId,
								testOwnerMailId, junitTestStatus);
						Logger.info("test instance id creted/found:" + junitTestInstanceID);

						/* create serenity Test Runs and attach url to the RunID */
						if (!junitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !junitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !junitTestInstanceID.equalsIgnoreCase("")) {

							junitTestRunId = ALMTestRunCreator.createTestRun(testSetId, junitTestCaseId,
									junitTestInstanceID, driver, junitExecutionDate, junitTestStatus, testOwnerMailId,
									junitRunName, driver, "hp.qc.run.MANUAL", junitExeDuration);
							Logger.info("test run id creted/found:" + junitTestRunId);

							ALMCommonHelper.attachURLtoALMRunID(junitTestRunId, testAttachment, junitRunName);

							String queryParam = "test-instances/" + junitTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									junitTestStatus, junitExecutionDate);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + junitConfigId
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
									+ "Otherwise, your alm will be messed up.");
							break;
						}

					}
				}

			}
		}

	}

	public static void junitUpdateTestReportInALMWhileManualTestCasesArePresentWithName() throws Exception {

		List<File> files;
		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "xml" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			files.remove(test_arg);
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

			File inputFile = new File(file.getCanonicalPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("testcase");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");

					Logger.info("name:" + scenarioName);
					junitTestCaseId = ALMCommonHelper.isResouceAlreadyAvailable(
							"tests?query={parent-id[=\"" + testPlanFolderId + "\"];name[=\"" + scenarioName + "\"]}");

					if (junitTestCaseId.equalsIgnoreCase("ERROR_OCCURED")
							|| junitTestCaseId.equalsIgnoreCase("No_ID_FOUND")) {
						throw new ConnectorCustomException("Test Case Id not found for Test case Name:" + scenarioName
								+ " in test plan folder id:" + testPlanFolderId);
					}

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					junitRunName = "Run_" + dtf.format(now).toString();

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					junitExecutionDate = exeDate.format(nowExe).toString();

					if (eElement.getElementsByTagName("error").getLength() == 0) {
						junitTestStatus = "Passed";
					} else {
						junitTestStatus = "Failed";
					}

					junitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, junitTestCaseId,
							testOwnerMailId, junitTestStatus);
					Logger.info("test instance id creted/found:" + junitTestInstanceID);

					/* create serenity Test Runs and attach url to the RunID */
					if (!junitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
							&& !junitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
							&& !junitTestInstanceID.equalsIgnoreCase("")) {

						junitTestRunId = ALMTestRunCreator.createTestRun(testSetId, junitTestCaseId,
								junitTestInstanceID, driver, junitExecutionDate, junitTestStatus, testOwnerMailId,
								junitRunName, driver, "hp.qc.run.MANUAL", junitExeDuration);
						Logger.info("test run id creted/found:" + junitTestRunId);

						ALMCommonHelper.attachURLtoALMRunID(junitTestRunId, testAttachment, junitRunName);

						String queryParam = "test-instances/" + junitTestInstanceID;
						updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
								junitTestStatus, junitExecutionDate);

					} else {
						Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
								+ junitConfigId + "\n<<- Plesase recheck your parameters.\n"
								+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
								+ "Otherwise, your alm will be messed up.");
						break;
					}

				}
			}

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

	public static void junitUpdateTestReportInALMWhileManualTestCasesAreNotPresent() throws Exception {

		List<File> files;
		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "xml" };
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			files.remove(test_arg);
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

			File inputFile = new File(file.getCanonicalPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList suiteList = doc.getElementsByTagName("testsuite");
			Node suite = suiteList.item(0);
			Element suiteElement = (Element) suite;
			featureName = suiteElement.getAttribute("name");

			// newlyCreatedtestPlanFolderId =
			// ALMTestPlanFolderCreator.createTestFolderInTestPlan(testPlanFolderId,
			// featureName, "As an user I want to use all functionalities of " +
			// featureName);
			//
			// Logger.info("new test feature folder id created/found:" +
			// newlyCreatedtestPlanFolderId);

			NodeList nList = doc.getElementsByTagName("testcase");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");

					Logger.info("name:" + scenarioName);

					newlyCreatedTestCaseId = ALMTestCaseCreator.createTestCaseInTestPlan(testPlanFolderId,
							scenarioName);

					Logger.info("new test id created/found:" + newlyCreatedTestCaseId);

					if (alm_test_set_link_creation.equalsIgnoreCase("Y")) {
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
						LocalDateTime now = LocalDateTime.now();
						junitRunName = "Run_" + dtf.format(now).toString();

						DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						LocalDateTime nowExe = LocalDateTime.now();
						junitExecutionDate = exeDate.format(nowExe).toString();

						if (eElement.getElementsByTagName("error").getLength() == 0) {
							junitTestStatus = "Passed";
						} else {
							junitTestStatus = "Failed";
						}

						junitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId,
								newlyCreatedTestCaseId, testOwnerMailId, junitTestStatus);
						Logger.info("test instance id creted/found:" + junitTestInstanceID);

						/* create serenity Test Runs and attach url to the RunID */
						if (!junitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !junitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !junitTestInstanceID.equalsIgnoreCase("")) {

							junitTestRunId = ALMTestRunCreator.createTestRun(testSetId, newlyCreatedTestCaseId,
									junitTestInstanceID, driver, junitExecutionDate, junitTestStatus, testOwnerMailId,
									junitRunName, driver, "hp.qc.run.MANUAL", junitExeDuration);
							Logger.info("test run id creted/found:" + junitTestRunId);

							ALMCommonHelper.attachURLtoALMRunID(junitTestRunId, testAttachment, junitRunName);

							String queryParam = "test-instances/" + junitTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									junitTestStatus, junitExecutionDate);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + junitConfigId
									+ "\n<<- Plesase recheck your parameters.\n"
									+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
									+ "Otherwise, your alm will be messed up.");
							break;
						}
					}

				}
			}

		}
	}

}
