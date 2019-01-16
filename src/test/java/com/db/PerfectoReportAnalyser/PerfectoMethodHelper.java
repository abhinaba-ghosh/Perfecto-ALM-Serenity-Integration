package com.db.PerfectoReportAnalyser;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.db.ALMInfrastructure.Helper;

public class PerfectoMethodHelper {
	
	public static void updateALMConfigProperties( String technicalUserName, String technicalUserPassword,String perfecto_security_token)
			throws ConfigurationException {

		System.out.println("currnet dir:" + Helper.getCurrentDirectory());
		PropertiesConfiguration config = new PropertiesConfiguration(
				Helper.getCurrentDirectory() + "//config.properties");
		config.setProperty("alm_login_user_name", technicalUserName);
		config.setProperty("alm_login_user_pwd", technicalUserPassword);
		config.setProperty("perfecto_security_token", perfecto_security_token);
		config.save();

		System.out.println(" Config Property Successfully Updated..");
	}


}
