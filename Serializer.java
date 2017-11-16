import java.lang.reflect.*;
import java.io.*;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.IdentityHashMap;
import java.net.*;

public class Serializer {
	
	static int SOCKETNUMBER = 1999;
	static IdentityHashMap IDMap = new IdentityHashMap<>();
	
	public static void main(String[] args) {
		
		//create objects as per user choice
		List<Object> objList = userChoices();
		//add to identity map
		for (Object obj : objList) {
			addToMap(obj);
		}
		//serialize
		Document xml = serialize(objList);	
		//write to file for viewing
		writeDocument(xml);
		//prompt user to start deserializer
		System.out.printf("About to send document over network through socket %d\n"
				+ "please make sure deserializer is running and then press enter\n(ENTER)\n", SOCKETNUMBER);
		Scanner keyboard = new Scanner(System.in);
		keyboard.nextLine();
		//send document
//		sendDocument(1999);
	}
	
	public static List<Object> userChoices() {
		
		List<Object> objectList = new ArrayList<Object>();
		
		System.out.printf("Select what kind of object you'd like to create:\n"
				+ "\t1. A simple object with only primitives for variables\n"
				+ "\t2. An object that contains a reference to another object\n"
				+ "\t3. An object that contains an array of primitives\n"
//				+ "\t4. An object that contains an array of object references\n"
//				+ "\t5. An object that uses an instance of Java's collection classes to refer to several other objects\n
				);
		
		System.out.printf("Choose: ");
			
		Scanner keyboard = new Scanner(System.in);
		int choice = keyboard.nextInt();
		
		switch (choice) {
		case 1:
			objectList.add(createPrimitiveClass());
			break;
			
		case 2:
			System.out.printf("Creating object with reference to another class\nPress enter to configure referenced class\n(ENTER)\n");
			keyboard = new Scanner(System.in);
			keyboard.nextLine();
			objectList.add(createPrimitiveClass());
			objectList.add(new ObjectReferenceClass(objectList.get(0)));
			break;
			
		case 3:
			objectList.add(createPrimitiveArrayClass());
			break;
			
		default:
			System.out.println("Invalid choice");
			System.exit(0);
			break;
		}
		
		return objectList;
	}
	
	public static Object createPrimitiveArrayClass() {
		
		System.out.println("Enter a line of characters. These characters will be added to the char array");
		Scanner keyboard = new Scanner(System.in);
		String input = keyboard.nextLine();
		PrimitiveArrayClass primitiveArrayClass = new PrimitiveArrayClass(input.toCharArray());
		return primitiveArrayClass;
	}
	
	public static Object createPrimitiveClass() {
		
		PrimitiveClass primitiveClass = new PrimitiveClass();
		
		boolean editing = true;
		
		while (editing) {
			System.out.printf("Would you like to edit any of the fields?\n"
					+ "\t1. boolean booleanPrimitive\t= %s\n"
					+ "\t2. char charPrimitive\t\t= %s\n"
					+ "\t3. byte bytePrimitive\t\t= %d\n"
					+ "\t4. short shortPrimitive\t\t= %d\n"
					+ "\t5. int intPrimitive\t\t= %d\n"
					+ "\t6. long longPrimitive\t\t= %d\n"
					+ "\t7. float floatPrimitive\t\t= %f\n"
					+ "\t8. double doublePrimitive\t= %f\n"
					+ "\t9. Done\n"
					, primitiveClass.booleanPrimitive
					, primitiveClass.charPrimitive
					, primitiveClass.bytePrimitive
					, primitiveClass.shortPrimitive
					, primitiveClass.intPrimitive
					, primitiveClass.longPrimitive
					, primitiveClass.floatPrimitive
					, primitiveClass.doublePrimitive
					);
			
			Scanner keyboard = new Scanner(System.in);
			int choice = 0;
			System.out.printf("Choose: ");
			if (keyboard.hasNextInt()) {
				choice = keyboard.nextInt();
				System.out.printf("Input: ");
			}
			
			switch (choice) {
			case 1:
				if (keyboard.hasNextBoolean())
					primitiveClass.booleanPrimitive = keyboard.nextBoolean();
				else
					System.out.println("Invalid input: not a valid boolean");
				break;
			case 2:
				if (keyboard.hasNext(Pattern.compile(".")))
					primitiveClass.charPrimitive = keyboard.next().charAt(0);
				else
					System.out.println("Invalid input: not a valid char");
				break;
			case 3:
				if (keyboard.hasNextByte())
					primitiveClass.bytePrimitive = keyboard.nextByte();
				else
					System.out.println("Invalid input: not a valid byte");
				break;
			case 4:
				if (keyboard.hasNextShort())
					primitiveClass.shortPrimitive = keyboard.nextShort();
				else
					System.out.println("Invalid input: not a valid short");
				break;
			case 5:
				if (keyboard.hasNextInt())
					primitiveClass.intPrimitive = keyboard.nextInt();
				else
					System.out.println("Invalid input: not a valid int");
				break;
			case 6:
				if (keyboard.hasNextLong())
					primitiveClass.longPrimitive = keyboard.nextLong();
				else
					System.out.println("Invalid input: not a valid long");
				break;
			case 7:
				if (keyboard.hasNextFloat())
					primitiveClass.floatPrimitive = keyboard.nextFloat();
				else
					System.out.println("Invalid input: not a valid float");
				break;
			case 8:
				if (keyboard.hasNextDouble())
					primitiveClass.doublePrimitive = keyboard.nextDouble();
				else
					System.out.println("Invalid input: not a valid double");
				break;
			case 9:
				editing = false;
				break;
			default:
				System.out.println("Invalid Choice");
				break;
			}
		}
			
		return primitiveClass;
	}
	
