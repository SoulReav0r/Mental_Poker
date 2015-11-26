import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.net.ssl.*;

public class TLS_Server implements Runnable
{
	
	String X509path = "";
  /**
   * The port we will listen on
   */
  private int port;

  /**
   * A list of open connections
   */
  private Set connections = new HashSet();

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
   * Passphrase for accessing our authentication keystore
   */
  static private String passphrase = "";

  /**
   * A source of secure random numbers
   */
  static private SecureRandom secureRandom;

  /**
   * Create a Server that listens on the given port.
   * Start the background listening thread
   */
  public TLS_Server( int port, String X509path, String passphrase ) {
    this.port = port;
    this.passphrase = passphrase;
    this.X509path = X509path;
    secureRandom = new SecureRandom();
    secureRandom.nextInt();
    	
    new Thread( this ).start();
  }

  private void setupClientKeyStore() throws GeneralSecurityException, IOException {
    clientKeyStore = KeyStore.getInstance( "JKS" );
    clientKeyStore.load( new FileInputStream( X509path+"/client.public" ),
                       "public".toCharArray() );
  }

  private void setupServerKeystore() throws GeneralSecurityException, IOException {
    serverKeyStore = KeyStore.getInstance( "JKS" );
    serverKeyStore.load( new FileInputStream( X509path+"/server.private" ),
                        passphrase.toCharArray() );
  }

  private void setupSSLContext() throws GeneralSecurityException, IOException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
    tmf.init( clientKeyStore );

    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( serverKeyStore, passphrase.toCharArray() );

    sslContext = SSLContext.getInstance( "TLS" );
    sslContext.init( kmf.getKeyManagers(),
                     tmf.getTrustManagers(),
                     secureRandom );
  }

  /**
   * Background thread: accept new connections
   */
  public void run() {
    try {
      setupClientKeyStore();
      setupServerKeystore();
      setupSSLContext();

      SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
      SSLServerSocket ss = (SSLServerSocket)sf.createServerSocket( port );

      // Require client authorization
      ss.setNeedClientAuth( true );

      System.out.println( "Listening on port "+port+"..." );
      while (true) {
        Socket socket = ss.accept();
        System.out.println( "Got connection from "+socket );

        TLS_ConnectionProcessor cp = new TLS_ConnectionProcessor( this, socket );
        connections.add( cp );
      }
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  /**
   * Remove a connection that has been closed from our set
   * of open connections
   */
  void removeConnection( TLS_ConnectionProcessor cp ) {
    connections.remove( cp );
  }

  /**
   * Return an iteration over open connections
   */
  Iterator getConnections() {
    return connections.iterator();
  }

  /**
   * Create and start a Server.  The port number must
   * be provided on the command line
   */
  static public void main( String args[] ) {

  }
}
