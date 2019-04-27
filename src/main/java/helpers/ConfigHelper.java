package helpers;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ConfigHelper {

	public static String getValueFromXml(String xmlPath, String nodeName) throws Exception {

		String value = "";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document doc = builder.parse(new File(xmlPath));
		XPath path = XPathFactory.newInstance().newXPath();
		NodeList node = (NodeList) path.compile("//param[@name=" + "\"" + nodeName + "\"" + "]/text()").evaluate(doc,
				XPathConstants.NODESET);

		for (int i = 0; i < node.getLength(); i++) {
			value = node.item(i).getNodeValue();

		}

		return value;
	}

}
