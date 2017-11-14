import java.lang.reflect.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Serializer {
	public static void main(String[] args) {
		
		Object obj = createPrimitiveObject();
		Document xml = serialize(obj);
		
		

		
	}
	
	public static Object createPrimitiveObject() {
		
		return new PrimitiveClass();
	}
	
	public static org.jdom2.Document serialize(Object obj){
		
		Document doc = new Document();
		
		//set root
		Element theRoot = new Element("serialized");
		doc.setRootElement(theRoot);
		
		//create object element with class name and id attribute
		Element object = new Element("object");
		Attribute className = new Attribute("name", obj.getClass().getName());
		Attribute classID = new Attribute("id", "WIP");
		object.setAttribute(className).setAttribute(classID);
		theRoot.addContent(object);
		
		//add fields
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			String declaringClass = fields[i].getDeclaringClass().getName();
			String fieldValue;
			try {
				fieldValue = fields[i].get(obj).toString();
				System.out.println(fieldValue);
			} catch (IllegalAccessException e) {
				fieldValue = "ErrorAccessingString";
			}
			Element field = new Element("field");
			field.setAttribute("name", fieldName).setAttribute("declaringclass", declaringClass);
			Element value = new Element("value");
			value.addContent(fieldValue);
			field.addContent(value);
			object.addContent(field);
			
		}
		
		//write to file for viewing
		try {
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileOutputStream(new File("serialize.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		
		return doc;
	}
}
