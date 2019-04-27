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

public class TestRun extends Constants{

	/**
	 * @param cycleID
	 * @param testID
	 * @param testCycleId
	 * @param hostName
	 * @param executionDate
	 * @param status
	 * @param owner
	 * @param runName
	 * @param OSName
	 * @param subTypeID
	 * @param runDuration
	 * @return
	 * @throws JAXBException
	 */
	public static String prepareXml(String cycleID, String testID, String testCycleId, String hostName,
			String executionDate, String status, String owner, String runName, String OSName, String subTypeID,
			String runDuration) throws Exception {

		
		List<Field> field = new ArrayList<Field>();
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File(test_arg));
		XPath path = XPathFactory.newInstance().newXPath();
		NodeList db_field_name_node = (NodeList) path.compile("//test_run//db_fld_name/text()").evaluate(doc,
				XPathConstants.NODESET);
		NodeList db_field_value_node = (NodeList) path.compile("//test_run//db_fld_value/text()").evaluate(doc,
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

		field.add(new Field("execution-date", executionDate));
		field.add(new Field("cycle-id", cycleID));
		field.add(new Field("host", hostName));
		field.add(new Field("status", status));
		field.add(new Field("test-id", testID));
		field.add(new Field("owner", owner));
		field.add(new Field("name", runName));
		field.add(new Field("testcycl-id", testCycleId));
		field.add(new Field("os-name", OSName));
		field.add(new Field("subtype-id", subTypeID));
		field.add(new Field("duration", runDuration));

		Entity entity = new Entity("run", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();

	}

}
