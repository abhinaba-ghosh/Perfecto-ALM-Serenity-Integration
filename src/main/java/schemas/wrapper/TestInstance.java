package schemas.wrapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestInstance {

	public static String prepareXml(String testSetId, String testId, String tester, String executionStatus)
			throws Exception {

		List<Field> field = new ArrayList<Field>();

		field.add(new Field("cycle-id", testSetId));
		field.add(new Field("actual-tester", tester));
		field.add(new Field("test-id", testId));
		field.add(new Field("subtype-id", "hp.qc.test-instance.MANUAL"));
		field.add(new Field("owner", tester));
		field.add(new Field("status", executionStatus));

		Entity entity = new Entity("test-instance", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();
	}
	
	public static String prepareStatusXml(String status,String executionDate) throws Exception {
		
		List<Field> field = new ArrayList<Field>();

		
		field.add(new Field("status", status));
		field.add(new Field("exec-date", executionDate));

		Entity entity = new Entity("test-instance", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();
	}
	}

}
