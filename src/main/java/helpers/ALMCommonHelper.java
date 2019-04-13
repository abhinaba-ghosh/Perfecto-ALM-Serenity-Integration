package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.pmw.tinylog.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import infrastructure.AlmConnector;
import infrastructure.Constants;
import infrastructure.Response;
import infrastructure.RestConnector;

public class ALMCommonHelper {

	public ALMCommonHelper() {

	}

	public static String getNodeValueFromXmlString(String xmlString, String nodeName)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

		String test_id = "";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = new InputSource();
		src.setCharacterStream(new StringReader(xmlString));

		Document doc = builder.parse(src);
		XPath path = XPathFactory.newInstance().newXPath();
		NodeList node = (NodeList) path.compile("//Field[@Name=" + "\"" + nodeName + "\"" + "]/Value/text()")
				.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < node.getLength(); i++) {
			test_id = node.item(i).getTextContent();
		}

		return test_id;

	}

	public static String pushRequestMessageToALM(String xmlContent, String almEntity) throws Exception {

		String newlyCretedResourceId = "NO_ID_GENERATED";

		AlmConnector alm = new AlmConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(almEntity);
		Logger.info("testconfig url:" + testconfigurl);

		Logger.info("request xml: " + xmlContent);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpPost(testconfigurl, xmlContent.getBytes(), requestHeaders);

		Logger.info("postedEntityResponse.getStatusCode():" + postedEntityResponse.getStatusCode());

		if ((postedEntityResponse.getStatusCode() == 200) || (postedEntityResponse.getStatusCode() == 201)) {

			newlyCretedResourceId = ALMCommonHelper.getNodeValueFromXmlString(postedEntityResponse.toString(), "id");
			Logger.info("Newly created resource id is:" + newlyCretedResourceId);

		} else {
			Logger.error("response: " + postedEntityResponse.getFailure());
			Logger.error("error response: " + postedEntityResponse.toString());

		}

		alm.logout();
		alm = null;

		return newlyCretedResourceId;
	}

	public static String isResouceAlreadyAvailable(String queryParam) throws Exception {

		String responseString = "";

		AlmConnector alm = new AlmConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(queryParam);
		Logger.info("testconfig url:" + testconfigurl);
		URL url = new URL(testconfigurl);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), url.getRef());
		testconfigurl = uri.toASCIIString();
		// System.out.println("new testconfig url:" + testconfigurl);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpGet(testconfigurl, null, requestHeaders);

		// Logger.info("get response: " + postedEntityResponse.toString());

		if (postedEntityResponse.toString().contains("TotalResults=\"0\"")) {
			responseString = "No_ID_FOUND";
		} else if ((postedEntityResponse.getStatusCode() == 200) || (postedEntityResponse.getStatusCode() == 201)) {

			String ResourceId = ALMCommonHelper.getNodeValueFromXmlString(postedEntityResponse.toString(), "id");
			Logger.info("resource already present with id:" + ResourceId);
			responseString = ResourceId;

		} else {
			Logger.error("response: " + postedEntityResponse.getFailure());
			Logger.error("error response: " + postedEntityResponse.toString());
			responseString = "ERROR_OCCURED";

		}

		alm.logout();
		alm = null;

		return responseString;
	}

	public static String getResponse(String queryParam) throws Exception {

		AlmConnector alm = new AlmConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(queryParam);
		Logger.info("testconfig url:" + testconfigurl);
		URL url = new URL(testconfigurl);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), url.getRef());
		testconfigurl = uri.toASCIIString();

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpGet(testconfigurl, null, requestHeaders);

		Logger.info("get response: " + postedEntityResponse.toString());

		return postedEntityResponse.toString();

	}

	public static void uploadFileAttachmentToTestRun(String runID, String filePathToAttach) throws Exception {

		String boundary = "" + System.currentTimeMillis();
		final String LINE_FEED = "\r\n";
		String filePath = filePathToAttach;

		AlmConnector alm = new AlmConnector();

		RestConnector con = RestConnector.getInstance();
		con.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		con.getQCSession();
		String runAttachmentURL = "runs/" + runID + "/attachments";
		String attachmentsRunStepsURL = con.buildEntityCollectionUrl(runAttachmentURL);
		Logger.info("testconfig url:" + attachmentsRunStepsURL);

		try {

			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(Constants.SECURE_PROXY_HOST, Constants.SECURE_PROXY_PORT));
			URL urlUpdateRunStepsURL = new URL(attachmentsRunStepsURL);
			HttpURLConnection httpConn = (HttpURLConnection) urlUpdateRunStepsURL.openConnection(proxy);

			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true); // indicates POST method
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Cookie", con.getCookieString());

			httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			File uploadFile = new File(filePath);
			OutputStream outputStream = httpConn.getOutputStream();

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
			String fileName = uploadFile.getName();

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"filename\"").append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.append(fileName).append(LINE_FEED);
			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"")
					.append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.flush();
			FileInputStream inputStream = new FileInputStream(uploadFile);
			byte[] byteSteam = new byte[inputStream.available()];
			inputStream.read(byteSteam);
			outputStream.write(byteSteam);
			outputStream.flush();
			inputStream.close();
			writer.append(LINE_FEED);
			writer.append("--" + boundary + "--");
			writer.flush();
			writer.close();
			System.out.println();
			int status = httpConn.getResponseCode();
			Logger.info("Status code : " + status);

			if (status == HttpURLConnection.HTTP_CREATED) {
				Logger.info("Successfully created");
				httpConn.disconnect();
			} else {

				Logger.warn("Attachment Not created. Please verify the RunID and the resource file path");
				httpConn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		alm.logout();
		alm = null;
	}

	public static void attachURLtoALMRunID(String runID, String link, String attahmentName) throws Exception {

		String boundary = "" + System.currentTimeMillis();
		final String LINE_FEED = "\r\n";
		String atchName = attahmentName + ".url";

		AlmConnector alm = new AlmConnector();

		RestConnector con = RestConnector.getInstance();
		con.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		con.getQCSession();
		String runAttachmentURL = "runs/" + runID + "/attachments";
		String attachmentsRunStepsURL = con.buildEntityCollectionUrl(runAttachmentURL);
		Logger.info("testconfig url:" + attachmentsRunStepsURL);

		try {

			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(Constants.SECURE_PROXY_HOST, Constants.SECURE_PROXY_PORT));
			URL urlUpdateRunStepsURL = new URL(attachmentsRunStepsURL);
			HttpURLConnection httpConn = (HttpURLConnection) urlUpdateRunStepsURL.openConnection(proxy);

			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true); // indicates POST method
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Cookie", con.getCookieString());

			httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			OutputStream outputStream = httpConn.getOutputStream();

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"filename\"").append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.append(atchName).append(LINE_FEED);
			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.append(link).append(LINE_FEED);

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + atchName + "\"")
					.append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.flush();
			writer.append("[InternetShortcut]\r\n" + "URL=" + link).append(LINE_FEED);
			outputStream.flush();
			writer.append(LINE_FEED);
			writer.append("--" + boundary + "--");
			writer.flush();
			writer.close();
			System.out.println();
			int status = httpConn.getResponseCode();
			Logger.info("Status code : " + status);
			Logger.info("message : " + httpConn.getResponseMessage());

			if (status == HttpURLConnection.HTTP_CREATED) {
				Logger.info("Successfully created");
				httpConn.disconnect();
			} else {

				Logger.warn(
						"Attachment Not created. Please verify the RunID and the url description. Both must be valid");
				httpConn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		alm.logout();
		alm = null;

	}

	public static String deleteResource(String queryParam) throws Exception {

		AlmConnector alm = new AlmConnector();

		RestConnector conn = RestConnector.getInstance();
		conn.init(new HashMap<String, String>(), Constants.HOST, Constants.DOMAIN, Constants.PROJECT);

		alm.login(Constants.USERNAME, Constants.PASSWORD);

		conn.getQCSession();
		String testconfigurl = conn.buildEntityCollectionUrl(queryParam);
		Logger.info("testconfig url:" + testconfigurl);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("Accept", "application/xml");
		Response postedEntityResponse = conn.httpDelete(testconfigurl, requestHeaders);

		Logger.info("get response: " + postedEntityResponse.toString());

		return postedEntityResponse.toString();

	}

}
