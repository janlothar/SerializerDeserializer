import java.io.*;
import java.net.*;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Deserializer {
	
	int SOCKETNUMBER = 1999;

	public static void main(String[] args) {
		
//		receiveDocument(SOCKETNUMBER);
		Document xmlDocument = readDocument();
		Object object = deserialize(xmlDocument);
		
//		DEBUG CHECK PRODUCED DOCUMENT
//		try {
//			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
//			xmlOutput.output(xmlDocument, new FileOutputStream(new File("TEST.xml")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
	
	public static Object deserialize(org.jdom2.Document document) {
		
		Element rootSerialized = document.getRootElement();
		
		List<Element> objectSerialized = rootSerialized.getChildren();
		
		List<Element> fieldSerialized = objectSerialized.get(0).getChildren();
		
		String[] fieldValues = new String[fieldSerialized.size()];
		
		PrimitiveClass primitiveclass = new PrimitiveClass();
		
		for (int i = 0; i < fieldValues.length; i++) {
			fieldValues[i] = fieldSerialized.get(i).getChildText("value");
			System.out.println(fieldValues[i]);
		}
		
		primitiveclass.booleanPrimitive = Boolean.parseBoolean(fieldValues[0]);
		primitiveclass.charPrimitive = fieldValues[1].charAt(0);
		primitiveclass.bytePrimitive = Byte.parseByte(fieldValues[2]);
		primitiveclass.shortPrimitive = Short.parseShort(fieldValues[3]);
		primitiveclass.intPrimitive = Integer.parseInt(fieldValues[4]);
		primitiveclass.longPrimitive = Long.parseLong(fieldValues[5]);
		primitiveclass.floatPrimitive = Float.parseFloat(fieldValues[6]);
		primitiveclass.doublePrimitive = Double.parseDouble(fieldValues[7]);
		
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

}
