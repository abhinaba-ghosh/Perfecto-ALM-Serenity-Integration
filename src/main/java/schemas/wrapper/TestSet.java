package schemas.wrapper;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import infrastructure.Constants;
import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestSet extends Constants{

	/**
	 * @param parentFolderId
	 * @param TestSetName
	 * @param applicationName
	 * @param TestPhase
	 * @param TestStatus
	 * @param description
	 * @return
	 * @throws JAXBException
	 */
	public static String prepareXml(String parentFolderId, String TestSetName, String applicationName, String TestPhase,
			String TestStatus, String description) throws Exception {

		List<Field> field = new ArrayList<Field>();

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File(test_arg));
		XPath path = XPathFactory.newInstance().newXPath();
		NodeList db_field_name_node = (NodeList) path.compile("//test-set//db_fld_name/text()").evaluate(doc,
				XPathConstants.NODESET);
		NodeList db_field_value_node = (NodeList) path.compile("//test-set//db_fld_value/text()").evaluate(doc,
				XPathConstants.NODESET);

		for (int i = 0; i < db_field_name_node.getLength(); i++) {
			String fld_name = db_field_name_node.item(i).getNodeValue();
			String fld_value = db_field_value_node.item(i).getNodeValue();
			if (fld_value.equalsIgnoreCase("null")) {
				field.add(new Field(fld_name, null));
			} else {
				field.add(new Field(fld_name, fld_value));
			}

		}
		
		field.add(new Field("name", TestSetName));
		field.add(new Field("status", TestStatus));
		field.add(new Field("subtype-id", "hp.qc.test-set.default"));
		field.add(new Field("parent-id", parentFolderId));
		field.add(new Field("description", description));

		Entity entity = new Entity("test-set", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();

	}

}