	public static org.jdom2.Document serialize(List<Object> objList){
		
		Document doc = new Document();
		
		//set root
		Element rootElement = new Element("serialized");
		doc.setRootElement(rootElement);
		
		for (Object obj : objList) {
			Element objectElement;
			String className = obj.getClass().getName();
			switch (className) {
			case "PrimitiveClass":
				objectElement = createPrimitiveClassElement(obj);
				break;
			
			case "ObjectReferenceClass":
				objectElement = createObjectReferenceClassElement(obj);
				break;
				
			case "PrimitiveArrayClass":
				objectElement = createPrimitiveArrayClassElement(obj);
				break;

			default:
				objectElement = createPrimitiveClassElement(obj);
				System.out.println("Error creating element");
				System.exit(0);
				break;
			}
			
			rootElement.addContent(objectElement);
		}
		
		return doc;
	}

	private static void writeDocument(Document doc) {
		try {
			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileOutputStream(new File("serialize.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static org.jdom2.Element createPrimitiveClassElement(Object obj){
		
		//create object element with class name and id attribute
		Element objectElement = new Element("object");
		Attribute className = new Attribute("name", obj.getClass().getName());
		Attribute classID = new Attribute("id", IDMap.get(obj).toString());
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
//				System.out.println(fieldValue);
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
	
	public static org.jdom2.Element createObjectReferenceClassElement(Object obj){
		
		Element objectElement = new Element("object");
		Attribute className = new Attribute("name", obj.getClass().getName());
		Attribute classID = new Attribute("id", IDMap.get(obj).toString());
		objectElement.setAttribute(className).setAttribute(classID);
		
		//add fields
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			String declaringClass = fields[i].getDeclaringClass().getName();
			Object fieldValue;
			try {
				fieldValue = fields[i].get(obj);
			} catch (IllegalAccessException e) {
				fieldValue = "ErrorAccessingString";
			}
			Element field = new Element("field");
			field.setAttribute("name", fieldName).setAttribute("declaringclass", declaringClass);
			Element value = new Element("reference");
			value.addContent(IDMap.get(fieldValue).toString());
			field.addContent(value);
			objectElement.addContent(field);
			
		}
		
		return objectElement;
	}
	
	public static org.jdom2.Element createPrimitiveArrayClassElement(Object obj){
		
		Element objectElement = new Element("object");
		Attribute className = new Attribute("name", obj.getClass().getName());
		Attribute classID = new Attribute("id", IDMap.get(obj).toString());
		objectElement.setAttribute(className).setAttribute(classID);
		
		//add fields
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			String declaringClass = fields[i].getDeclaringClass().getName();
			Object fieldArray;
			try {
				fieldArray = fields[i].get(obj);
			} catch (IllegalAccessException e) {
				fieldArray = "ErrorAccessingString";
			}
			
			Element field = new Element("field");
			field.setAttribute("name", fieldName)
				.setAttribute("declaringclass", declaringClass)
				.setAttribute("length", Integer.toString(Array.getLength(fieldArray)));
			
			Object fieldArrayElement;
	
		    for(int j=0 ;j<Array.getLength(fieldArray); j++) {
		        fieldArrayElement = Array.get(fieldArray, j);
			    Element value = new Element("value");
				value.addContent(fieldArrayElement.toString());
				field.addContent(value);
		    }
			
			
			objectElement.addContent(field);
			
		}
		
		return objectElement;
	}
	
	//returns true if successful
	public static boolean sendDocument(int socketNumber) {
		
		try {
			Socket s = new Socket("localhost", socketNumber);
			BufferedReader br = new BufferedReader(new FileReader("serialize.xml"));
			String k;
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			while ((k = br.readLine()) != null) {
				k += "\n"; //readLine() omits endline characters so add it back
				dos.writeUTF(k);
			}
			
			s.close();
			br.close();
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void addToMap(Object object) {
		
		if (IDMap.get(object) == null) {
			IDMap.put(object, IDMap.size());
		}
	}
}
