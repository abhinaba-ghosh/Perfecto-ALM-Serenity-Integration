package com.db.PerfectoAnalyserRunner;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.pmw.tinylog.Logger;

import com.db.PerfectoReportAnalyser.PerfectoAPIHandler;
import com.db.PerfectoReportAnalyser.PerfectoMethodHelper;
import com.db.PerfectoReportAnalyser.PerfectoReportParser;

import helpers.Helper;

public class PerfectoIntegrationRunner {

	static Properties runner_props = Helper
			.readPropertiesFromFile(Helper.getCurrentDirectory() + "//config.properties");

	public static final String DigiZoomReportingUrl = runner_props.getProperty("perfecto_report_url");
	public static final String testApplicationNameListedInALM = runner_props.getProperty("alm_application_name");
	public static final String testOwnerMailId = runner_props.getProperty("alm_owner_user_id");

	public static String testPlanFolderId = "";
	public static String testLabFolderId = "";
	public static String tagsParam = "";
	public static String PerfectoJsonResponseAsPerSpecificTag = "";
	public static String almUser="";
	public static String almUserPassword="";
	public static String perfectoToken="";

	public static void main(String[] args) throws Exception {

		try {
			almUser=args[0];
			almUserPassword=args[1];
			perfectoToken=args[2];
			testPlanFolderId = args[3];
			testLabFolderId = args[4];
			tagsParam = args[5];

		} catch (Exception e) {
			Logger.warn("Please re-check the parameter list.\n" + "perfecto-ALM bridge require 5 parameters -\n"
					+ "- ALM User Name - [String]:: can not be null\n"
					+ "- ALM User password - [String]:: can not be null\n"
					+ "- Perfecto Token - [String]:: can not be null\n"
					+ "- Test Plan folder Id - [String]:: can not be null\n"
					+ "- Test Lab folder id  - [String]:: can not be null\n"
					+ "- perfecto url tags   - [Comma separated String]:: can not be null\n"
					+" -  for any help, connect to: abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
					+ " - any clarification / enhancement request : Central Test Management (CTM)");
			Logger.error(e);
			System.exit(0);

		}

		
		SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
        
        try{
        	PerfectoMethodHelper.updateALMConfigProperties(almUser, almUserPassword, perfectoToken);
        }catch(Exception e){
        	Logger.warn("Error occured while updating config.properties file\n"
					+" -  for any help, connect to: abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
					+ " - any clarification / enhancement request : Central Test Management (CTM)");
			Logger.error(e);
			System.exit(0);
        }
		
		PerfectoJsonResponseAsPerSpecificTag = PerfectoAPIHandler.getPerectoReportByTagName(DigiZoomReportingUrl,
				tagsParam);

		if (PerfectoJsonResponseAsPerSpecificTag.contains("deviceType")
				&& PerfectoJsonResponseAsPerSpecificTag.contains("model")) {
			PerfectoReportParser.perfectoUpdateTestReportInALM(PerfectoJsonResponseAsPerSpecificTag, testPlanFolderId,
					testApplicationNameListedInALM, "C-Medium", "SIT", "MANUAL", "Design", "Internal", testOwnerMailId,
					"***CREATED BY PERFECTO-ALM-BRIDGE***", testLabFolderId);

		} else {
			Logger.error("Error in perfecto response");
			Logger.error("Please analyse the perfecto_report.json file. -\n"
					+ " -  for any help, connect to: abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
					+ " - any clarification / enhancement request : Central Test Management (CTM)");
			System.exit(0);

		}

	}
	
	 private static class DefaultTrustManager implements X509TrustManager {

	        @Override
	        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

	        @Override
	        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	    }

}
