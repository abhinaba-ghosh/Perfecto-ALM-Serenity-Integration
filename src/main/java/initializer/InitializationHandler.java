package initializer;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

import helpers.ConfigHelper;
import infrastructure.Constants;

public class InitializationHandler extends Constants {

	public static void inializeALMConfigurations() throws Exception {

		test_framework_type = ConfigHelper.getValueFromXml(test_arg, "test_report_framework_type");
		test_report_read_mode = ConfigHelper.getValueFromXml(test_arg, "test_report_read_mode");
		test_report_location = ConfigHelper.getValueFromXml(test_arg, "test_report_location");
		test_execution_driver = ConfigHelper.getValueFromXml(test_arg, "test_execution_driver");

		alm_test_case_operation = ConfigHelper.getValueFromXml(test_arg, "alm_test_case_operation");
		alm_test_case_mapping_method = ConfigHelper.getValueFromXml(test_arg, "alm_test_case_mapping_method");
		alm_test_set_link_creation = ConfigHelper.getValueFromXml(test_arg, "alm_test_set_link_to_test_case");

		USERNAME = almUser;
		PASSWORD = almUserPassword;
		testOwnerMailId=almUser;
		HOST = ConfigHelper.getValueFromXml(test_arg, "alm_host");
		PORT = ConfigHelper.getValueFromXml(test_arg, "alm_port");
		DOMAIN = ConfigHelper.getValueFromXml(test_arg, "alm_domain");
		PROJECT = ConfigHelper.getValueFromXml(test_arg, "alm_project");
		SECURE_PROXY_HOST = ConfigHelper.getValueFromXml(test_arg, "alm_secure_proxy_host");
		SECURE_PROXY_PORT = Integer.parseInt(ConfigHelper.getValueFromXml(test_arg, "alm_secure_proxy_port"));
		
		logFileName = ConfigHelper.getValueFromXml(test_arg, "tinylog.filename");
		logFormat = ConfigHelper.getValueFromXml(test_arg, "tinylog.format");

	}

	public static void initializeLogger() {

		Configurator.defaultConfig()
					.writer(new FileWriter(logFileName))
					.addWriter(new ConsoleWriter())
					.locale(null)
					.formatPattern(logFormat)
					.activate();
	}

}
