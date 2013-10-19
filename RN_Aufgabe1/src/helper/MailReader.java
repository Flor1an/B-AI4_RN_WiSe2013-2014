package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class MailReader {
	private ArrayList<String> mails = new ArrayList<>();
	private ArrayList<String> names = new ArrayList<>();
	private ArrayList<String> toDelete = new ArrayList<>();

	final File folder = new File("mails");

	@SuppressWarnings("resource")
	public MailReader() {
		refreshInformation();
		System.out.println(getAllMailAmount());
		System.out.println(getAllMailSize());
	}

	private void refreshInformation() {
		for (final File fileEntry : folder.listFiles()) {
			StringBuffer content = new StringBuffer();
			try {
				FileReader fr = new FileReader(fileEntry.getAbsolutePath());
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();

				while (line != null) {
					content.append(line);
					line = br.readLine();
				}

				br.close();
				mails.add(content.toString());
				names.add(fileEntry.getName().substring(0,
						fileEntry.getName().length() - ".txt".length()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println(mails.toString());
	}

	public int getAllMailAmount() {
		int count = mails.size();

		return count;
	}

	public int getAllMailSize() {
		int count = 0;

		for (String mail : mails) {
			count += mail.length();
		}
		return count;
	}

	public int getSpecificMailSize(int mailId) {
		int count = mails.get(mailId).length();
		return count;
	}

	public String getSpecificMail(int mailId) {

		return mails.get(mailId);
	}

	public String getSpecificUniqueid(int mailId) {

		return names.get(mailId);
	}

	public boolean markAsDeleted(int mailId) {
		return toDelete.add(getSpecificUniqueid(mailId));
	}

	public boolean resetDeletionList() {
		toDelete = new ArrayList<>();
		return true;
	}

	public boolean doDeletion() {
		for (String uid : toDelete) {

			File file = new File(folder + File.separator + uid + ".txt");
	
			file.delete();

		}
		return true;
	}

}
