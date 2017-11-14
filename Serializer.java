import java.lang.reflect.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

public class Serializer {
	
	public static void main(String[] args) {
		
		
		Object obj = userChoices();
		Document xml = serialize(obj);	
	}
	
	public static Object userChoices() {
		
		Object object = new Object();
		
		System.out.printf("Select what kind of object you'd like to create:\n"
				+ "\t1. A simple object with only primitives for variables\n"
//				+ "\t2. An object that contains a reference to another object\n"
//				+ "\t3. An object that contains an array of primitives\n"
//				+ "\t4. An object that contains an array of object references\n"
//				+ "\t5. An object that uses an instance of Java's collection classes to refer to several other objects"
				);
			
		Scanner keyboard = new Scanner(System.in);
		int choice = keyboard.nextInt();
		
		switch (choice) {
		case 1:
			object = new PrimitiveClass();
			object = editPrimitiveClass(object);
			
			break;

		default:
			System.out.println("Invalid choice");
			System.exit(0);
			break;
		}
		
		return object;
	}
	
	public static Object editPrimitiveClass(Object object) {
		
		PrimitiveClass primitiveClass = new PrimitiveClass();
		
		return new PrimitiveClass();
	}
	
	public static org.jdom2.Document serialize(Object obj){
		
		Document doc = new Document();
		
		//set root
		Element rootElement = new Element("serialized");
		doc.setRootElement(rootElement);
		
		Element testElement = createPrimitiveClassElement(obj);
		rootElement.addContent(testElement);
		
		
		//write to file for viewing
		try {
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileOutputStream(new File("serialize.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		
		return doc;
	}
	
	public static org.jdom2.Element createPrimitiveClassElement(Object obj){
		
		//create object element with class name and id attribute
		Element objectElement = new Element("object");
		Attribute className = new Attribute("name", obj.getClass().getName());
		Attribute classID = new Attribute("id", "WIP");
		objectElement.setAttribute(className).setAttribute(classID);
		
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
			objectElement.addContent(field);
			
		}
		
		return objectElement;
	}
}
