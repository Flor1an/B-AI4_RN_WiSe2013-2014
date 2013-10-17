package client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MailWriter {
	private static FileWriter writer;


	public static void writeMail(String fileName, String inPut) throws IOException {

		
		
		String convert = "MAIL "+fileName.concat(".txt");
		File file = new File(convert);
		
		
		writer = new FileWriter(file, true);
		writer.append(inPut);
		writer.append('\n');
		writer.flush();
		writer.close();
	}
}
