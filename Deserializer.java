import java.io.*;
import java.net.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class Deserializer {

	public static void main(String[] args) {
		receiveDocument(1999);
		Document xmlDocument = readDocument();

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
