import java.lang.reflect.*;
import java.io.*;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.IdentityHashMap;
import java.net.*;

public class Serializer {
	
	static int SOCKETNUMBER = 1999;
	static IdentityHashMap IDMap = new IdentityHashMap<>();
	
	public static void main(String[] args) {
		
		
		Object obj = userChoices();
		Document xml = serialize(obj);	
		System.out.printf("About to send document over network through socket %d\n"
				+ "please make sure deserializer is running and then press ENTER\n", SOCKETNUMBER);
		Scanner keyboard = new Scanner(System.in);
		keyboard.nextLine();
		sendDocument(1999);
	}
	
	public static Object userChoices() {
		
		Object object = new Object();
		
		System.out.printf("Select what kind of object you'd like to create:\n"
				+ "\t1. A simple object with only primitives for variables\n"
//				+ "\t2. An object that contains a reference to another object\n"
//				+ "\t3. An object that contains an array of primitives\n"
//				+ "\t4. An object that contains an array of object references\n"
//				+ "\t5. An object that uses an instance of Java's collection classes to refer to several other objects\n
				);
		
		System.out.printf("Choose: ");
			
		Scanner keyboard = new Scanner(System.in);
		int choice = keyboard.nextInt();
		
		switch (choice) {
		case 1:
			object = editPrimitiveClass(new PrimitiveClass());
			addToMap(object);
			break;

		default:
			System.out.println("Invalid choice");
			System.exit(0);
			break;
		}
		
		return object;
	}
	
	public static Object editPrimitiveClass(PrimitiveClass primitiveClass) {
		
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
