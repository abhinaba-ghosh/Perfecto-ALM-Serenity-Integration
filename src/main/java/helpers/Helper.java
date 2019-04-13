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



public class Helper {

	
	
	public Helper() {
		
	}
	
	public static String getCurrentDirectory() {
		String current = "";
		
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			current=s;
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		} catch(Exception e) {
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
		}catch(UnknownHostException e) {
			
		}
		
		return hostName;
	}
	
	public static String getOwnMachineAddress() {
		String hostName = "";
		
		java.net.InetAddress localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			hostName = localMachine.getHostAddress();
		}catch(UnknownHostException e) {
			
		}
		
		return hostName;
	}
	
	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parentDirectoryName) throws Exception {
        if (fileToZip == null || !fileToZip.exists()) {
            return;
        }

        String zipEntryName = fileToZip.getName();
        if (parentDirectoryName!=null && !parentDirectoryName.isEmpty()) {
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
            }
            else
            {
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
		}
		catch(Exception e) {
			String msg = e.getMessage();
			Helper.log("File error: " + msg); 
		}
		
		return props;
	}
	
}
