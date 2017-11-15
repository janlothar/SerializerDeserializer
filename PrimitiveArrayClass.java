
public class PrimitiveArrayClass {

	char[] charArray;
	
	public PrimitiveArrayClass(char[] array) {
		
		charArray = new char[array.length];
		for (int i = 0; i < array.length; i++) {
			charArray[i] = array[i];
		}
	}
}
