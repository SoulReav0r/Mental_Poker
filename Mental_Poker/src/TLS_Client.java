import java.io.*;
import java.security.*;
import javax.net.ssl.*;

public class TLS_Client implements Runnable
{
  
   private String X509path = "";
	
  /**
   * Connection to the client
   */
  private DataInputStream din;

  /**
   * Connection to the client
   */
  private DataOutputStream dout;

  /**
   * KeyStore for storing our public/private key pair
   */
  private KeyStore clientKeyStore;
  
  /**
   * KeyStore for storing the server's public key
   */
  private KeyStore serverKeyStore;

  /**
   * Used to generate a SocketFactory
   */
  private SSLContext sslContext;
  
  /**
   * JSON Object for sending
   */
  private JSONObject Message = new JSONObject("{Message : Test}");

    /**
   * Passphrase for accessing our authentication keystore
   */
  static private String passphrase = "clientpw";

  /**
   * A source of secure random numbers
   */
  static private SecureRandom secureRandom;

  public TLS_Client( String host, int port, String X509path, String passphrase ) {
	secureRandom = new SecureRandom();
	secureRandom.nextInt();
	this.X509path = X509path;
    this.passphrase = passphrase;
	connect( host, port );
    new Thread( this ).start();
  }

  private void setupServerKeystore() throws GeneralSecurityException, IOException {
    serverKeyStore = KeyStore.getInstance( "JKS" );
    serverKeyStore.load( new FileInputStream( X509path+"/server.public" ), 
                        "public".toCharArray() );
  }

  private void setupClientKeyStore() throws GeneralSecurityException, IOException {
    clientKeyStore = KeyStore.getInstance( "JKS" );
    clientKeyStore.load( new FileInputStream( X509path+"/client.private" ),
                       passphrase.toCharArray() );
  }

  private void setupSSLContext() throws GeneralSecurityException, IOException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
    tmf.init( serverKeyStore );

    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( clientKeyStore, passphrase.toCharArray() );

    sslContext = SSLContext.getInstance( "TLS" );
    sslContext.init( kmf.getKeyManagers(),
                     tmf.getTrustManagers(),
                     secureRandom );
  }

  private void connect( String host, int port ) {
    try {
      setupServerKeystore();
      setupClientKeyStore();
      setupSSLContext();

      SSLSocketFactory sf = sslContext.getSocketFactory();
      SSLSocket socket = (SSLSocket)sf.createSocket( host, port );

      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      din = new DataInputStream( in );
      dout = new DataOutputStream( out );
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  public void run() {
    try {
      while (true) {      
    	//TODO insert protocol
    	  
        	dout.writeUTF(Message.getString("Message")); // TODO Message is send continuously --> better choose non Blocking data structures like Queue
        	System.out.println(din.readUTF()); // print message from server
        	;      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }
  
  static public void main( String args[] ) {

  }

}
