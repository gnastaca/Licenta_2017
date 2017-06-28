package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Helpful {
	
	public static String read(InputStream in) throws IOException{
		 byte[] lungime = new byte[4];
		 in.read(lungime, 0, 4);
		 int data = ByteBuffer.wrap(lungime).getInt();
		 byte recive[] = new byte[data];
		 in.read(recive, 0, data);
		 return new String(recive,"ISO-8859-1");
	}
	
	public static void write(OutputStream out, String string) throws IOException{
		byte stringBytes[] = string.getBytes();
		byte [] len = ByteBuffer.allocate(4).putInt(stringBytes.length).array();
		//System.out.println("*%%%" +string);
		out.write(len,0,4);
		out.write(stringBytes, 0, stringBytes.length);
		//System.out.println("*%%% Am trimis!!!");
	}
	
	public static JSONObject stringToJsonObject(String str){
	   	try {
	       	JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(str);
				return json;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	return null;
	}
}