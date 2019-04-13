package schemas.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.NONE)
public class Entity {

	@XmlAttribute(name = "Type")
	private String Type;

	@XmlElement
	private Fields Fields;

	public Entity() {

	}

	public Entity(String Type, Fields Fields) {
		this.Type = Type;
		this.Fields = Fields;
	}

}
