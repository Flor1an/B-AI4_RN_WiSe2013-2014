package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MailWriter {
	private static FileWriter writer;


	public static void writeMail(String fileName, String inPut) throws IOException {

		(new File("mails")).mkdirs();
		
		String convert = "mails" + File.separator + fileName.concat(".txt");
		File file = new File(convert);
		
		
		writer = new FileWriter(file, true);
		writer.append(inPut);
		writer.append('\n');
		writer.flush();
		writer.close();
	}
	
	public static void writeLog(String fileName, String inPut) throws IOException {

		(new File("logs")).mkdirs();
		
		String convert = "logs" + File.separator + "LOG "+fileName.concat(".txt");
		File file = new File(convert);
		
		
		writer = new FileWriter(file, true);
		writer.append(inPut);
		writer.append('\n');
		writer.flush();
		writer.close();
	}
}
