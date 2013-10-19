package helper;

public class UserData {

	private String userName;
	private String password;
	private String serverAdress;
	private int port;
	private boolean keepCopyOnServer;

	public UserData(String userName, String password, String serverAdress, int port, boolean keepCopyOnServer) {
		this.userName = userName;
		this.password = password;
		this.serverAdress = serverAdress;
		this.port = port;
		this.keepCopyOnServer=keepCopyOnServer;
	}
	
	public UserData(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getServerAdress() {
		return serverAdress;
	}

	public int getPort() {
		return port;
	}
	
	public boolean keepCopyOnServer(){
		return keepCopyOnServer;
	}
	
	
}
