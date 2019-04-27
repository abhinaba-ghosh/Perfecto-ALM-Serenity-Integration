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
import infrastructure.Constants;
import initializer.InitializationHandler;

@SuppressWarnings({ "unused", "unlikely-arg-type","unchecked"})
public class XunitReportParser extends Constants {

	private static String xunitTestCaseName;
	private static String xunitTestStatus;
	private static String xunitRunName;
	private static String xunitExeDuration;
	private static String xunitExecutionDate;

	private static String xunitTestCaseId;
	private static String xunitConfigId;
	private static String xunitTestInstanceID;
	private static String xunitTestRunId;
	private static String updateStatusOfTestInstance;
	private static String newlyCreatedtestPlanFolderId;
	private static String newlyCreatedTestCaseId;

	public static void xunitUpdateTestReportInALMWhileManualTestCasesArePresentWithID() throws Exception {

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

					xunitTestCaseName = eElement.getAttribute("name");

					Logger.info("name:" + xunitTestCaseName);
					xunitTestCaseId = getFunctionId(xunitTestCaseName);

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					xunitRunName = "Run_" + dtf.format(now).toString();
					xunitExeDuration = eElement.getAttribute("time").split("\\.")[0];

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					xunitExecutionDate = exeDate.format(nowExe).toString();

					if (eElement.getElementsByTagName("error").getLength() == 0) {
						xunitTestStatus = "Passed";
						if (eElement.getElementsByTagName("skipped").getLength() != 0) {
							xunitTestStatus = "Not Completed";
						}
					} else {
						xunitTestStatus = "Failed";
					}

					if (xunitTestCaseId.contains(",")) {

						String[] testCaseIdArray = xunitTestCaseId.split(",");
						for (int i = 0; i < testCaseIdArray.length; i++) {

							xunitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId,
									testCaseIdArray[i], testOwnerMailId, xunitTestStatus);
							Logger.info("test instance id creted/found:" + xunitTestInstanceID);

							/* create serenity Test Runs and attach url to the RunID */
							if (!xunitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
									&& !xunitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
									&& !xunitTestInstanceID.equalsIgnoreCase("")) {

								xunitTestRunId = ALMTestRunCreator.createTestRun(testSetId, testCaseIdArray[i],
										xunitTestInstanceID, test_execution_driver, xunitExecutionDate, xunitTestStatus,
										testOwnerMailId, xunitRunName, test_execution_driver, "hp.qc.run.MANUAL",
										xunitExeDuration);
								Logger.info("test run id creted/found:" + xunitTestRunId);

								if (testRunAttachmentType.equalsIgnoreCase("url")) {
									ALMCommonHelper.attachURLtoALMRunID(xunitTestRunId, testAttachment, xunitRunName);
								} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
									ALMCommonHelper.uploadFileAttachmentToTestRun(xunitTestRunId, testAttachment);

								}

								String queryParam = "test-instances/" + xunitTestInstanceID;
								updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
										xunitTestStatus, xunitExecutionDate);

							} else {
								Logger.error("BOOOOOM !!!!\n"
										+ "Error occured while creating test instance with congig id->>" + xunitConfigId
										+ "\n<<- Plesase recheck your parameters.\n"
										+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
										+ "Otherwise, your alm will be messed up.");
								break;
							}

						}

					} else {

						xunitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, xunitTestCaseId,
								testOwnerMailId, xunitTestStatus);
						Logger.info("test instance id creted/found:" + xunitTestInstanceID);

						/* create serenity Test Runs and attach url to the RunID */
						if (!xunitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
								&& !xunitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
								&& !xunitTestInstanceID.equalsIgnoreCase("")) {

							xunitTestRunId = ALMTestRunCreator.createTestRun(testSetId, xunitTestCaseId,
									xunitTestInstanceID, test_execution_driver, xunitExecutionDate, xunitTestStatus,
									testOwnerMailId, xunitRunName, test_execution_driver, "hp.qc.run.MANUAL",
									xunitExeDuration);
							Logger.info("test run id creted/found:" + xunitTestRunId);

							if (testRunAttachmentType.equalsIgnoreCase("url")) {
								ALMCommonHelper.attachURLtoALMRunID(xunitTestRunId, testAttachment, xunitRunName);
							} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
								ALMCommonHelper.uploadFileAttachmentToTestRun(xunitTestRunId, testAttachment);

							}

							String queryParam = "test-instances/" + xunitTestInstanceID;
							updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
									xunitTestStatus, xunitExecutionDate);

						} else {
							Logger.error("BOOOOOM !!!!\n"
									+ "Error occured while creating test instance with congig id->>" + xunitConfigId
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

	public static void xunitUpdateTestReportInALMWhileManualTestCasesArePresentWithName() throws Exception {

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

					xunitTestCaseName = eElement.getAttribute("name");

					Logger.info("name:" + xunitTestCaseName);
					xunitTestCaseId = ALMCommonHelper.isResouceAlreadyAvailable("tests?query={parent-id[=\""
							+ testPlanFolderId + "\"];name[=\"" + xunitTestCaseName + "\"]}");

					if (xunitTestCaseId.equalsIgnoreCase("ERROR_OCCURED")
							|| xunitTestCaseId.equalsIgnoreCase("No_ID_FOUND")) {
						throw new ConnectorCustomException("Test Case Id not found for Test case Name:"
								+ xunitTestCaseName + " in test plan folder id:" + testPlanFolderId);
					}

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					xunitRunName = "Run_" + dtf.format(now).toString();

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					xunitExecutionDate = exeDate.format(nowExe).toString();
					xunitExeDuration = eElement.getAttribute("time").split("\\.")[0];

					if (eElement.getElementsByTagName("error").getLength() == 0) {
						xunitTestStatus = "Passed";
						if (eElement.getElementsByTagName("skipped").getLength() != 0) {
							xunitTestStatus = "Not Completed";
						}
					} else {
						xunitTestStatus = "Failed";
					}

					xunitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, xunitTestCaseId,
							testOwnerMailId, xunitTestStatus);
					Logger.info("test instance id creted/found:" + xunitTestInstanceID);

					/* create serenity Test Runs and attach url to the RunID */
					if (!xunitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
							&& !xunitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
							&& !xunitTestInstanceID.equalsIgnoreCase("")) {

						xunitTestRunId = ALMTestRunCreator.createTestRun(testSetId, xunitTestCaseId,
								xunitTestInstanceID, test_execution_driver, xunitExecutionDate, xunitTestStatus,
								testOwnerMailId, xunitRunName, test_execution_driver, "hp.qc.run.MANUAL",
								xunitExeDuration);
						Logger.info("test run id creted/found:" + xunitTestRunId);

						if (testRunAttachmentType.equalsIgnoreCase("url")) {
							ALMCommonHelper.attachURLtoALMRunID(xunitTestRunId, testAttachment, xunitRunName);
						} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
							ALMCommonHelper.uploadFileAttachmentToTestRun(xunitTestRunId, testAttachment);

						}

						String queryParam = "test-instances/" + xunitTestInstanceID;
						updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
								xunitTestStatus, xunitExecutionDate);

					} else {
						Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
								+ xunitConfigId + "\n<<- Plesase recheck your parameters.\n"
								+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
								+ "Otherwise, your alm will be messed up.");
						break;
					}

				}
			}

		}

	}

	private static String getFunctionId(String xunitTestCaseName) {
		String temp_func_id = "";

		Matcher m = Pattern.compile("\\(ALM_TCID-([^]]+)\\)").matcher(xunitTestCaseName);
		if (m.find()) {
			temp_func_id = m.group(1).replaceAll(" ", "");
			Logger.info("Serenity Report creation: Received ALM TC id - " + temp_func_id);

		}

		return temp_func_id;

	}

	public static void xunitUpdateTestReportInALMWhileManualTestCasesAreNotPresent() throws Exception {

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
			for (int i = 0; i < suiteList.getLength(); i++) {
				Node suite = suiteList.item(i);
				Element suiteElement = (Element) suite;
				String suiteName = suiteElement.getAttribute("name");
				Logger.info("suite name:" + suiteElement.getAttribute("name"));
				String newlyCreatedSuitefolderId = ALMTestPlanFolderCreator.createTestFolderInTestPlan(testPlanFolderId,
						suiteName, suiteName);
				Logger.info("new test feature folder id created/found:" + newlyCreatedSuitefolderId);
				createALMResourcesForTestSuite(newlyCreatedSuitefolderId, suiteElement);

			}

		}
	}

	private static void createALMResourcesForTestSuite(String suitefolderId, Element suiteElement) throws Exception {

		NodeList nList = suiteElement.getElementsByTagName("testcase");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				xunitTestCaseName = eElement.getAttribute("name");

				Logger.info("x unit test case name:" + xunitTestCaseName);

				newlyCreatedTestCaseId = ALMTestCaseCreator.createTestCaseInTestPlan(suitefolderId, xunitTestCaseName);

				Logger.info("new test id created/found:" + newlyCreatedTestCaseId);

				if (alm_test_set_link_creation.equalsIgnoreCase("Y")) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
					LocalDateTime now = LocalDateTime.now();
					xunitRunName = "Run_" + dtf.format(now).toString();
					xunitExeDuration = eElement.getAttribute("time").split("\\.")[0];

					DateTimeFormatter exeDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime nowExe = LocalDateTime.now();
					xunitExecutionDate = exeDate.format(nowExe).toString();

					if (eElement.getElementsByTagName("error").getLength() == 0) {
						xunitTestStatus = "Passed";
						if (eElement.getElementsByTagName("skipped").getLength() != 0) {
							xunitTestStatus = "Not Completed";
						}
					} else {
						xunitTestStatus = "Failed";
					}

					xunitTestInstanceID = ALMTestInstanceCreator.createTestInstance(testSetId, newlyCreatedTestCaseId,
							testOwnerMailId, xunitTestStatus);
					Logger.info("test instance id creted/found:" + xunitTestInstanceID);

					/* create serenity Test Runs and attach url to the RunID */
					if (!xunitTestInstanceID.equalsIgnoreCase("ERROR_OCCURED")
							&& !xunitTestInstanceID.equalsIgnoreCase("NO_ID_GENERATED")
							&& !xunitTestInstanceID.equalsIgnoreCase("")) {

						xunitTestRunId = ALMTestRunCreator.createTestRun(testSetId, newlyCreatedTestCaseId,
								xunitTestInstanceID, test_execution_driver, xunitExecutionDate, xunitTestStatus,
								testOwnerMailId, xunitRunName, test_execution_driver, "hp.qc.run.MANUAL",
								xunitExeDuration);
						Logger.info("test run id creted/found:" + xunitTestRunId);

						if (testRunAttachmentType.equalsIgnoreCase("url")) {
							ALMCommonHelper.attachURLtoALMRunID(xunitTestRunId, testAttachment, xunitRunName);
						} else if (testRunAttachmentType.equalsIgnoreCase("file")) {
							ALMCommonHelper.uploadFileAttachmentToTestRun(xunitTestRunId, testAttachment);

						}

						String queryParam = "test-instances/" + xunitTestInstanceID;
						updateStatusOfTestInstance = ALMTestInstanceCreator.updateTestInstanceStatus(queryParam,
								xunitTestStatus, xunitExecutionDate);

					} else {
						Logger.error("BOOOOOM !!!!\n" + "Error occured while creating test instance with congig id->>"
								+ xunitConfigId + "\n<<- Plesase recheck your parameters.\n"
								+ "Please send an email to the CTM|CTS Team cts.ta@db.com for any support request.\n"
								+ "Otherwise, your alm will be messed up.");
						break;
					}
				}

			}
		}

	}

}
