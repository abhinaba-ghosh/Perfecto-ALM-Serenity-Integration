package schemas.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Fields")
@XmlAccessorType(XmlAccessType.NONE)
public class Fields {

	@XmlElement
	private List<Field> Field;

	public Fields() {

	}

	public Fields(List<Field> Field) {
		this.Field = Field;
	}

}
