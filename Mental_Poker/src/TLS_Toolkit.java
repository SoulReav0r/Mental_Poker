
public class TLS_Toolkit {

	String X509path = "D:/JavaWorkspace/Mental_Poker/Mental_Poker/src"; //path to X509-Certificate without "/" at the end
	String ServerPass = "serverpw"; // passphrase for accessing server keystore
	String ClientPass = "clientpw"; // passphrase for accessing client keystore

	   /**
	   * Depending on used constructor client or server got instanced 
	   */
	public TLS_Toolkit(int port) {

		new TLS_Server(port, X509path, ServerPass);

	}

	public TLS_Toolkit(String host, int port) {

		new TLS_Client(host, port, X509path, ClientPass);

	}

	public static void ConnectionTest() {

		TLS_Toolkit Server = new TLS_Toolkit(4242);
		TLS_Toolkit Client = new TLS_Toolkit("localhost", 4242);
	}
	
	  static public void main( String args[] ) {
		  ConnectionTest();
	  }

}
