package runner;

import org.pmw.tinylog.Logger;

import exceptions.ConnectorCustomException;
import infrastructure.Constants;
import parsers.JsonReportParser;
import parsers.JunitReportParser;
import parsers.TestNGReportParser;
import parsers.XunitReportParser;

public class BridgeRunnerHelper  extends Constants{
	
	public static void handleALMOperationBasedOnFrameworkType() throws Exception{
		
		Logger.info("framework selected: " + test_framework_type);
		

		switch (test_framework_type.toLowerCase()) {

		case "serenity-json":
			if (alm_test_case_operation.equalsIgnoreCase("create")
					|| alm_test_case_operation.equalsIgnoreCase("update")) {
				JsonReportParser.serenityUpdateTestReportInALMWhileManualTestCasesAreNotPresent();

			} else if (alm_test_case_operation.equalsIgnoreCase("none")) {
				if (alm_test_case_mapping_method.equalsIgnoreCase("name")) {
					JsonReportParser.serenityUpdateTestReportInALMWhileManualTestCasesArePresentWithName();
				} else if (alm_test_case_mapping_method.equalsIgnoreCase("id")) {
					JsonReportParser.serenityUpdateTestReportInALMWhileManualTestCasesArePresentWithID();
				} else {
					Logger.error("alm_test_case_mapping_method is set to:" + alm_test_case_mapping_method + "\n"
							+ "alm_test_case_mapping_method expected:name/id");
					System.exit(1);
				}

			}

			break;

		case "testng":
			if (alm_test_case_operation.equalsIgnoreCase("create")
					|| alm_test_case_operation.equalsIgnoreCase("update")) {
				TestNGReportParser.testngUpdateTestReportInALMWhileManualTestCasesAreNotPresent();

			} else if (alm_test_case_operation.equalsIgnoreCase("none")) {
				if (alm_test_case_mapping_method.equalsIgnoreCase("name")) {
					TestNGReportParser.testngUpdateTestReportInALMWhileManualTestCasesArePresentWithName();
				} else if (alm_test_case_mapping_method.equalsIgnoreCase("id")) {
					TestNGReportParser.testngUpdateTestReportInALMWhileManualTestCasesArePresentWithID();
				} else {
					Logger.error("alm_test_case_mapping_method is set to:" + alm_test_case_mapping_method + "\n"
							+ "alm_test_case_mapping_method expected:name/id");
					System.exit(1);
				}

			}
			break;

		case "junit":
			if (alm_test_case_operation.equalsIgnoreCase("create")
					|| alm_test_case_operation.equalsIgnoreCase("update")) {
				JunitReportParser.junitUpdateTestReportInALMWhileManualTestCasesAreNotPresent();

			} else if (alm_test_case_operation.equalsIgnoreCase("none")) {
				if (alm_test_case_mapping_method.equalsIgnoreCase("name")) {
					JunitReportParser.junitUpdateTestReportInALMWhileManualTestCasesArePresentWithName();
				} else if (alm_test_case_mapping_method.equalsIgnoreCase("id")) {
					JunitReportParser.junitUpdateTestReportInALMWhileManualTestCasesArePresentWithID();
				} else {
					Logger.error("alm_test_case_mapping_method is set to:" + alm_test_case_mapping_method + "\n"
							+ "alm_test_case_mapping_method expected:name/id");
					System.exit(1);
				}

			}
			break;
			
		case "xunit":
			if (alm_test_case_operation.equalsIgnoreCase("create")
					|| alm_test_case_operation.equalsIgnoreCase("update")) {
				XunitReportParser.xunitUpdateTestReportInALMWhileManualTestCasesAreNotPresent();

			} else if (alm_test_case_operation.equalsIgnoreCase("none")) {
				if (alm_test_case_mapping_method.equalsIgnoreCase("name")) {
					XunitReportParser.xunitUpdateTestReportInALMWhileManualTestCasesArePresentWithName();
				} else if (alm_test_case_mapping_method.equalsIgnoreCase("id")) {
					XunitReportParser.xunitUpdateTestReportInALMWhileManualTestCasesArePresentWithID();
				} else {
					Logger.error("alm_test_case_mapping_method is set to:" + alm_test_case_mapping_method + "\n"
							+ "alm_test_case_mapping_method expected:name/id");
					System.exit(1);
				}

			}
			break;

		default:
			throw new ConnectorCustomException("Wrong Framework type selected :" + test_framework_type
					+ "\nPlease select a correct Framework Type (serenity/testng/junit)"
					+ "\nPlease go through: https://confluence.intranet.db.com/x/b5pIJ"
					+ "\nPlease send an email to the CTM|CTS Team cts.ta@db.com for any support request.");

		}

		
	}

}
