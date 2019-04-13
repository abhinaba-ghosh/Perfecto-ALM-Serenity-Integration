package schemas.wrapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestConfigWrapper {

	public static String prepareXml(String parentTsetId, String configName, String owner, String description) throws Exception {

		List<Field> field = new ArrayList<Field>();

		field.add(new Field("parent-id", parentTsetId));
		field.add(new Field("data-state", "0"));
		field.add(new Field("name", configName));
		field.add(new Field("owner", owner));
		field.add(new Field("description", description));

		Entity entity = new Entity("test-config", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();
	}

}
