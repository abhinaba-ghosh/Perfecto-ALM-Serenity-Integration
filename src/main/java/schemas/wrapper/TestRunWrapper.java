package schemas.wrapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestRunWrapper {

	public static String prepareXml(String cycleId, String testId, String testCycleId, String hostName,
			String executionDate, String status, String owner, String runName, String OSName, String subTypeId,
			String runDuration) throws Exception {

		List<Field> field = new ArrayList<Field>();

		field.add(new Field("execution-date", executionDate));
		field.add(new Field("user-template-02", "Internal"));
		field.add(new Field("cycle-id", cycleId));
		field.add(new Field("host", hostName));
		field.add(new Field("status", status));
		field.add(new Field("test-id", testId));
		field.add(new Field("owner", owner));
		field.add(new Field("name", runName));
		field.add(new Field("testcycl-id", testCycleId));
		field.add(new Field("os-name", OSName));
		field.add(new Field("subtype-id", subTypeId));
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
