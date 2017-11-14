import java.io.*;
import java.net.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Deserializer {

	public static void main(String[] args) {
		receiveDocument(1999);
		Document xmlDocument = readDocument();
		
//		DEBUG CHECK PRODUCED DOCUMENT
//		try {
//			XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
//			xmlOutput.output(xmlDocument, new FileOutputStream(new File("TEST.xml")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		deserialize(xmlDocument);

	}
	
	public static void deserialize(org.jdom2.Document document) {
		
		Document readDoc = document;
		
		System.out.println("Root: " + readDoc.getRootElement());
		
		// Gets the text found between the name tags
		
		System.out.println("Value: " + readDoc.getRootElement().getChild("object").getChild("field").getChildText("value"));
		
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
