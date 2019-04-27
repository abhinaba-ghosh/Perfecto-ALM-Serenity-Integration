package schemas.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="Field")
@XmlAccessorType(XmlAccessType.NONE)
public class Field {
	
	@XmlAttribute (name="Name")
	private String Name;
	
	@XmlElement (nillable=true)
	private String Value;

	public Field() {
		
	}
	
	public Field(String Name,String Value) {
		this.Name=Name;
		this.Value=Value;
	}
}
