import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Deserializer {
	
	static int SOCKETNUMBER = 1999;
	static IdentityHashMap IDMap = new IdentityHashMap();

	public static void main(String[] args) {
		
		System.out.println("Deserializer running. Waiting for Serializer to send document...");
//		receiveDocument(SOCKETNUMBER);
		Document xmlDocument = readDocument();
		System.out.println("Document received. Deserializing...");
		Object object = deserialize(xmlDocument);
		inspect(object);

	}
	
	public static Object deserialize(org.jdom2.Document document) {
		
		Element rootSerialized = document.getRootElement();
		
		List<Element> objectSerialized = rootSerialized.getChildren();
		
		Object deserializedObject = new Object();
		
		for(Element objectElement : objectSerialized) {
			String objectType = objectElement.getAttributeValue("name");
			
			switch (objectType) {
			case "PrimitiveClass":
				deserializedObject = deserializePrimitiveClass(objectElement);
				break;
				
			case "ObjectReferenceClass":
				deserializedObject = deserializeObjectReferenceClass(objectElement);
				break;
				
			case "PrimitiveArrayClass":
				deserializedObject = deserializePrimitiveArrayClass(objectElement);
				break;
				
			case "ObjectReferenceArrayClass":
				deserializedObject = deserializeObjectReferenceArrayClass(objectElement);
				break;
			case "ObjectReferenceCollectionClass":
				deserializedObject = deserializeObjectReferenceCollectionClass(objectElement);
			default:
				return deserializedObject;
			}
		}
		
		return deserializedObject;
		
	}
	
	public static Object deserializeObjectReferenceCollectionClass(Element objectElement) {
		
		List<Element> fieldReferencesSerialized = objectElement.getChild("field").getChildren();
		
		int idValue = Integer.parseInt(objectElement.getAttributeValue("id"));
		
		Object[] objectRefs = new Object[fieldReferencesSerialized.size()];
		
		for (int i = 0; i < objectRefs.length; i++) {
			int value = Integer.parseInt(fieldReferencesSerialized.get(i).getText());
			objectRefs[i] = IDMap.get(value);
		}
		
		Collection<Object> objRefCollection = Arrays.asList(objectRefs);
		
		ObjectReferenceCollectionClass objectColClass = new ObjectReferenceCollectionClass(objRefCollection);
		
		IDMap.put(idValue, objectColClass);
		
		return objectColClass;
	}
	
	public static Object deserializeObjectReferenceArrayClass(Element objectElement) {
		
		List<Element> fieldReferencesSerialized = objectElement.getChild("field").getChildren();
		
		int idValue = Integer.parseInt(objectElement.getAttributeValue("id"));
		
		Object[] objectRefs = new Object[fieldReferencesSerialized.size()];
		
		for (int i = 0; i < objectRefs.length; i++) {
			int value = Integer.parseInt(fieldReferencesSerialized.get(i).getText());
			objectRefs[i] = IDMap.get(value);
		}
		
		ObjectReferenceArrayClass objectArrClass = new ObjectReferenceArrayClass(objectRefs);
		
		IDMap.put(idValue, objectArrClass);
		
		return objectArrClass;
	}
	
	public static Object deserializeObjectReferenceClass(Element objectElement) {
		
		Element fieldSerialized = objectElement.getChild("field");
		
		int idValue = Integer.parseInt(objectElement.getAttributeValue("id"));
		
		int refValue = Integer.parseInt(fieldSerialized.getChildText("reference"));
		
		ObjectReferenceClass objectRefClass = new ObjectReferenceClass(IDMap.get(refValue));
		
		IDMap.put(idValue, objectRefClass);
		
		return objectRefClass;
	}
	
	public static Object deserializePrimitiveArrayClass(Element objectElement) {
		
		List<Element> fieldValuesSerialized = objectElement.getChild("field").getChildren();
		
		int idValue = Integer.parseInt(objectElement.getAttributeValue("id"));
		
		char[] fieldValues = new char[fieldValuesSerialized.size()];
		
		for (int i = 0; i < fieldValues.length; i++) {
			char value = fieldValuesSerialized.get(i).getText().charAt(0);
			fieldValues[i] = value;
		}
		
		PrimitiveArrayClass primitiveArrayClass = new PrimitiveArrayClass(fieldValues);
		
		IDMap.put(idValue, primitiveArrayClass);
		
		return primitiveArrayClass;
	}
	
	public static Object deserializePrimitiveClass(Element objectElement) {
		
		List<Element> fieldSerialized = objectElement.getChildren();
		
		int idValue = Integer.parseInt(objectElement.getAttributeValue("id"));
		
		String[] fieldValues = new String[fieldSerialized.size()];
		
		PrimitiveClass primitiveclass = new PrimitiveClass();
		
		for (int i = 0; i < fieldValues.length; i++) {
			fieldValues[i] = fieldSerialized.get(i).getChildText("value");
		}
		
		primitiveclass.booleanPrimitive = Boolean.parseBoolean(fieldValues[0]);
		primitiveclass.charPrimitive = fieldValues[1].charAt(0);
		primitiveclass.bytePrimitive = Byte.parseByte(fieldValues[2]);
		primitiveclass.shortPrimitive = Short.parseShort(fieldValues[3]);
		primitiveclass.intPrimitive = Integer.parseInt(fieldValues[4]);
		primitiveclass.longPrimitive = Long.parseLong(fieldValues[5]);
		primitiveclass.floatPrimitive = Float.parseFloat(fieldValues[6]);
		primitiveclass.doublePrimitive = Double.parseDouble(fieldValues[7]);
		
		System.out.println("Putting primitive class in " + idValue);
		
		IDMap.put(idValue, primitiveclass);
		
		return primitiveclass;
	}
	
	public static boolean receiveDocument(int socketNumber) {
		
		try {
			
			ServerSocket ss = new ServerSocket(socketNumber);
			Socket s = ss.accept();
			DataInputStream dis = new DataInputStream(s.getInputStream());
			String k;
			FileOutputStream fos = new FileOutputStream("deserialized.xml");
			byte[] b;			
			while ((k = dis.readUTF()) != null) {
				b = k.getBytes();
				fos.write(b);
			}
			ss.close();
			fos.close();
			return true;
		} catch (EOFException e) {
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public static org.jdom2.Document readDocument() {
		
		Document document = new Document();
		try {
			SAXBuilder builder = new SAXBuilder();
			document = builder.build(new File("deserialized.xml"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return document;
	}
	
	public static void inspect(Object obj) {
		
		
		Class classToInspect = obj.getClass();
		
		//get class details
		String className = classToInspect.getName();
		String[] fieldDetails = getFieldDetails(obj);
		
		//print class details
		System.out.println("Class name: " + className);

		//print field details
		System.out.println("\nFields: ");
		if (fieldDetails.length == 0) {
			System.out.println("\tNone");
		} else {
			for (int i = 0; i < fieldDetails.length; i++) {
				System.out.println("\tfield " + (i+1) + ":\t" + fieldDetails[i]);
			}
		}
    }

	public static String[] getFieldDetails(Object toInspect) {
		
		Field[] fields = toInspect.getClass().getDeclaredFields();
		String[] fieldDetails = new String[fields.length];
		
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			String fieldName = fields[i].getName();
			String fieldModifier = Modifier.toString(fields[i].getModifiers());
			String fieldType = fields[i].getType().toString();
			fieldDetails[i] = (fieldModifier + " " + fieldType + " " + fieldName);
			try {
				Object fieldValue = fields[i].get(toInspect);
				fieldDetails[i] += (" = " + fieldValue);
			}catch(IllegalAccessException e) {
				//converting stack trace to string uses code from this tutorial: 
				//	http://www.baeldung.com/java-stacktrace-to-string
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				fieldDetails[i] += (" = IllegalAccessException on this field: " + sw.toString());
			}
		}
		
		return fieldDetails;
	}
}
