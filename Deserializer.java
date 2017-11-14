import java.io.*;
import java.net.*;

public class Deserializer {

	public static void main(String[] args) {
		receiveDocument(1999);

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

}
