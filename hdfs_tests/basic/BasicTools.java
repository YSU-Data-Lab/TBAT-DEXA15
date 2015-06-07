package basic;
import java.io.*;
import java.lang.instrument.Instrumentation;

public class BasicTools {
	public static void copyFile(String file_in_name, String file_out_name) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file_in_name));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(file_out_name));
	    int i;
	    do {
	      i = br.read();
	      if (i != -1) {
	        if (Character.isLowerCase((char) i))
	          bw.write(Character.toUpperCase((char) i));
	        else if (Character.isUpperCase((char) i))
	          bw.write(Character.toLowerCase((char) i));
	        else
	          bw.write((char) i);
	      }
	    } while (i != -1);
	    br.close();
	    bw.close();
	}

	private static Instrumentation instrumentation;
	
	public static void premain(String args, Instrumentation inst) {
	        instrumentation = inst;
	}
	 
	public static long getObjectSize(Object o) {
	        return instrumentation.getObjectSize(o);
	}
}
