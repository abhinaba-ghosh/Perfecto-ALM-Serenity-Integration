package com.db.PerfectoReportAnalyser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.pmw.tinylog.Logger;

import helpers.SystemHelper;

public class PerfectoAPIHandler {

	static Properties props = SystemHelper
			.readPropertiesFromFile(SystemHelper.getCurrentDirectory() + "//config.properties");

	public static final String Perfecto_Token = props.getProperty("perfecto_security_token");
	public static final String Secure_Host = props.getProperty("perfecto_secure_proxy_host");
	public static final int Secure_Proxy_Port = Integer.parseInt(props.getProperty("perfecto_secure_proxy_port"));

	public static String getPerectoReportByTagName(String DigiZoomReportingUrl, String tagString) throws Exception {

		String reportingUrlByTagName = DigiZoomReportingUrl + "?" + getTagsUrl(tagString);

		URL url = new URL(reportingUrlByTagName);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), url.getRef());
		reportingUrlByTagName = uri.toASCIIString();
		URL perefectoUrl = new URL(reportingUrlByTagName);
		Logger.info("perfecto reporting url:" + perefectoUrl);
		System.out.println("*** Keep calm! We summon perfecto report from Cloud Heaven! *****");

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Secure_Host, Secure_Proxy_Port));
		HttpURLConnection conn = (HttpURLConnection) perefectoUrl.openConnection(proxy);
		conn.setRequestMethod("GET");

		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("PERFECTO_AUTHORIZATION", Perfecto_Token);
		conn.connect();

		Logger.info("perfect response code:"+conn.getResponseCode());
		if (conn.getResponseCode() != 200) {
			
			if(conn.getResponseCode() == 500) {
				Logger.error("perfecto response error message:" + conn.getResponseMessage());
				Logger.error("perfecto response error code : " + conn.getResponseCode());
				Logger.error("Perfecto cloud security token is invalid, use a valid token or create a new token. -\n"
						+ " -  for any help, hotline : abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
						+ " - any clarification / enhancement request : Central Test Management (CTM)");
				System.exit(0);
			}
			Logger.error("perfecto response error message:" + conn.getResponseMessage());
			Logger.error("perfecto response error code : " + conn.getResponseCode());
			Logger.error("Error while geeting perfecto response from perfecto cloud. -\n"
					+ " -  for any help, hotline : abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
					+ " - any clarification / enhancement request : Central Test Management (CTM)");
			System.exit(0);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		String response = "";

		while ((output = br.readLine()) != null) {
			response = response + output;

		}
		conn.disconnect();
		writePerfectoReport(response);
		return response;

	}

	public static String getTagsUrl(String tagsString) {

		// String demo="Nightly,1.11.0(15169),Adriana
		// Panaete,Epic_Subaccount_EN,Banking";
		String[] tagsArray = tagsString.split(",");
		String response = "";

		for (int i = 0; i < tagsArray.length; i++) {
			response = response + "tags[" + Integer.toString(i) + "]=" + tagsArray[i];
			response = response + "&";
		}

		return response.substring(0, response.length() - 1);
	}


	public static void writePerfectoReport(String response) {
		BufferedWriter bWriter = null;
		File reportFile = new File(SystemHelper.getCurrentDirectory() + "//perfecto_report.json");
		if (reportFile.exists()) {
			reportFile.delete();
			try {
				reportFile.createNewFile();
			} catch (IOException e) {
				Logger.warn("Error while deleteing exisiting perfecto report file");
				Logger.error(e);
				System.exit(0);
			}
		}

		try {
			bWriter = new BufferedWriter(new FileWriter(reportFile));
			bWriter.write(response);
			bWriter.close();
		} catch (IOException e) {
			Logger.warn("Error while writing report to perfecto report file");
			Logger.error("Please analyse the perfecto url tags. -\n"
					+ " -  for any help, hotline : abhinaba.ghosh@db.com / jamal.tikniouine@db.com\n"
					+ " - any clarification / enhancement request : Central Test Management (CTM)");
			Logger.error(e);
			System.exit(0);
		}

	}

}
