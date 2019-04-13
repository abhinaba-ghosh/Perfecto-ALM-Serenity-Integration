package schemas.wrapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestDesignWrapper {

	public static String prepareXml(String parentTestId, String stepName, String description) throws Exception {

		List<Field> field = new ArrayList<Field>();

		field.add(new Field("parent-id", parentTestId));
		field.add(new Field("description", description));
		field.add(new Field("name", stepName));

		Entity entity = new Entity("design-step", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();

	}

}
