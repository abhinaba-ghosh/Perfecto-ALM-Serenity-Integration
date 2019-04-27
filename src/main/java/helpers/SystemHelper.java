package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SystemHelper {

	public SystemHelper() {

	}

	public static String getCurrentDirectory() {
		String current = "";

		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			current = s;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return current;
	}

	public static long getMillis() {
		return System.currentTimeMillis();
	}

	public static String getTime() {

		long now = getMillis();

		Date date = new Date(now);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS");
		String dateFormatted = formatter.format(date);

		return dateFormatted;
	}

	public static String getTimestamp() {

		long now = getMillis();

		Date date = new Date(now);
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		String dateFormatted = formatter.format(date);

		return dateFormatted;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void log(String info) {
		String out = getTime() + " " + info;
		System.out.println(out);
	}

	public static String readContentFromFile(String string) {
		String content = "";

		try {

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(string));
			String line;
			while ((line = br.readLine()) != null) {
				content += line + "\r\n";
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			log("Exception: " + msg);
		}

		return content;
	}

	public static void writeToFile(String filenameWithPath, String content) {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(filenameWithPath, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writer.write(content);
		writer.close();
	}

	public static String getUserName() {
		String userName = System.getProperty("user.name");
		return userName;
	}

	public static String getOwnMachineName() {
		String hostName = "";

		java.net.InetAddress localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			hostName = localMachine.getHostName();
		} catch (UnknownHostException e) {

		}

		return hostName;
	}

	public static String getOwnMachineAddress() {
		String hostName = "";

		java.net.InetAddress localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			hostName = localMachine.getHostAddress();
		} catch (UnknownHostException e) {

		}

		return hostName;
	}

	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parentDirectoryName)
			throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parentDirectoryName != null && !parentDirectoryName.isEmpty()) {
			zipEntryName = parentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			// If directory, go recursively down to next directory
			System.out.println("+" + zipEntryName);
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			// If file, add it to the zip file
			System.out.println("   " + zipEntryName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);

			// Check filename: Add all but mp4 files
			if (zipEntryName.contains("mp4") == false) {
				zos.putNextEntry(new ZipEntry(zipEntryName));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			} else {
				System.out.println("MP4 files are not added: " + zipEntryName);
			}
		}
	}

	public static Properties readPropertiesFromFile(String pathToProperties) {

		// Reads all properties files in the given directory

		Properties props = new Properties();

		try {
			InputStream input = new FileInputStream(pathToProperties);
			props.load(input);
		} catch (Exception e) {
			String msg = e.getMessage();
			SystemHelper.log("File error: " + msg);
		}

		return props;
	}
	
	public static void printHelpMessage() {

		 System.out.println("\nta2alm [Version 1.3.0]"
	                + "\nThis JAR manages ALM.Net Operations."
	                + "\r\n\r\n"
	                + "Note: This JAR must be used with a configuration file."
	                + "\r\n\r\n"
	                + "Usage:"
	                + "\r\n"
	                + "java -jar ta2alm.jar [options] [param1] [param2] [param3] [param4] [param5] [param6] [param7]"
	                + "\r\n\r\n"
	                + "Options (optional):"
	                + "\r\n"
	                + "\thelp		Prints this help message."
	                + "\r\n\r\n"
	                + "Parameters:\r\n"
	                + "\tparam1\t[config file path]\r\n"
	                + "\t\tProvide absolute/relative path of config file."
	                + "\r\n\r\n"
	                + "\tparam2\t[alm_user]\r\n"
	                + "\t\tProvide alm user name."
	                + "\r\n\r\n"
	                + "\tparam3\t[alm_password]\r\n"
	                + "\t\tProvide alm password."
	                + "\r\n\r\n"
	                + "\tparam4\t[test_plan_folder_id]\r\n"
	                + "\t\tProvide test plan folder id."
	                + "\r\n\r\n"
	                + "\tparam5\t[test_set_id]\r\n"
	                + "\t\tProvide test set id."
	                + "\r\n\r\n"
	                + "\tparam6\t[test_attachment_type]\r\n"
	                + "\t\tProvide test attachment type\r\n"
	                + "\t\toption 1: \"url\" [in case report is hosted on server]\r\n"
	                + "\t\toption 2: \"file\" [in case report is hosted in local system]"
	                + "\r\n\r\n"
	                + "\tparam7\t[test_attachment]\r\n"
	                + "\t\tProvide test execution \"master\" report url/file path.\r\n"
	                + "\t\toption 1: server master report URL.\r\n"
	                + "\t\toption 2: local machine master report path.\r\n"
	                + "\r\n"
	                + "\t\tNote: If param6 and param7 are not relevant,provide empty string."
	                + "\r\n\r\n"
	                + "Example:\r\n"
	                + "1. java -jar ta2alm.jar \"C:\\\\temp\\\\config.xml\" \"admin\" \"1234\" \"567\" \"456\" \"file\" \"C:\\\\temp\\\\report.xml\"" 
	                + "\r\n\n"
	                + "2. java -jar ta2alm.jar \"C:\\\\temp\\\\config.xml\" \"admin\" \"1234\" \"567\" \"456\" \"url\" \"https://teamcity02/report.html\"" 
	                + "\r\n"
	                + "\nPlease send an email to the CTM|CTS Team cts.ta@db.com for any support request."
	                + "\n(c) A CTM|CTS Product\n");
		System.exit(1);

	}


}

