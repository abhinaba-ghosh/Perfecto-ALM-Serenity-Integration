package schemas.wrapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import schemas.entities.Entity;
import schemas.entities.Field;
import schemas.entities.Fields;

public class TestPlanFolder {
	
	public static String prepareXml(String parentFolderId, String testFolderName, String description) throws Exception {

		List<Field> field = new ArrayList<Field>();

		field.add(new Field("parent-id", parentFolderId));
		field.add(new Field("description", description));
		field.add(new Field("name", testFolderName));

		Entity entity = new Entity("test-folder", new Fields(field));

		JAXBContext jaxbContext = JAXBContext.newInstance(Entity.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(entity, sw);

		return sw.toString();

	}

}
