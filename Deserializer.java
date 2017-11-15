import java.io.*;
import java.net.*;
import java.lang.reflect.*;
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
		inspect(object);
		
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
	
	public static void inspect(Object obj) {
		
		
		Class classToInspect = obj.getClass();
		
		//get class details
		String className = getClassName(classToInspect);
		String superclassName = getSuperclassName(classToInspect);
		String[] interfaceNames = getInterfaceNames(classToInspect);
		String[] methodDetails = getMethodDetails(classToInspect);
		String[] constructorDetails = getConstructorDetails(classToInspect);
		String[] fieldDetails = getFieldDetails(obj);
		
		//print class details
		System.out.println("Class name: " + className);
		System.out.println("Superclass name: " + superclassName);
		//print interfaces
		System.out.println("\nInterfaces of Class:");
		if (interfaceNames.length == 0) {
			System.out.println("\tNone");
		} else {
			for(int i=0; i<interfaceNames.length; i++) {
				System.out.println("\t" + interfaceNames[i]);
			}
		}
		//print method details
		System.out.println("\nMethods: ");
		if (methodDetails.length == 0) {
			System.out.println("\tNone");
		} else {
			for (int i = 0; i < methodDetails.length; i++) {
				System.out.println("\tmethod " + (i+1) + ":\t" + methodDetails[i]);
			}
		}
		//print constructor details
		System.out.println("\nConstructors: ");
		if (constructorDetails.length == 0) {
			System.out.println("\tNone");
		} else {
			for (int i = 0; i < constructorDetails.length; i++) {
				System.out.println("\tconstructor " + (i+1) + ":\t" + constructorDetails[i]);
			}
		}
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
	
	
	public static String getClassName(Class toInspect) {
		
		String className = toInspect.getName();
		return className;
	}
	
	
	public static String getSuperclassName(Class toInspect) {
		
		Class superclass = toInspect.getSuperclass();
		String superclassName = superclass.getName();
		return superclassName;
	}
	
	
	public static String[] getInterfaceNames(Class toInspect) {
		
		Class[] interfaces = toInspect.getInterfaces();
		String[] interfaceNames = new String[interfaces.length];
		
		if (interfaceNames.length == 0) {
			return interfaceNames;
		} else {
			for(int i=0; i<interfaces.length; i++) {
				interfaceNames[i] = interfaces[i].getName();
			}
			return interfaceNames;
		}
	}
	
	
	public static String[] getMethodDetails(Class toInspect) {

		Method[] methods = toInspect.getDeclaredMethods();
		String[] methodDetails = new String[methods.length];
		
		for (int i=0; i<methods.length; i++) {
			methods[i].setAccessible(true);
			String modifierName = getMethodModiferNames(methods[i]);
			String returnType = getMethodReturnType(methods[i]);
			String methodName = methods[i].getName();
			String[] parameterNames = getMethodParameterNames(methods[i]);
			String formattedParameterNames = formatParameterNames(parameterNames);
			String[] exceptionNames = getMethodExceptionNames(methods[i]);
			String formattedExceptionNames = formatExceptionNames(exceptionNames);
			methodDetails[i] = (modifierName + " " + returnType + " " + methodName + formattedParameterNames + " throws " + formattedExceptionNames);
		}
		
		return methodDetails;
	}
	
	
	public static String[] getConstructorDetails(Class toInspect) {
		
		Constructor[] constructors = toInspect.getDeclaredConstructors();
		String[] constructorDetails = new String[constructors.length];
		
		for (int i = 0; i < constructors.length; i++) {
			constructors[i].setAccessible(true);
			String modifierName = getConstructorModifierNames(constructors[i]);
			String constructorName = constructors[i].getName();
			String[] parameterNames = getConstructorParameterNames(constructors[i]);
			String parameterNamesformatted = formatParameterNames(parameterNames);
			constructorDetails[i] = (modifierName + " " + constructorName + parameterNamesformatted);			
		}
		
		return constructorDetails;
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
	
	
	public static String getMethodReturnType(Method toInspect) {
		
		String returnType = toInspect.getReturnType().getName();
		return returnType;
	}
	
	
	public static String[] getMethodExceptionNames(Method toInspect) {
		
		Class[] exceptionTypes = toInspect.getExceptionTypes();
		return getParameterNames(exceptionTypes);
	}
	
	
	public static String[] getMethodParameterNames(Method toInspect) {
		
		Class[] parameterTypes = toInspect.getParameterTypes();
		return getParameterNames(parameterTypes);
	}
	
	
	public static String[] getConstructorParameterNames(Constructor toInspect) {
		
		Class[] parameters = toInspect.getParameterTypes();
		return getParameterNames(parameters);
	}
	
	
	public static String getMethodModiferNames(Method toInspect) {
		
		int modsEncoded = toInspect.getModifiers();
		return getModifierNames(modsEncoded);
	}
	
	
	public static String getConstructorModifierNames(Constructor toInspect) {
		
		int modsEncoded = toInspect.getModifiers();
		return getModifierNames(modsEncoded);
	}
	

	private static String[] getParameterNames(Class[] parameters) {
		
		String[] parameterNames = new String[parameters.length];
		
		for (int i = 0; i < parameters.length; i++) {
			parameterNames[i] = parameters[i].getName();
		}
		
		return parameterNames;
	}
	

	private static String getModifierNames(int modsEncoded) {
		
		String modifiers = Modifier.toString(modsEncoded);
		return modifiers;
	}
	
	
	public static String formatParameterNames(String[] parameterNames) {
		
		String formattedParameterNames = new String();
		
		if (parameterNames.length == 0) {
			formattedParameterNames = "(No parameters)";
		}
		else {
			formattedParameterNames = "(";
			for(int i=0; i<parameterNames.length; i++) {
				formattedParameterNames += (parameterNames[i] + " param"+(i+1));
				if (i<parameterNames.length-1) {
					formattedParameterNames += ", ";
				}
			}
			formattedParameterNames += ")";
		}
		
		return formattedParameterNames;
	}
	
	
	public static String formatExceptionNames(String[] exceptionNames) {
		
		String formattedExceptionNames = new String();
		
		if (exceptionNames.length == 0) {
			formattedExceptionNames= "no exceptions";
		} else {
			for (int i = 0; i < exceptionNames.length; i++) {
				formattedExceptionNames += exceptionNames[i];
				if (i<exceptionNames.length-1) {
					formattedExceptionNames += ", ";
				}
			}
		}
		
		return formattedExceptionNames;
	}

}
