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

@SuppressWarnings({ "unused", "unlikely-arg-type","unchecked"})
public class TestNGReportParser extends InitializationHandler {

	private static String scenarioName;
	private static String testNgTestStatus;
	private static String testNgRunName;
	private static String testNgExeDuration;
	private static String testNgExecutionDate;

	private static String testNgTestCaseId;
	private static String testNgConfigId;
	private static String testNgTestInstanceID;
	private static String testNgTestRunId;
	private static String updateStatusOfTestInstance;
	private static String featureName;
	private static String newlyCreatedTestCaseId;

	public static void testngUpdateTestReportInALMWhileManualTestCasesArePresentWithID() throws Exception {

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
			NodeList nList = doc.getElementsByTagName("test");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");
					testNgExeDuration = eElement.getAttribute("duration-ms");

					Logger.info("name:" + scenarioName);
					testNgTestCaseId = getFunctionId(scenarioName);

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					testNgRunName = "Run_" + dtf.format(now).toString();

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					testNgExecutionDate = exeDate.format(nowExe).toString();

					if (!checkTestFailed(eElement)) {
						testNgTestStatus = "Passed";
					} else {
						testNgTestStatus = "Failed";
					}

					if (testNgTestCaseId.contains(",")) {

						String[] testCaseIdArray = testNgTestCaseId.split(",");
						for (int i = 0; i < testCaseIdArray.length; i++) {

							testNgTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId,
									testCaseIdArray[i], testOwnerMailId, testNgTestStatus);
							Logger.info("test instance id creted/found:" + testNgTestInstanceID);

							/* create serenity Test Runs and attach url to the RunID */
							if (!testNgTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
									&& !testNgTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
									&& !testNgTestInstanceID.equalsIgnoreCase("")) {

								testNgTestRunId = ALMTestRunCreator.createTestRun(testSetId, testCaseIdArray[i],
										testNgTestInstanceID, test_execution_driver, testNgExecutionDate,
										testNgTestStatus, testOwnerMailId, testNgRunName, test_execution_driver,
										"hp.qc.run.MANUAL", testNgExeDuration);
								Logger.info("test run id creted/found:" + testNgTestRunId);

								ALMCommonHelper.attachURLtoALMRunID(testNgTestRunId, testAttachment, testNgRunName);

								String queryParam = "test-instances/" + testNgTestInstanceID;
								updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
										testNgTestStatus, testNgExecutionDate);

							} else {
								Logger.error("BOOOOOM !!!!\n"
										+ "Error occured while creating test instance with congig id->>"
										+ testNgConfigId + "\n<<- Plesase recheck your parameters.\n"
										+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
										+ "Otherwise, your alm will be messed up.");
								break;
							}

						}

					} else {

						testNgTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, testNgTestCaseId,
								testOwnerMailId, testNgTestStatus);
						Logger.info("test instance id creted/found:" + testNgTestInstanceID);

						/* create serenity Test Runs and attach url to the RunID */
						if (!testNgTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !testNgTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !testNgTestInstanceID.equalsIgnoreCase("")) {

							testNgTestRunId = ALMTestRunCreator.createTestRun(testSetId, testNgTestCaseId,
									testNgTestInstanceID, test_execution_driver, testNgExecutionDate, testNgTestStatus,
									testOwnerMailId, testNgRunName, test_execution_driver, "hp.qc.run.MANUAL",
									testNgExeDuration);
							Logger.info("test run id creted/found:" + testNgTestRunId);

							ALMCommonHelper.attachURLtoALMRunID(testNgTestRunId, testAttachment, testNgRunName);

							String queryParam = "test-instances/" + testNgTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									testNgTestStatus, testNgExecutionDate);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + testNgConfigId
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

	public static void testngUpdateTestReportInALMWhileManualTestCasesArePresentWithName() throws Exception {

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
			NodeList nList = doc.getElementsByTagName("test");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");
					testNgExeDuration = eElement.getAttribute("duration-ms");

					Logger.info("name:" + scenarioName);
					testNgTestCaseId = ALMCommonHelper.isResouceAlreadyAvailable(
							"tests?query={parent-id[=\"" + testPlanFolderId + "\"];name[=\"" + scenarioName + "\"]}");

					if (testNgTestCaseId.equalsIgnoreCase("ERROR_OCCURED")
							|| testNgTestCaseId.equalsIgnoreCase("No_ID_FOUND")) {
						throw new ConnectorCustomException("Test Case Id not found for Test case Name:" + scenarioName
								+ " in test plan folder id:" + testPlanFolderId);
					}

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					testNgRunName = "Run_" + dtf.format(now).toString();

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					testNgExecutionDate = exeDate.format(nowExe).toString();

					if (!checkTestFailed(eElement)) {
						testNgTestStatus = "Passed";
					} else {
						testNgTestStatus = "Failed";
					}

					testNgTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, testNgTestCaseId,
							testOwnerMailId, testNgTestStatus);
					Logger.info("test instance id creted/found:" + testNgTestInstanceID);

					/* create serenity Test Runs and attach url to the RunID */
					if (!testNgTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
							&& !testNgTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
							&& !testNgTestInstanceID.equalsIgnoreCase("")) {

						testNgTestRunId = ALMTestRunCreator.createTestRun(testSetId, testNgTestCaseId,
								testNgTestInstanceID, test_execution_driver, testNgExecutionDate, testNgTestStatus,
								testOwnerMailId, testNgRunName, test_execution_driver, "hp.qc.run.MANUAL",
								testNgExeDuration);
						Logger.info("test run id creted/found:" + testNgTestRunId);

						ALMCommonHelper.attachURLtoALMRunID(testNgTestRunId, testAttachment, testNgRunName);

						String queryParam = "test-instances/" + testNgTestInstanceID;
						updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
								testNgTestStatus, testNgExecutionDate);

					} else {
						Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
								+ testNgConfigId + "\n<<- Plesase recheck your parameters.\n"
								+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
								+ "Otherwise, your alm will be messed up.");
						break;
					}

				}
			}

		}

	}

	private static boolean checkTestFailed(Element eElement) {

		int node_count = eElement.getElementsByTagName("test-method").getLength();

		for (int i = 0; i < node_count; i++) {
			if (eElement.getElementsByTagName("test-method").item(i).getAttributes().getNamedItem("status").toString()
					.contains("FAIL")) {
				return true;
			}
		}
		return false;

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

	public static void testngUpdateTestReportInALMWhileManualTestCasesAreNotPresent() throws Exception {

		List<File> files;
		Logger.info("test_report_read_mode is set to:" + test_report_read_mode);
		Logger.info("test_report_location is set to:" + test_report_location);

		if (test_report_read_mode.equalsIgnoreCase("path")) {
			File dir = new File(test_report_location);
			String[] extensions = new String[] { "xml" };
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

			File inputFile = new File(file.getCanonicalPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList suiteList = doc.getElementsByTagName("suite");
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

			NodeList nList = doc.getElementsByTagName("test");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					scenarioName = eElement.getAttribute("name");
					testNgExeDuration = eElement.getAttribute("duration-ms");

					Logger.info("name:" + scenarioName);
					newlyCreatedTestCaseId = ALMTestCaseCreator.createTestCaseInTestPlan(testPlanFolderId,
							scenarioName);

					Logger.info("new test id created/found:" + newlyCreatedTestCaseId);

					if (alm_test_set_link_creation.equalsIgnoreCase("Y")) {

						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
						LocalDateTime now = LocalDateTime.now();
						testNgRunName = "Run_" + dtf.format(now).toString();

						DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						LocalDateTime nowExe = LocalDateTime.now();
						testNgExecutionDate = exeDate.format(nowExe).toString();

						if (!checkTestFailed(eElement)) {
							testNgTestStatus = "Passed";
						} else {
							testNgTestStatus = "Failed";
						}

						testNgTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId,
								newlyCreatedTestCaseId, testOwnerMailId, testNgTestStatus);
						Logger.info("test instance id creted/found:" + testNgTestInstanceID);

						if (!testNgTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !testNgTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !testNgTestInstanceID.equalsIgnoreCase("")) {

							testNgTestRunId = ALMTestRunCreator.createTestRun(testSetId, newlyCreatedTestCaseId,
									testNgTestInstanceID, test_execution_driver, testNgExecutionDate, testNgTestStatus,
									testOwnerMailId, testNgRunName, test_execution_driver, "hp.qc.run.MANUAL",
									testNgExeDuration);
							Logger.info("test run id creted/found:" + testNgTestRunId);

							ALMCommonHelper.attachURLtoALMRunID(testNgTestRunId, testAttachment, testNgRunName);

							String queryParam = "test-instances/" + testNgTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									testNgTestStatus, testNgExecutionDate);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + testNgConfigId
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
